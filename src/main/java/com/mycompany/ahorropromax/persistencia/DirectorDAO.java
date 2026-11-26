package com.mycompany.ahorropromax.persistencia;

import com.mycompany.ahorropromax.negocio.Director;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Director (empleado del banco).
 *
 * Estructura idéntica a AsesorDAO, pero apuntando a la tabla 'director'.
 */
public class DirectorDAO {

    public Director insertar(Director director) {
        String sqlPersona = "INSERT INTO persona (identificacion, nombres, apellidos) " +
                "VALUES (?, ?, ?)";
        String sqlDirector = "INSERT INTO director (id_persona, salario_basico) VALUES (?, ?)";

        Connection cn = null;
        try {
            cn = ConexionDB.getConexion();
            cn.setAutoCommit(false);

            long idPersona;
            try (PreparedStatement ps = cn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, director.getIdentificacion());
                ps.setString(2, director.getNombres());
                ps.setString(3, director.getApellidos());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        cn.rollback();
                        throw new RuntimeException("No se obtuvo id de persona");
                    }
                    idPersona = rs.getLong(1);
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlDirector)) {
                ps.setLong(1, idPersona);
                ps.setBigDecimal(2, director.getSalarioBasico());
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    cn.rollback();
                    throw new RuntimeException("No se pudo insertar director");
                }
            }

            cn.commit();
            director.setId(idPersona);
            return director;

        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignore) { }
            }
            throw new RuntimeException("Error insertando director: " + e.getMessage(), e);

        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException ignore) { }
            }
        }
    }

    public Director buscarPorIdentificacion(String identificacion) {
        String sql = "SELECT p.id, p.identificacion, p.nombres, p.apellidos, d.salario_basico " +
                "FROM persona p JOIN director d ON d.id_persona = p.id " +
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
            throw new RuntimeException("Error buscando director: " + e.getMessage(), e);
        }
    }

    public List<Director> listar() {
        String sql = "SELECT p.id, p.identificacion, p.nombres, p.apellidos, d.salario_basico " +
                "FROM persona p JOIN director d ON d.id_persona = p.id " +
                "ORDER BY p.apellidos, p.nombres";

        List<Director> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando directores: " + e.getMessage(), e);
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
            throw new RuntimeException("Error eliminando director: " + e.getMessage(), e);
        }
    }

    private Director mapear(ResultSet rs) throws SQLException {
        Director d = new Director(
                rs.getString("identificacion"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getBigDecimal("salario_basico"));
        d.setId(rs.getLong("id"));
        return d;
    }
}