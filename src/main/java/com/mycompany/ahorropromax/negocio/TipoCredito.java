package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;

/**
 * Catálogo de tipos de crédito con sus reglas de negocio (RN1).
 *
 * Reglas:
 *   VEHICULO       → tasa 2.0% mensual, plazo mínimo 60 meses (5 años), sin máximo
 *   VIVIENDA       → tasa 0.9% mensual, sin mínimo, plazo máximo 120 meses (10 años)
 *   LIBRE_INVERSION→ tasa 1.9% mensual, plazo mínimo 36 meses (3 años), sin máximo
 *
 * Encapsula:
 *   - la tasa mensual como BigDecimal (precisión financiera)
 *   - los plazos mínimo/máximo en meses
 *   - la validación de plazos
 */
public enum TipoCredito {

    VEHICULO("Vehículo",
             new BigDecimal("0.0200"),
             60,
             null),

    VIVIENDA("Vivienda",
             new BigDecimal("0.0090"),
             null,
             120),

    LIBRE_INVERSION("Libre inversión",
                    new BigDecimal("0.0190"),
                    36,
                    null);

    private final String nombre;
    private final BigDecimal tasaMensual;
    private final Integer plazoMinimoMeses;
    private final Integer plazoMaximoMeses;

    TipoCredito(String nombre,
                BigDecimal tasaMensual,
                Integer plazoMinimoMeses,
                Integer plazoMaximoMeses) {
        this.nombre = nombre;
        this.tasaMensual = tasaMensual;
        this.plazoMinimoMeses = plazoMinimoMeses;
        this.plazoMaximoMeses = plazoMaximoMeses;
    }

    // -------------------------------------------------------------------
    // Getters (encapsulamiento: atributos privados, lectura pública)
    // -------------------------------------------------------------------

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getTasaMensual() {
        return tasaMensual;
    }

    public Integer getPlazoMinimoMeses() {
        return plazoMinimoMeses;
    }

    public Integer getPlazoMaximoMeses() {
        return plazoMaximoMeses;
    }

    // -------------------------------------------------------------------
    // Validaciones de plazo
    // -------------------------------------------------------------------

    /**
     * Indica si el plazo (en meses) está dentro de los límites del tipo.
     * Si el tipo no tiene mínimo, cualquier plazo positivo es válido.
     * Si el tipo no tiene máximo, cualquier plazo por encima del mínimo es válido.
     */
    public boolean esPlazoValido(int plazoMeses) {
        if (plazoMeses <= 0) {
            return false;
        }
        if (plazoMinimoMeses != null && plazoMeses < plazoMinimoMeses) {
            return false;
        }
        if (plazoMaximoMeses != null && plazoMeses > plazoMaximoMeses) {
            return false;
        }
        return true;
    }

    /**
     * Devuelve un mensaje legible con los límites del tipo, para mostrarlo al usuario
     * cuando el plazo es inválido.
     */
    public String describirLimitesPlazo() {
        if (plazoMinimoMeses != null && plazoMaximoMeses != null) {
            return "entre " + plazoMinimoMeses + " y " + plazoMaximoMeses + " meses";
        }
        if (plazoMinimoMeses != null) {
            return "mínimo " + plazoMinimoMeses + " meses";
        }
        if (plazoMaximoMeses != null) {
            return "máximo " + plazoMaximoMeses + " meses";
        }
        return "cualquier plazo positivo";
    }

    // -------------------------------------------------------------------
    // Mapeo desde el String de la BD
    // -------------------------------------------------------------------

    /**
     * Convierte el texto tal como viene en la tabla tipo_credito.nombre
     * al enum correspondiente.
     *
     * @throws IllegalArgumentException si el nombre no corresponde a ningún tipo.
     */
    public static TipoCredito desdeNombreBd(String nombreBd) {
        if (nombreBd == null) {
            throw new IllegalArgumentException("El nombre del tipo no puede ser nulo");
        }
        for (TipoCredito t : values()) {
            if (t.nombre.equalsIgnoreCase(nombreBd)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de crédito desconocido: " + nombreBd);
    }

    @Override
    public String toString() {
        return nombre;
    }
}