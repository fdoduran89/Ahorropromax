package com.mycompany.ahorropromax.core;

import com.mycompany.ahorropromax.negocio.Asesor;
import com.mycompany.ahorropromax.negocio.Director;
import com.mycompany.ahorropromax.persistencia.AsesorDAO;
import com.mycompany.ahorropromax.persistencia.DirectorDAO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Prueba del Bloque 2 de DAOs: AsesorDAO + DirectorDAO.
 *
 * Flujo:
 *   1. Insertar asesor (si no existe)
 *   2. Insertar director (si no existe)
 *   3. Buscar por identificación
 *   4. Listar todos
 */
public class MainPruebaDAO2 {

    public static void main(String[] args) {
        System.out.println("=== Prueba de DAOs (Bloque 2) ===\n");

        AsesorDAO asesorDAO     = new AsesorDAO();
        DirectorDAO directorDAO = new DirectorDAO();

        // ---------------------------------------------------------
        // 1. Insertar asesor
        // ---------------------------------------------------------
        String identAsesor = "900100100"; // Ana (ya existe en el script)
        if (!asesorDAO.existeIdentificacion(identAsesor)) {
            Asesor nuevo = new Asesor(identAsesor, "Ana", "Gómez",
                    new BigDecimal("3000000.00"));
            asesorDAO.insertar(nuevo);
            System.out.println("✓ Asesor insertado con id = " + nuevo.getId());
        } else {
            System.out.println("• Asesor " + identAsesor + " ya existe, no se inserta");
        }

        // ---------------------------------------------------------
        // 2. Insertar director
        // ---------------------------------------------------------
        String identDirector = "900100200"; // Carlos (ya existe en el script)
        if (!directorDAO.existeIdentificacion(identDirector)) {
            Director nuevo = new Director(identDirector, "Carlos", "Pérez",
                    new BigDecimal("6000000.00"));
            directorDAO.insertar(nuevo);
            System.out.println("✓ Director insertado con id = " + nuevo.getId());
        } else {
            System.out.println("• Director " + identDirector + " ya existe, no se inserta");
        }

        // ---------------------------------------------------------
        // 3. Buscar por identificación
        // ---------------------------------------------------------
        System.out.println("\n--- Búsqueda por identificación ---\n");

        Asesor asesorEncontrado = asesorDAO.buscarPorIdentificacion(identAsesor);
        if (asesorEncontrado != null) {
            imprimirAsesor(asesorEncontrado);
        } else {
            System.out.println("Asesor no encontrado");
        }

        Director directorEncontrado = directorDAO.buscarPorIdentificacion(identDirector);
        if (directorEncontrado != null) {
            imprimirDirector(directorEncontrado);
        } else {
            System.out.println("Director no encontrado");
        }

        // ---------------------------------------------------------
        // 4. Listar todos
        // ---------------------------------------------------------
        System.out.println("\n--- Todos los asesores ---\n");
        List<Asesor> asesores = asesorDAO.listar();
        for (Asesor a : asesores) {
            imprimirAsesor(a);
        }

        System.out.println("\n--- Todos los directores ---\n");
        List<Director> directores = directorDAO.listar();
        for (Director d : directores) {
            imprimirDirector(d);
        }

        System.out.println("\n=== Fin de la prueba ===");
    }

    private static void imprimirAsesor(Asesor a) {
        System.out.println("  [Asesor] " + a.getNombreCompleto()
                + " (id=" + a.getId() + ")");
        System.out.println("    Identificación: " + a.getIdentificacion());
        System.out.println("    Salario básico: $" + a.getSalarioBasico());
        System.out.println("    Retención:      $" + a.calcularRetencion());
        System.out.println();
    }

    private static void imprimirDirector(Director d) {
        System.out.println("  [Director] " + d.getNombreCompleto()
                + " (id=" + d.getId() + ")");
        System.out.println("    Identificación: " + d.getIdentificacion());
        System.out.println("    Salario básico: $" + d.getSalarioBasico());
        System.out.println("    Retención:      $" + d.calcularRetencion());
        System.out.println();
    }
}