package com.mycompany.ahorropromax.core;

import com.mycompany.ahorropromax.negocio.Asesor;
import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.Credito;
import com.mycompany.ahorropromax.negocio.CuotaAmortizacion;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.negocio.EstadoCredito;
import com.mycompany.ahorropromax.negocio.TipoCredito;
import com.mycompany.ahorropromax.persistencia.AsesorDAO;
import com.mycompany.ahorropromax.persistencia.CreditoDAO;
import com.mycompany.ahorropromax.persistencia.CuotaAmortizacionDAO;
import com.mycompany.ahorropromax.persistencia.EmpleadoCorporativoDAO;

import java.math.BigDecimal;
import java.util.List;

public class MainPruebaDAO3 {

    public static void main(String[] args) {
        System.out.println("=== Prueba de DAOs (Bloque 3: Crédito + Amortización) ===\n");

        CreditoDAO creditoDAO = new CreditoDAO();
        CuotaAmortizacionDAO cuotaDAO = new CuotaAmortizacionDAO();
        EmpleadoCorporativoDAO empleadoDAO = new EmpleadoCorporativoDAO();
        AsesorDAO asesorDAO = new AsesorDAO();

        try {
            // ---------------------------------------------------------
            // 1. Cargar empleado y asesor desde la BD
            // ---------------------------------------------------------
            EmpleadoCorporativo empleado = empleadoDAO.buscarPorIdentificacion("1020304050");
            if (empleado == null) {
                System.out.println("❌ Empleado 1020304050 no encontrado. Ejecuta Bloque 1 primero.");
                return;
            }
            System.out.println("✓ Empleado cargado: " + empleado.getNombreCompleto());

            Asesor asesor = asesorDAO.buscarPorIdentificacion("900100100");
            if (asesor == null) {
                System.out.println("❌ Asesor 900100100 no encontrado. Ejecuta Bloque 2 primero.");
                return;
            }
            System.out.println("✓ Asesor cargado: " + asesor.getNombreCompleto());

            // ---------------------------------------------------------
            // 2. Crear crédito del PDF: Libre inversión, 10M, 5% forzado, 12 meses
            //    (usamos Vivienda para que acepte 12 meses, tasa 5% forzada)
            // ---------------------------------------------------------
            Credito credito = new Credito(
                    empleado, asesor, TipoCredito.VIVIENDA,
                    new BigDecimal("10000000.00"), 12,
                    new BigDecimal("0.0500"));
            credito.calcularCuota();
            credito.evaluar();

            System.out.println("\n✓ Crédito creado:");
            System.out.println("  Monto: " + credito.getMontoSolicitado());
            System.out.println("  Plazo: " + credito.getPlazoMeses() + " meses");
            System.out.println("  Cuota: " + credito.getCuotaMensual());
            System.out.println("  Estado: " + credito.getEstado());

            if (credito.getEstado() != EstadoCredito.APROBADO) {
                System.out.println("❌ El crédito no fue aprobado. Motivo: " + credito.getMotivoRechazo());
                return;
            }

            // ---------------------------------------------------------
            // 3. Insertar el crédito
            // ---------------------------------------------------------
            creditoDAO.insertar(credito);
            System.out.println("✓ Crédito insertado con id = " + credito.getId());

            // ---------------------------------------------------------
            // 4. Generar tabla y guardar
            // ---------------------------------------------------------
            List<CuotaAmortizacion> tabla = credito.generarTablaAmortizacion();
            System.out.println("\n✓ Tabla generada con " + tabla.size() + " filas");
            cuotaDAO.insertarLote(tabla, credito.getId());

            // ---------------------------------------------------------
            // 5. Recuperar de la BD
            // ---------------------------------------------------------
            System.out.println("\n--- Recuperando crédito y tabla desde la BD ---\n");
            Credito recuperado = creditoDAO.buscarPorId(credito.getId());
            if (recuperado == null) {
                System.out.println("❌ No se pudo recuperar el crédito");
                return;
            }
            System.out.println("✓ Crédito recuperado:");
            System.out.println("  ID: " + recuperado.getId());
            System.out.println("  Empleado: " + recuperado.getEmpleado().getNombreCompleto());
            System.out.println("  Asesor: " + recuperado.getAsesor().getNombreCompleto());
            System.out.println("  Tipo: " + recuperado.getTipo());
            System.out.println("  Monto: " + recuperado.getMontoSolicitado());
            System.out.println("  Cuota: " + recuperado.getCuotaMensual());
            System.out.println("  Estado: " + recuperado.getEstado());

            List<CuotaAmortizacion> tablaRecuperada = cuotaDAO.listarPorCredito(recuperado.getId());
            System.out.println("\n✓ Tabla recuperada con " + tablaRecuperada.size() + " filas:");
            for (CuotaAmortizacion c : tablaRecuperada) {
                System.out.println("    " + c);
            }

            // ---------------------------------------------------------
            // 6. Verificación: saldo final debe ser 0
            // ---------------------------------------------------------
            System.out.println("\n--- Verificaciones ---\n");
            CuotaAmortizacion ultima = tablaRecuperada.get(tablaRecuperada.size() - 1);
            System.out.println("  Saldo final: " + ultima.getSaldoPendiente());
            System.out.println("  ¿Saldo 0? " + (ultima.getSaldoPendiente().signum() == 0));

            BigDecimal cuotaEsperada = new BigDecimal("1128254.10");
            System.out.println("  Cuota esperada: " + cuotaEsperada);
            System.out.println("  Cuota recuperada: " + recuperado.getCuotaMensual());
            System.out.println("  ¿Coincide? " + (recuperado.getCuotaMensual().compareTo(cuotaEsperada) == 0));

            System.out.println("\n=== Fin de la prueba ===");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}