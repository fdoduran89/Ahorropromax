package com.mycompany.ahorropromax.core;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.persistencia.ClienteCorporativoDAO;
import com.mycompany.ahorropromax.persistencia.EmpleadoCorporativoDAO;

import java.math.BigDecimal;
import java.util.List;

public class MainPruebaDAO {

    public static void main(String[] args) {
        System.out.println("=== Prueba de DAOs (Bloque 1) ===\n");

        ClienteCorporativoDAO empresaDAO   = new ClienteCorporativoDAO();
        EmpleadoCorporativoDAO empleadoDAO = new EmpleadoCorporativoDAO();

        try {
            // ---------------------------------------------------------
            // 1. Insertar empresa
            // ---------------------------------------------------------
            ClienteCorporativo empresa = new ClienteCorporativo(
                    "900777111-2", "Innovación Digital Ltda.", "Servicios TI");
            empresa.setDireccion("Carrera 15 #85-20");
            empresa.setTelefono("6017778888");
            empresa.setCorreo("contacto@innovadigital.com");

            empresaDAO.insertar(empresa);
            System.out.println("✓ Empresa insertada con id = " + empresa.getId());

            // ---------------------------------------------------------
            // 2. Insertar empleado
            // ---------------------------------------------------------
            EmpleadoCorporativo empleado = new EmpleadoCorporativo(
                    "1122334455", "María", "Rodríguez",
                    empresa, "Desarrolladora senior",
                    new BigDecimal("5500000.00"),
                    new BigDecimal("5500000.00"),
                    new BigDecimal("2000000.00"));

            empleadoDAO.insertar(empleado, empresa);
            System.out.println("✓ Empleado insertado con id = " + empleado.getId());

            // ---------------------------------------------------------
            // 3. Buscar empleado por identificación
            // ---------------------------------------------------------
            System.out.println("\n--- Búsqueda por identificación ---");
            EmpleadoCorporativo encontrado = empleadoDAO.buscarPorIdentificacion("1122334455");
            if (encontrado != null) {
                imprimirEmpleado(encontrado);
            } else {
                System.out.println("No encontrado");
            }

            // ---------------------------------------------------------
            // 4. Listar empleados de la empresa
            // ---------------------------------------------------------
            System.out.println("\n--- Empleados de la empresa ---");
            List<EmpleadoCorporativo> lista = empleadoDAO.listarPorEmpresa(empresa.getId());
            for (EmpleadoCorporativo e : lista) {
                imprimirEmpleado(e);
            }

            // ---------------------------------------------------------
            // 5. Listar todas las empresas
            // ---------------------------------------------------------
            System.out.println("\n--- Todas las empresas ---");
            for (ClienteCorporativo c : empresaDAO.listar()) {
                System.out.println("  " + c);
            }

            System.out.println("\n=== Fin de la prueba ===");

        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void imprimirEmpleado(EmpleadoCorporativo e) {
        System.out.println("  " + e.getNombreCompleto()
                + " (" + e.getCargo() + " en " + e.getClienteCorporativo().getRazonSocial() + ")");
        System.out.println("    Identificación: " + e.getIdentificacion());
        System.out.println("    Salario:   $" + e.getSalarioBasico());
        System.out.println("    Ingresos:  $" + e.getIngresosMensuales());
        System.out.println("    Gastos:    $" + e.getGastosMensuales());
        System.out.println("    ¿Apto para crédito? " + e.esAptoParaCredito(new BigDecimal("1000000")));
        System.out.println();
    }
}