package com.mycompany.ahorropromax.persistencia;

import com.mycompany.ahorropromax.negocio.Asesor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Asesor (empleado del banco).
 *
 * INSERT en dos pasos: persona + asesor (transacción).
 * Métodos:
 *   - insertar(Asesor)
 *   - buscarPorIdentificacion(String)
 *   - listar()
 *   - existeIdentificacion(String)
 *   - eliminar(Long idPersona)
 */
public class AsesorDAO {

    /**
     * Inserta el asesor en una transacción.
     * 1. INSERT persona → obtiene id
     * 2. INSERT asesor → usa el id de persona
     */
    public Asesor insertar(Asesor asesor) {
        String sqlPersona = "INSERT INTO persona (identificacion, nombres, apellidos) " +
                "VALUES (?, ?, ?)";
        String sqlAsesor = "INSERT INTO asesor (id_persona, salario_basico) VALUES (?, ?)";

        Connection cn = null;
        try {
            cn = ConexionDB.getConexion();
            cn.setAutoCommit(false);

            long idPersona;
            try (PreparedStatement ps = cn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, asesor.getIdentificacion());
                ps.setString(2, asesor.getNombres());
                ps.setString(3, asesor.getApellidos());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        cn.rollback();
                        throw new RuntimeException("No se obtuvo id de persona");
                    }
                    idPersona = rs.getLong(1);
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlAsesor)) {
                ps.setLong(1, idPersona);
                ps.setBigDecimal(2, asesor.getSalarioBasico());
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    cn.rollback();
                    throw new RuntimeException("No se pudo insertar asesor");
                }
            }

            cn.commit();
            asesor.setId(idPersona);
            return asesor;

        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignore) { }
            }
            throw new RuntimeException("Error insertando asesor: " + e.getMessage(), e);

        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException ignore) { }
            }
        }
    }

    public Asesor buscarPorIdentificacion(String identificacion) {
        String sql = "SELECT p.id, p.identificacion, p.nombres, p.apellidos, a.salario_basico " +
                "FROM persona p JOIN asesor a ON a.id_persona = p.id " +
                "WHERE p.identificacion = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando asesor: " + e.getMessage(), e);
        }
    }

    public List<Asesor> listar() {
        String sql = "SELECT p.id, p.identificacion, p.nombres, p.apellidos, a.salario_basico " +
                "FROM persona p JOIN asesor a ON a.id_persona = p.id " +
                "ORDER BY p.apellidos, p.nombres";

        List<Asesor> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando asesores: " + e.getMessage(), e);
        }
    }

    public boolean existeIdentificacion(String identificacion) {
        String sql = "SELECT 1 FROM persona WHERE identificacion = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error verificando identificación: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(Long idPersona) {
        String sql = "DELETE FROM persona WHERE id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idPersona);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando asesor: " + e.getMessage(), e);
        }
    }

    private Asesor mapear(ResultSet rs) throws SQLException {
        Asesor a = new Asesor(
                rs.getString("identificacion"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getBigDecimal("salario_basico"));
        a.setId(rs.getLong("id"));
        return a;
    }
}