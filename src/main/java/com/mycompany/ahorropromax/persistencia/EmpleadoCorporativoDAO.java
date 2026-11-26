package com.mycompany.ahorropromax.persistencia;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de EmpleadoCorporativo.
 *
 * INSERT en transacción:
 *   1. INSERT en persona → obtenemos id
 *   2. INSERT en empleado_corporativo → usamos el id de persona
 *
 * Si el paso 2 falla, se hace rollback del paso 1.
 *
 * Métodos:
 *   - insertar(EmpleadoCorporativo, Long idEmpresa)
 *   - buscarPorIdentificacion(String)
 *   - listarPorEmpresa(Long)
 *   - actualizar(...)
 *   - eliminar(Long)
 */
public class EmpleadoCorporativoDAO {

    /**
     * Inserta la persona y el empleado corporativo en una sola transacción.
     *
     * @param emp       el empleado corporativo (sin id)
     * @param empresa   la empresa a la que pertenece
     * @return el empleado con id asignado
     */
    public EmpleadoCorporativo insertar(EmpleadoCorporativo emp, ClienteCorporativo empresa) {
        String sqlPersona = "INSERT INTO persona (identificacion, nombres, apellidos) " +
                "VALUES (?, ?, ?)";
        String sqlEmpleado = "INSERT INTO empleado_corporativo " +
                "(id_persona, cliente_corporativo_id, cargo, salario_basico, " +
                "ingresos_mensuales, gastos_mensuales) VALUES (?, ?, ?, ?, ?, ?)";

        Connection cn = null;
        try {
            cn = ConexionDB.getConexion();
            cn.setAutoCommit(false); // inicio de transacción

            // 1. Insertar persona
            long idPersona;
            try (PreparedStatement ps = cn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, emp.getIdentificacion());
                ps.setString(2, emp.getNombres());
                ps.setString(3, emp.getApellidos());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        cn.rollback();
                        throw new RuntimeException("No se obtuvo id de persona");
                    }
                    idPersona = rs.getLong(1);
                }
            }

            // 2. Insertar empleado corporativo
            try (PreparedStatement ps = cn.prepareStatement(sqlEmpleado)) {
                ps.setLong(1, idPersona);
                ps.setLong(2, empresa.getId());
                ps.setString(3, emp.getCargo());
                ps.setBigDecimal(4, emp.getSalarioBasico());
                ps.setBigDecimal(5, emp.getIngresosMensuales());
                ps.setBigDecimal(6, emp.getGastosMensuales());

                int filas = ps.executeUpdate();
                if (filas == 0) {
                    cn.rollback();
                    throw new RuntimeException("No se pudo insertar empleado_corporativo");
                }
            }

            cn.commit();
            emp.setId(idPersona);
            emp.setClienteCorporativo(empresa);
            return emp;

        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignore) { }
            }
            throw new RuntimeException("Error insertando empleado corporativo: " + e.getMessage(), e);

        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException ignore) { }
            }
        }
    }

    public EmpleadoCorporativo buscarPorIdentificacion(String identificacion) {
        String sql = "SELECT p.id, p.identificacion, p.nombres, p.apellidos, " +
                "e.cliente_corporativo_id, e.cargo, e.salario_basico, " +
                "e.ingresos_mensuales, e.gastos_mensuales, " +
                "c.nit, c.razon_social, c.sector, c.direccion, c.telefono, c.correo " +
                "FROM persona p " +
                "JOIN empleado_corporativo e ON e.id_persona = p.id " +
                "JOIN cliente_corporativo c ON c.id = e.cliente_corporativo_id " +
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
            throw new RuntimeException("Error buscando empleado: " + e.getMessage(), e);
        }
    }

    public List<EmpleadoCorporativo> listarPorEmpresa(Long idEmpresa) {
        String sql = "SELECT p.id, p.identificacion, p.nombres, p.apellidos, " +
                "e.cliente_corporativo_id, e.cargo, e.salario_basico, " +
                "e.ingresos_mensuales, e.gastos_mensuales, " +
                "c.nit, c.razon_social, c.sector, c.direccion, c.telefono, c.correo " +
                "FROM persona p " +
                "JOIN empleado_corporativo e ON e.id_persona = p.id " +
                "JOIN cliente_corporativo c ON c.id = e.cliente_corporativo_id " +
                "WHERE e.cliente_corporativo_id = ? " +
                "ORDER BY p.apellidos, p.nombres";

        List<EmpleadoCorporativo> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando empleados: " + e.getMessage(), e);
        }
    }

    public boolean actualizar(EmpleadoCorporativo emp) {
        String sqlPersona = "UPDATE persona SET nombres = ?, apellidos = ? WHERE id = ?";
        String sqlEmpleado = "UPDATE empleado_corporativo SET cargo = ?, salario_basico = ?, " +
                "ingresos_mensuales = ?, gastos_mensuales = ? WHERE id_persona = ?";

        Connection cn = null;
        try {
            cn = ConexionDB.getConexion();
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sqlPersona)) {
                ps.setString(1, emp.getNombres());
                ps.setString(2, emp.getApellidos());
                ps.setLong(3, emp.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlEmpleado)) {
                ps.setString(1, emp.getCargo());
                ps.setBigDecimal(2, emp.getSalarioBasico());
                ps.setBigDecimal(3, emp.getIngresosMensuales());
                ps.setBigDecimal(4, emp.getGastosMensuales());
                ps.setLong(5, emp.getId());
                ps.executeUpdate();
            }

            cn.commit();
            return true;

        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignore) { }
            }
            throw new RuntimeException("Error actualizando empleado: " + e.getMessage(), e);

        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException ignore) { }
            }
        }
    }

    public boolean eliminar(Long idPersona) {
        String sql = "DELETE FROM persona WHERE id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idPersona);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando empleado: " + e.getMessage(), e);
        }
    }

    private EmpleadoCorporativo mapear(ResultSet rs) throws SQLException {
        ClienteCorporativo empresa = new ClienteCorporativo(
                rs.getString("nit"),
                rs.getString("razon_social"),
                rs.getString("sector"));
        empresa.setId(rs.getLong("cliente_corporativo_id"));
        empresa.setDireccion(rs.getString("direccion"));
        empresa.setTelefono(rs.getString("telefono"));
        empresa.setCorreo(rs.getString("correo"));

        EmpleadoCorporativo emp = new EmpleadoCorporativo(
                rs.getString("identificacion"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                empresa,
                rs.getString("cargo"),
                rs.getBigDecimal("salario_basico"),
                rs.getBigDecimal("ingresos_mensuales"),
                rs.getBigDecimal("gastos_mensuales"));
        emp.setId(rs.getLong("id"));
        return emp;
    }
}