package com.mycompany.ahorropromax.core;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.servicios.ServicioClienteCorporativo;
import com.mycompany.ahorropromax.servicios.ServicioEmpleadoCorporativo;

import java.math.BigDecimal;
import java.util.List;

public class MainPruebaServicioEmpleado {

    public static void main(String[] args) {
        System.out.println("=== Prueba de ServicioEmpleadoCorporativo ===\n");

        ServicioClienteCorporativo servicioCliente = new ServicioClienteCorporativo();
        ServicioEmpleadoCorporativo servicioEmpleado = new ServicioEmpleadoCorporativo();

        try {
            // 1. Buscar una empresa existente
            ClienteCorporativo empresa = servicioCliente.buscarPorNit("900123456-7");
            if (empresa == null) {
                System.out.println("❌ Empresa 900123456-7 no existe. Ejecuta MainPruebaServicioCliente primero.");
                return;
            }
            System.out.println("✓ Empresa: " + empresa.getRazonSocial()
                    + " (id=" + empresa.getId() + ")");

            // 2. Registrar un empleado nuevo
            EmpleadoCorporativo empleado = new EmpleadoCorporativo(
                    "5566778899", "Andrés", "Castro",
                    empresa, "Coordinador de operaciones",
                    new BigDecimal("4200000.00"),
                    new BigDecimal("4200000.00"),
                    new BigDecimal("1800000.00"));

            servicioEmpleado.registrar(empleado, empresa.getId());
            System.out.println("✓ Empleado registrado: " + empleado.getNombreCompleto()
                    + " (id=" + empleado.getId() + ")");

            // 3. Intentar registrar con la misma identificación (debe fallar)
            System.out.println("\n--- Prueba de identificación duplicada ---");
            try {
                EmpleadoCorporativo duplicado = new EmpleadoCorporativo(
                        "5566778899", "Otro", "Empleado",
                        empresa, "Otro cargo",
                        new BigDecimal("1000000.00"),
                        new BigDecimal("1000000.00"),
                        new BigDecimal("500000.00"));
                servicioEmpleado.registrar(duplicado, empresa.getId());
                System.out.println("❌ ERROR: debería haber lanzado excepción");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Excepción correcta: " + e.getMessage());
            }

            // 4. Intentar registrar con empresa inexistente (debe fallar)
            System.out.println("\n--- Prueba de empresa inexistente ---");
            try {
                EmpleadoCorporativo huerfano = new EmpleadoCorporativo(
                        "9988776655", "Sin", "Empresa",
                        empresa, "Cargo",
                        new BigDecimal("1000000.00"),
                        new BigDecimal("1000000.00"),
                        new BigDecimal("500000.00"));
                servicioEmpleado.registrar(huerfano, 99999L);
                System.out.println("❌ ERROR: debería haber lanzado excepción");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Excepción correcta: " + e.getMessage());
            }

            // 5. Buscar por identificación
            System.out.println("\n--- Búsqueda por identificación ---");
            EmpleadoCorporativo encontrado = servicioEmpleado.buscarPorIdentificacion("5566778899");
            if (encontrado != null) {
                imprimirEmpleado(encontrado);
            }

            // 6. Listar empleados de la empresa
            System.out.println("\n--- Empleados de la empresa ---");
            List<EmpleadoCorporativo> lista = servicioEmpleado.listarPorEmpresa(empresa.getId());
            for (EmpleadoCorporativo e : lista) {
                imprimirEmpleado(e);
            }

            System.out.println("\n=== Fin de la prueba ===");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void imprimirEmpleado(EmpleadoCorporativo e) {
        System.out.println("  " + e.getNombreCompleto()
                + " (" + e.getCargo() + " en " + e.getClienteCorporativo().getRazonSocial() + ")");
        System.out.println("    Identificación: " + e.getIdentificacion());
        System.out.println("    Ingresos:  $" + e.getIngresosMensuales());
        System.out.println("    Gastos:    $" + e.getGastosMensuales());
        System.out.println("    Superávit: $" + e.calcularCapacidadDePago());
        System.out.println();
    }
}