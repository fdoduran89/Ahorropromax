package com.mycompany.ahorropromax.persistencia;

import com.mycompany.ahorropromax.negocio.Asesor;
import com.mycompany.ahorropromax.negocio.Director;
import com.mycompany.ahorropromax.negocio.Empleado;
import com.mycompany.ahorropromax.negocio.Liquidacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Liquidacion.
 *
 * Detecta automáticamente si el empleado es Asesor o Director
 * para llenar la columna correcta (asesor_id o director_id).
 * El CHECK de la BD garantiza que solo una esté llena.
 */
public class LiquidacionDAO {

    public Liquidacion insertar(Liquidacion liquidacion) {
        Empleado emp = liquidacion.getEmpleado();

        // Determinar el rol
        boolean esAsesor = emp instanceof Asesor;
        boolean esDirector = emp instanceof Director;

        if (!esAsesor && !esDirector) {
            throw new IllegalArgumentException(
                "El empleado debe ser Asesor o Director");
        }

        String sql = "INSERT INTO liquidacion_salario " +
                "(asesor_id, director_id, periodo, salario_basico, retencion, " +
                "total_comisiones, salario_final) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (esAsesor) {
                ps.setLong(1, emp.getId());
                ps.setNull(2, java.sql.Types.BIGINT);
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
                ps.setLong(2, emp.getId());
            }
            ps.setString(3, liquidacion.getPeriodo());
            ps.setBigDecimal(4, liquidacion.getSalarioBasico());
            ps.setBigDecimal(5, liquidacion.getRetencion());
            ps.setBigDecimal(6, liquidacion.getTotalComisiones());
            ps.setBigDecimal(7, liquidacion.getSalarioFinal());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se pudo insertar la liquidación");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    liquidacion.setId(rs.getLong(1));
                }
            }
            return liquidacion;

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando liquidación: " + e.getMessage(), e);
        }
    }

    public Liquidacion buscarPorEmpleadoYPeriodo(Long idPersona, String periodo) {
        String sql = "SELECT l.id, l.periodo, l.salario_basico, l.retencion, " +
                "l.total_comisiones, l.salario_final, " +
                "l.asesor_id, l.director_id " +
                "FROM liquidacion_salario l " +
                "WHERE (l.asesor_id = ? OR l.director_id = ?) " +
                "AND l.periodo = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idPersona);
            ps.setLong(2, idPersona);
            ps.setString(3, periodo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando liquidación: " + e.getMessage(), e);
        }
    }

    public List<Liquidacion> listarPorPeriodo(String periodo) {
        String sql = "SELECT l.id, l.periodo, l.salario_basico, l.retencion, " +
                "l.total_comisiones, l.salario_final, " +
                "l.asesor_id, l.director_id " +
                "FROM liquidacion_salario l " +
                "WHERE l.periodo = ? " +
                "ORDER BY l.id";

        List<Liquidacion> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, periodo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando liquidaciones: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(Long id) {
        String sql = "DELETE FROM liquidacion_salario WHERE id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando liquidación: " + e.getMessage(), e);
        }
    }

    /**
     * Reconstruye una Liquidacion desde el ResultSet.
     * 
     * NOTA: como asesor_id y director_id son excluyentes, aquí solo
     * se crea un Empleado "mock" con los datos que tenemos. En un caso
     * real, se haría un JOIN con persona/asesor/director para reconstruir
     * el Empleado completo. Para el alcance académico, dejamos un Empleado
     * básico con el id, y el llamador puede completarlo si lo necesita.
     */
    private Liquidacion mapear(ResultSet rs) throws SQLException {
        Long asesorId = rs.getObject("asesor_id", Long.class);
        Long directorId = rs.getObject("director_id", Long.class);

        // Crear un Empleado "vacío" solo con id y rol.
        // El servicio puede rehidratarlo con el DAO correspondiente si lo necesita.
        Empleado emp = null;
        if (asesorId != null) {
            Asesor a = new Asesor();
            a.setId(asesorId);
            emp = a;
        } else if (directorId != null) {
            Director d = new Director();
            d.setId(directorId);
            emp = d;
        } else {
            throw new SQLException("Liquidación sin empleado asociado");
        }

        Liquidacion liq = new Liquidacion(
                emp,
                rs.getString("periodo"),
                rs.getBigDecimal("salario_basico"),
                rs.getBigDecimal("retencion"),
                rs.getBigDecimal("total_comisiones"),
                rs.getBigDecimal("salario_final"));
        liq.setId(rs.getLong("id"));
        return liq;
    }
}