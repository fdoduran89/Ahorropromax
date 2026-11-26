package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Asesor de crédito: empleado del banco que asigna créditos.
 *
 * Polimorfismo: implementa calcularComision() con los porcentajes del asesor.
 * Tabla RN6: Vehículo → 2.0% Vivienda → 2.5% Libre inversión → 1.5%
 */
public class Asesor extends Empleado {

    public static final BigDecimal PCT_VEHICULO = new BigDecimal("0.020");
    public static final BigDecimal PCT_VIVIENDA = new BigDecimal("0.025");
    public static final BigDecimal PCT_LIBRE = new BigDecimal("0.015");

    public Asesor() {
    }

    public Asesor(String identificacion, String nombres, String apellidos,
            BigDecimal salarioBasico) {
        super(identificacion, nombres, apellidos, salarioBasico);
    }

    @Override
    public String getRol() {
        return "Asesor";
    }

    @Override
    public BigDecimal calcularComision(TipoCredito tipo, BigDecimal monto) {
        if (tipo == null || monto == null) {
            throw new IllegalArgumentException("Tipo y monto son obligatorios");
        }
        BigDecimal pct = switch (tipo) {
            case VEHICULO ->
                PCT_VEHICULO;
            case VIVIENDA ->
                PCT_VIVIENDA;
            case LIBRE_INVERSION ->
                PCT_LIBRE;
        };
        return monto.multiply(pct).setScale(2, RoundingMode.HALF_UP);
    }
}
