package com.mycompany.ahorropromax.core;

import com.mycompany.ahorropromax.negocio.Credito;
import com.mycompany.ahorropromax.negocio.CuotaAmortizacion;
import com.mycompany.ahorropromax.negocio.TipoCredito;
import com.mycompany.ahorropromax.servicios.ResultadoCredito;
import com.mycompany.ahorropromax.servicios.ServicioCredito;

import java.math.BigDecimal;
import java.util.List;

public class MainPruebaServicioCredito {

    public static void main(String[] args) {
        System.out.println("=== Prueba de ServicioCredito ===\n");

        ServicioCredito servicio = new ServicioCredito();

        // --- CASO 1: Crédito aprobado (Vivienda, $10M, 1 año, tasa forzada 5%) ---
        // Nota: usamos Vivienda porque admite 12 meses
        System.out.println("--- CASO 1: Crédito aprobado (Vivienda, $10M, 1 año) ---\n");
        probarCredito(servicio,
                "1020304050",          // Luisa (empleado)
                "900100100",           // Ana (asesor)
                TipoCredito.VIVIENDA,
                new BigDecimal("10000000.00"),
                1);                    // 1 año = 12 meses

        // --- CASO 2: Crédito rechazado por superávit ---
        // Pedro Ramírez (1020304051) tiene gastos altos
        System.out.println("\n--- CASO 2: Crédito rechazado (empleado con gastos altos) ---\n");
        probarCredito(servicio,
                "1020304051",          // Pedro (no existe, pero probará el error)
                "900100100",
                TipoCredito.LIBRE_INVERSION,
                new BigDecimal("10000000.00"),
                3);

        // --- CASO 3: Plazo inválido (Vehículo a 1 año, mínimo 5) ---
        System.out.println("\n--- CASO 3: Plazo inválido (Vehículo, 1 año) ---\n");
        probarCredito(servicio,
                "1020304050",
                "900100100",
                TipoCredito.VEHICULO,
                new BigDecimal("20000000.00"),
                1);                    // 1 año < 5 mínimo

        // --- CASO 4: Empleado inexistente ---
        System.out.println("\n--- CASO 4: Empleado inexistente ---\n");
        probarCredito(servicio,
                "0000000000",
                "900100100",
                TipoCredito.LIBRE_INVERSION,
                new BigDecimal("5000000.00"),
                3);

        System.out.println("\n=== Fin de la prueba ===");
    }

    private static void probarCredito(ServicioCredito servicio,
                                       String identificacionEmpleado,
                                       String identificacionAsesor,
                                       TipoCredito tipo,
                                       BigDecimal monto,
                                       int anios) {

        ResultadoCredito resultado = servicio.registrarCredito(
                identificacionEmpleado, identificacionAsesor, tipo, monto, anios);

        System.out.println("  Tipo de resultado: " + resultado.getTipo());
        System.out.println("  Mensaje: " + resultado.getMensaje());

        if (resultado.isExitoso() && resultado.getCredito() != null) {
            Credito c = resultado.getCredito();
            System.out.println("  Crédito ID: " + c.getId());
            System.out.println("  Empleado: " + c.getEmpleado().getNombreCompleto());
            System.out.println("  Asesor: " + c.getAsesor().getNombreCompleto());
            System.out.println("  Cuota: $" + c.getCuotaMensual());
            System.out.println("  Filas de tabla: " + resultado.getTabla().size());

            List<CuotaAmortizacion> tabla = resultado.getTabla();
            if (!tabla.isEmpty()) {
                CuotaAmortizacion ultima = tabla.get(tabla.size() - 1);
                System.out.println("  Saldo final: $" + ultima.getSaldoPendiente());
            }
        } else if (resultado.isRechazado() && resultado.getCredito() != null) {
            System.out.println("  Crédito ID (rechazado): " + resultado.getCredito().getId());
            System.out.println("  Motivo: " + resultado.getCredito().getMotivoRechazo());
        } else if (resultado.requiereReintento()) {
            System.out.println("  Plazo mínimo sugerido: " + resultado.getPlazoMinimoSugerido() + " años");
            System.out.println("  Plazo máximo sugerido: " + resultado.getPlazoMaximoSugerido() + " años");
        }
        System.out.println();
    }
}