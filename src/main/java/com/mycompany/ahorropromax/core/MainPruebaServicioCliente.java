package com.mycompany.ahorropromax.core;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.servicios.ServicioClienteCorporativo;

import java.util.List;

public class MainPruebaServicioCliente {

    public static void main(String[] args) {
        System.out.println("=== Prueba de ServicioClienteCorporativo ===\n");

        ServicioClienteCorporativo servicio = new ServicioClienteCorporativo();

        try {
            // 1. Registrar empresa nueva
            ClienteCorporativo empresa = new ClienteCorporativo(
                    "900123456-7", "Servicios Logísticos Beta S.A.", "Logística");
            empresa.setDireccion("Av. 68 #22-15");
            empresa.setTelefono("6014445566");
            empresa.setCorreo("info@logisticabeta.com");

            servicio.registrar(empresa);
            System.out.println("✓ Empresa registrada: " + empresa.getRazonSocial()
                    + " (id=" + empresa.getId() + ")");

            // 2. Intentar registrar con el mismo NIT (debe fallar)
            System.out.println("\n--- Prueba de NIT duplicado ---");
            try {
                ClienteCorporativo duplicada = new ClienteCorporativo(
                        "900123456-7", "Otra Empresa", "Otro Sector");
                servicio.registrar(duplicada);
                System.out.println("❌ ERROR: debería haber lanzado excepción");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Excepción correcta: " + e.getMessage());
            }

            // 3. Buscar por NIT
            System.out.println("\n--- Búsqueda por NIT ---");
            ClienteCorporativo encontrada = servicio.buscarPorNit("900123456-7");
            if (encontrada != null) {
                System.out.println("  " + encontrada);
            }

            // 4. Listar todas
            System.out.println("\n--- Todas las empresas ---");
            List<ClienteCorporativo> todas = servicio.listar();
            for (ClienteCorporativo c : todas) {
                System.out.println("  " + c);
            }

            System.out.println("\n=== Fin de la prueba ===");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}