package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Empleado del banco (Asesor o Director).
 *
 * Herencia: - hereda de Persona (identificación, nombres, apellidos)
 *
 * Abstracción: - no se instancia directamente - declara calcularComision(...)
 * como abstracto
 *
 * Polimorfismo: - Asesor y Director implementan calcularComision() con sus
 * porcentajes - calcularSalarioFinal(...) usa ese método sin saber qué subclase
 * es
 */
public abstract class Empleado extends Persona {

    /**
     * Retención de ley: 8% del salario básico (salud + pensión).
     */
    public static final BigDecimal PORCENTAJE_RETENCION = new BigDecimal("0.08");

    private BigDecimal salarioBasico;

    // -------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------
    protected Empleado() {
    }

    protected Empleado(String identificacion, String nombres, String apellidos,
            BigDecimal salarioBasico) {
        super(identificacion, nombres, apellidos);
        setSalarioBasico(salarioBasico);
    }

    // -------------------------------------------------------------------
    // Encapsulamiento
    // -------------------------------------------------------------------
    public BigDecimal getSalarioBasico() {
        return salarioBasico;
    }

    public final void setSalarioBasico(BigDecimal salarioBasico) {
        if (salarioBasico == null || salarioBasico.signum() < 0) {
            throw new IllegalArgumentException("El salario básico no puede ser negativo");
        }
        this.salarioBasico = salarioBasico;
    }

    // -------------------------------------------------------------------
    // Cálculos
    // -------------------------------------------------------------------
    /**
     * Retención = 8% del salario básico.
     */
    public BigDecimal calcularRetencion() {
        return salarioBasico
                .multiply(PORCENTAJE_RETENCION)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Salario final = salario básico − retención + comisiones.
     */
    public BigDecimal calcularSalarioFinal(BigDecimal totalComisiones) {
        if (totalComisiones == null) {
            totalComisiones = BigDecimal.ZERO;
        }
        return salarioBasico
                .subtract(calcularRetencion())
                .add(totalComisiones)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Contrato polimórfico: cada subclase define sus porcentajes.
     *
     * @param tipo tipo de crédito aprobado
     * @param monto monto solicitado del crédito aprobado
     * @return comisión calculada sobre el monto
     */
    public abstract BigDecimal calcularComision(TipoCredito tipo, BigDecimal monto);
}
