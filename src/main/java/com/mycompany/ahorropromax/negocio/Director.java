package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Director del banco.
 *
 * Polimorfismo: implementa calcularComision() con los porcentajes del director.
 * Tabla RN6: Vehículo → 3.0% Vivienda → 4.0% Libre inversión → 2.5%
 */
public class Director extends Empleado {

    public static final BigDecimal PCT_VEHICULO = new BigDecimal("0.030");
    public static final BigDecimal PCT_VIVIENDA = new BigDecimal("0.040");
    public static final BigDecimal PCT_LIBRE = new BigDecimal("0.025");

    public Director() {
    }

    public Director(String identificacion, String nombres, String apellidos,
            BigDecimal salarioBasico) {
        super(identificacion, nombres, apellidos, salarioBasico);
    }

    @Override
    public String getRol() {
        return "Director";
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
