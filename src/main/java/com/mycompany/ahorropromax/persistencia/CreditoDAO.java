package com.mycompany.ahorropromax.persistencia;

import com.mycompany.ahorropromax.negocio.Asesor;
import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.Credito;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.negocio.EstadoCredito;
import com.mycompany.ahorropromax.negocio.TipoCredito;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Credito.
 *
 * Métodos:
 *   - insertar(Credito)
 *   - buscarPorId(Long)
 *   - listarPorAsesor(Long)
 *   - listarAprobadosPorPeriodo(String periodo)
 *   - actualizar(Credito)
 *
 * El buscarPorId hace JOIN triple para reconstruir el Crédito completo
 * (empleado corporativo + empresa + asesor).
 */
public class CreditoDAO {

    public Credito insertar(Credito credito) {
        String sql = "INSERT INTO credito " +
                "(empleado_corporativo_id, asesor_id, tipo_credito_id, " +
                "monto_solicitado, plazo_meses, tasa_aplicada, cuota_mensual, " +
                "estado, motivo_rechazo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, credito.getEmpleado().getId());
            ps.setLong(2, credito.getAsesor().getId());
            ps.setInt(3, credito.getTipo().ordinal() + 1); // mapeo 1-index para FK
            ps.setBigDecimal(4, credito.getMontoSolicitado());
            ps.setInt(5, credito.getPlazoMeses());
            ps.setBigDecimal(6, credito.getTasaAplicada());

            if (credito.getCuotaMensual() != null) {
                ps.setBigDecimal(7, credito.getCuotaMensual());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }

            ps.setString(8, credito.getEstado().name());

            if (credito.getMotivoRechazo() != null) {
                ps.setString(9, credito.getMotivoRechazo());
            } else {
                ps.setNull(9, Types.VARCHAR);
            }

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se pudo insertar el crédito");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    credito.setId(rs.getLong(1));
                }
            }
            return credito;

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando crédito: " + e.getMessage(), e);
        }
    }

    public Credito buscarPorId(Long id) {
        String sql = "SELECT c.id AS credito_id, c.monto_solicitado, c.plazo_meses, " +
                "c.tasa_aplicada, c.cuota_mensual, c.estado, c.motivo_rechazo, " +
                "c.tipo_credito_id, " +
                "p.id AS emp_id, p.identificacion AS emp_ident, p.nombres AS emp_nom, " +
                "p.apellidos AS emp_ape, " +
                "e.cargo, e.salario_basico AS emp_salario, " +
                "e.ingresos_mensuales, e.gastos_mensuales, " +
                "cc.id AS corp_id, cc.nit, cc.razon_social, cc.sector, " +
                "cc.direccion, cc.telefono, cc.correo, " +
                "pa.id AS ase_id, pa.identificacion AS ase_ident, " +
                "pa.nombres AS ase_nom, pa.apellidos AS ase_ape, " +
                "a.salario_basico AS ase_salario " +
                "FROM credito c " +
                "JOIN empleado_corporativo e ON e.id_persona = c.empleado_corporativo_id " +
                "JOIN persona p ON p.id = e.id_persona " +
                "JOIN cliente_corporativo cc ON cc.id = e.cliente_corporativo_id " +
                "JOIN asesor a ON a.id_persona = c.asesor_id " +
                "JOIN persona pa ON pa.id = a.id_persona " +
                "WHERE c.id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando crédito por id: " + e.getMessage(), e);
        }
    }

    public List<Credito> listarPorAsesor(Long idAsesor) {
        return listarConFiltro("WHERE c.asesor_id = ?", idAsesor);
    }

    public List<Credito> listarAprobadosPorPeriodo(String periodo) {
        // Convertimos "2026-11" a rango de fechas
        String[] partes = periodo.split("-");
        int anio = Integer.parseInt(partes[0]);
        int mes  = Integer.parseInt(partes[1]);

        String sql = "SELECT c.id AS credito_id, c.monto_solicitado, c.plazo_meses, " +
                "c.tasa_aplicada, c.cuota_mensual, c.estado, c.motivo_rechazo, " +
                "c.tipo_credito_id, " +
                "p.id AS emp_id, p.identificacion AS emp_ident, p.nombres AS emp_nom, " +
                "p.apellidos AS emp_ape, " +
                "e.cargo, e.salario_basico AS emp_salario, " +
                "e.ingresos_mensuales, e.gastos_mensuales, " +
                "cc.id AS corp_id, cc.nit, cc.razon_social, cc.sector, " +
                "cc.direccion, cc.telefono, cc.correo, " +
                "pa.id AS ase_id, pa.identificacion AS ase_ident, " +
                "pa.nombres AS ase_nom, pa.apellidos AS ase_ape, " +
                "a.salario_basico AS ase_salario " +
                "FROM credito c " +
                "JOIN empleado_corporativo e ON e.id_persona = c.empleado_corporativo_id " +
                "JOIN persona p ON p.id = e.id_persona " +
                "JOIN cliente_corporativo cc ON cc.id = e.cliente_corporativo_id " +
                "JOIN asesor a ON a.id_persona = c.asesor_id " +
                "JOIN persona pa ON pa.id = a.id_persona " +
                "WHERE c.estado = 'APROBADO' " +
                "AND YEAR(c.fecha_solicitud) = ? " +
                "AND MONTH(c.fecha_solicitud) = ? " +
                "ORDER BY c.fecha_solicitud";

        List<Credito> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, mes);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando créditos aprobados: " + e.getMessage(), e);
        }
    }

    public boolean actualizar(Credito credito) {
        String sql = "UPDATE credito SET cuota_mensual = ?, estado = ?, motivo_rechazo = ? " +
                "WHERE id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (credito.getCuotaMensual() != null) {
                ps.setBigDecimal(1, credito.getCuotaMensual());
            } else {
                ps.setNull(1, Types.DECIMAL);
            }

            ps.setString(2, credito.getEstado().name());

            if (credito.getMotivoRechazo() != null) {
                ps.setString(3, credito.getMotivoRechazo());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            ps.setLong(4, credito.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando crédito: " + e.getMessage(), e);
        }
    }

    private List<Credito> listarConFiltro(String where, Long param) {
        String sql = "SELECT c.id AS credito_id, c.monto_solicitado, c.plazo_meses, " +
                "c.tasa_aplicada, c.cuota_mensual, c.estado, c.motivo_rechazo, " +
                "c.tipo_credito_id, " +
                "p.id AS emp_id, p.identificacion AS emp_ident, p.nombres AS emp_nom, " +
                "p.apellidos AS emp_ape, " +
                "e.cargo, e.salario_basico AS emp_salario, " +
                "e.ingresos_mensuales, e.gastos_mensuales, " +
                "cc.id AS corp_id, cc.nit, cc.razon_social, cc.sector, " +
                "cc.direccion, cc.telefono, cc.correo, " +
                "pa.id AS ase_id, pa.identificacion AS ase_ident, " +
                "pa.nombres AS ase_nom, pa.apellidos AS ase_ape, " +
                "a.salario_basico AS ase_salario " +
                "FROM credito c " +
                "JOIN empleado_corporativo e ON e.id_persona = c.empleado_corporativo_id " +
                "JOIN persona p ON p.id = e.id_persona " +
                "JOIN cliente_corporativo cc ON cc.id = e.cliente_corporativo_id " +
                "JOIN asesor a ON a.id_persona = c.asesor_id " +
                "JOIN persona pa ON pa.id = a.id_persona " +
                where + " ORDER BY c.fecha_solicitud DESC";

        List<Credito> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando créditos: " + e.getMessage(), e);
        }
    }

    private Credito mapear(ResultSet rs) throws SQLException {
        // 1. Empleado corporativo
        ClienteCorporativo empresa = new ClienteCorporativo(
                rs.getString("nit"),
                rs.getString("razon_social"),
                rs.getString("sector"));
        empresa.setId(rs.getLong("corp_id"));
        empresa.setDireccion(rs.getString("direccion"));
        empresa.setTelefono(rs.getString("telefono"));
        empresa.setCorreo(rs.getString("correo"));

        EmpleadoCorporativo empleado = new EmpleadoCorporativo(
                rs.getString("emp_ident"),
                rs.getString("emp_nom"),
                rs.getString("emp_ape"),
                empresa,
                rs.getString("cargo"),
                rs.getBigDecimal("emp_salario"),
                rs.getBigDecimal("ingresos_mensuales"),
                rs.getBigDecimal("gastos_mensuales"));
        empleado.setId(rs.getLong("emp_id"));

        // 2. Asesor
        Asesor asesor = new Asesor(
                rs.getString("ase_ident"),
                rs.getString("ase_nom"),
                rs.getString("ase_ape"),
                rs.getBigDecimal("ase_salario"));
        asesor.setId(rs.getLong("ase_id"));

        // 3. Tipo de crédito (por ordinal + 1)
        int tipoId = rs.getInt("tipo_credito_id");
        TipoCredito tipo = TipoCredito.values()[tipoId - 1];

        // 4. Crédito
        Credito credito = new Credito(
                empleado, asesor, tipo,
                rs.getBigDecimal("monto_solicitado"),
                rs.getInt("plazo_meses"),
                rs.getBigDecimal("tasa_aplicada"));
        credito.setId(rs.getLong("credito_id"));

        // 5. Estado y cuota (usando setters)
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            credito.setEstado(EstadoCredito.valueOf(estadoStr));
        }
        BigDecimal cuota = rs.getBigDecimal("cuota_mensual");
        if (cuota != null) {
            credito.setCuotaMensual(cuota);
        }
        credito.setMotivoRechazo(rs.getString("motivo_rechazo"));

        return credito;
    }
}