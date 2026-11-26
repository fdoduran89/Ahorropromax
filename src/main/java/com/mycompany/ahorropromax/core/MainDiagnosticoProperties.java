package com.mycompany.ahorropromax.core;

import java.io.InputStream;
import java.util.Properties;

public class MainDiagnosticoProperties {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Diagnóstico de db.properties ===\n");

        // 1. ¿Dónde busca el archivo?
        System.out.println("Classpath de ejecución:");
        String cp = System.getProperty("java.class.path");
        for (String p : cp.split(";")) {
            if (p.contains("classes") || p.contains("target")) {
                System.out.println("  " + p);
            }
        }
        System.out.println();

        // 2. ¿Lo encuentra?
        try (InputStream in = MainDiagnosticoProperties.class
                .getResourceAsStream("/db.properties")) {
            if (in == null) {
                System.err.println("❌ NO se encontró /db.properties en el classpath");
                return;
            }
            System.out.println("✅ /db.properties encontrado en el classpath");

            // 3. Leer y mostrar CADA valor byte a byte
            Properties p = new Properties();
            p.load(in);

            System.out.println("\nContenido leído por Java:\n");
            for (String key : p.stringPropertyNames()) {
                String value = p.getProperty(key);
                System.out.println("  " + key + " = [" + value + "]");
                System.out.println("    long=" + value.length()
                        + "  bytes=" + bytesToHex(value.getBytes("UTF-8")));
            }

            // 4. Comparar la contraseña con la esperada
            System.out.println("\n=== Verificación de contraseña ===\n");
            String esperada = "Est4p4r4DB";
            String leida    = p.getProperty("db.password");
            System.out.println("Esperada: [" + esperada + "]");
            System.out.println("Leída:    [" + leida + "]");
            System.out.println("¿Coinciden? " + esperada.equals(leida));
            if (!esperada.equals(leida)) {
                System.out.println("  long esperada=" + esperada.length()
                        + "  long leída=" + leida.length());
            }
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}