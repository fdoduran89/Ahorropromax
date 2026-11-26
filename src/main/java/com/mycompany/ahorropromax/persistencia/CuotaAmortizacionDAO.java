package com.mycompany.ahorropromax.persistencia;

import com.mycompany.ahorropromax.negocio.CuotaAmortizacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de CuotaAmortizacion.
 *
 * La tabla se inserta en lote (transacción) para garantizar integridad:
 *   - Si una fila falla, se hace rollback de todas.
 *
 * Métodos:
 *   - insertarLote(List<CuotaAmortizacion>, Long idCredito)
 *   - listarPorCredito(Long idCredito)
 *   - eliminarPorCredito(Long idCredito)
 */
public class CuotaAmortizacionDAO {

    public void insertarLote(List<CuotaAmortizacion> tabla, Long idCredito) {
        if (tabla == null || tabla.isEmpty()) {
            return;
        }
        if (idCredito == null) {
            throw new IllegalArgumentException("El id del crédito es obligatorio");
        }

        String sql = "INSERT INTO cuota_amortizacion " +
                "(credito_id, numero_mes, deuda_inicial, cuota_fija, " +
                "interes_mensual, abono_capital, saldo_pendiente) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection cn = null;
        try {
            cn = ConexionDB.getConexion();
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                for (CuotaAmortizacion c : tabla) {
                    ps.setLong(1, idCredito);
                    ps.setInt(2, c.getNumeroMes());
                    ps.setBigDecimal(3, c.getDeudaInicial());
                    ps.setBigDecimal(4, c.getCuotaFija());
                    ps.setBigDecimal(5, c.getInteres());
                    ps.setBigDecimal(6, c.getAbonoCapital());
                    ps.setBigDecimal(7, c.getSaldoPendiente());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            cn.commit();
            System.out.println("  ✓ " + tabla.size() + " cuotas insertadas para crédito " + idCredito);

        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignore) { }
            }
            throw new RuntimeException("Error insertando tabla de amortización: " + e.getMessage(), e);

        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (SQLException ignore) { }
            }
        }
    }

    public List<CuotaAmortizacion> listarPorCredito(Long idCredito) {
        String sql = "SELECT numero_mes, deuda_inicial, cuota_fija, " +
                "interes_mensual, abono_capital, saldo_pendiente " +
                "FROM cuota_amortizacion WHERE credito_id = ? " +
                "ORDER BY numero_mes";

        List<CuotaAmortizacion> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idCredito);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new CuotaAmortizacion(
                            rs.getInt("numero_mes"),
                            rs.getBigDecimal("deuda_inicial"),
                            rs.getBigDecimal("cuota_fija"),
                            rs.getBigDecimal("interes_mensual"),
                            rs.getBigDecimal("abono_capital"),
                            rs.getBigDecimal("saldo_pendiente")));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando amortización: " + e.getMessage(), e);
        }
    }

    public boolean eliminarPorCredito(Long idCredito) {
        String sql = "DELETE FROM cuota_amortizacion WHERE credito_id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idCredito);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando amortización: " + e.getMessage(), e);
        }
    }
}