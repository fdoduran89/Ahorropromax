package com.mycompany.ahorropromax.core;

import com.mycompany.ahorropromax.servicios.ResultadoCargaMasiva;
import com.mycompany.ahorropromax.servicios.ServicioExcel;

import java.io.FileInputStream;

public class MainPruebaServicioExcel {

    public static void main(String[] args) {
        System.out.println("=== Prueba de ServicioExcel ===\n");

        String rutaArchivo = "empleados.xlsx";
        Long idEmpresa = 3L; // Servicios Logísticos Beta

        try (FileInputStream fis = new FileInputStream(rutaArchivo)) {
            ServicioExcel servicio = new ServicioExcel();
            ResultadoCargaMasiva resultado = servicio.cargar(fis, idEmpresa);

            System.out.println("Total filas: " + resultado.getTotalFilas());
            System.out.println("Insertados:  " + resultado.getTotalInsertados());
            System.out.println("Errores:     " + resultado.getTotalErrores());
            System.out.println();

            if (resultado.getTotalInsertados() > 0) {
                System.out.println("--- Empleados insertados ---");
                resultado.getInsertados().forEach(e ->
                    System.out.println("  ✓ " + e.getNombreCompleto()
                            + " (" + e.getIdentificacion() + ")"));
            }

            if (resultado.getTotalErrores() > 0) {
                System.out.println("\n--- Errores ---");
                resultado.getErrores().forEach(err -> System.out.println("  ✗ " + err));
            }

            System.out.println("\n=== Fin de la prueba ===");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}