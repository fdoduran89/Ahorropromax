package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Empleado de una empresa cliente del banco. Es el sujeto del crédito de
 * libranza.
 *
 * Herencia: - hereda de Persona (identificación, nombres, apellidos)
 *
 * Asociación: - N:1 con ClienteCorporativo (pertenece a una empresa)
 *
 * Encapsulamiento de la evaluación financiera (RN4): superávit = ingresos −
 * gastos aprobado = superávit ≥ 35% de ingresos Y cuota ≤ 35% de ingresos
 */
public class EmpleadoCorporativo extends Persona {

    /**
     * Porcentaje mínimo requerido (RN4).
     */
    public static final BigDecimal PORCENTAJE_MINIMO_SUPERAVIT = new BigDecimal("0.35");

    private ClienteCorporativo clienteCorporativo;
    private String cargo;
    private BigDecimal salarioBasico;
    private BigDecimal ingresosMensuales;
    private BigDecimal gastosMensuales;

    // -------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------
    public EmpleadoCorporativo() {
    }

    public EmpleadoCorporativo(String identificacion, String nombres, String apellidos,
            ClienteCorporativo clienteCorporativo, String cargo,
            BigDecimal salarioBasico,
            BigDecimal ingresosMensuales,
            BigDecimal gastosMensuales) {
        super(identificacion, nombres, apellidos);
        setClienteCorporativo(clienteCorporativo);
        setCargo(cargo);
        setSalarioBasico(salarioBasico);
        setIngresosMensuales(ingresosMensuales);
        setGastosMensuales(gastosMensuales);
    }

    // -------------------------------------------------------------------
    // Getters y setters con validación
    // -------------------------------------------------------------------
    public ClienteCorporativo getClienteCorporativo() {
        return clienteCorporativo;
    }

    public final void setClienteCorporativo(ClienteCorporativo clienteCorporativo) {
        if (clienteCorporativo == null) {
            throw new IllegalArgumentException("El empleado debe pertenecer a una empresa");
        }
        this.clienteCorporativo = clienteCorporativo;
    }

    public String getCargo() {
        return cargo;
    }

    public final void setCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("El cargo no puede estar vacío");
        }
        this.cargo = cargo.trim();
    }

    public BigDecimal getSalarioBasico() {
        return salarioBasico;
    }

    public final void setSalarioBasico(BigDecimal salarioBasico) {
        if (salarioBasico == null || salarioBasico.signum() < 0) {
            throw new IllegalArgumentException("El salario no puede ser negativo");
        }
        this.salarioBasico = salarioBasico;
    }

    public BigDecimal getIngresosMensuales() {
        return ingresosMensuales;
    }

    public final void setIngresosMensuales(BigDecimal ingresosMensuales) {
        if (ingresosMensuales == null || ingresosMensuales.signum() < 0) {
            throw new IllegalArgumentException("Los ingresos no pueden ser negativos");
        }
        this.ingresosMensuales = ingresosMensuales;
    }

    public BigDecimal getGastosMensuales() {
        return gastosMensuales;
    }

    public final void setGastosMensuales(BigDecimal gastosMensuales) {
        if (gastosMensuales == null || gastosMensuales.signum() < 0) {
            throw new IllegalArgumentException("Los gastos no pueden ser negativos");
        }
        this.gastosMensuales = gastosMensuales;
    }

    // -------------------------------------------------------------------
    // Identidad de rol
    // -------------------------------------------------------------------
    @Override
    public String getRol() {
        return "Empleado Corporativo";
    }

    // -------------------------------------------------------------------
    // Evaluación financiera (RN4)
    // -------------------------------------------------------------------
    /**
     * Superávit = ingresos − gastos.
     */
    public BigDecimal calcularCapacidadDePago() {
        return ingresosMensuales.subtract(gastosMensuales);
    }

    /**
     * Superávit mínimo exigido = 35% de los ingresos.
     */
    public BigDecimal calcularSuperavitRequerido() {
        return ingresosMensuales
                .multiply(PORCENTAJE_MINIMO_SUPERAVIT)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Porcentaje real de superávit sobre ingresos (informativo).
     */
    public BigDecimal calcularPorcentajeSuperavit() {
        if (ingresosMensuales.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return calcularCapacidadDePago()
                .divide(ingresosMensuales, 4, RoundingMode.HALF_UP);
    }

    /**
     * Evalúa si el empleado es apto para un crédito con la cuota indicada.
     *
     * Condiciones (RN4): 1. superávit ≥ 35% de ingresos 2. cuota ≤ 35% de
     * ingresos
     */
    public boolean esAptoParaCredito(BigDecimal cuotaMensual) {
        if (cuotaMensual == null) {
            throw new IllegalArgumentException("La cuota mensual es obligatoria");
        }
        BigDecimal superavit = calcularCapacidadDePago();
        BigDecimal minimo = calcularSuperavitRequerido();
        return superavit.compareTo(minimo) >= 0
                && cuotaMensual.compareTo(minimo) <= 0;
    }

    /**
     * Devuelve el motivo por el cual NO es apto, o null si es apto. Útil para
     * la JSP de resultado rechazado.
     */
    public String getMotivoNoApto(BigDecimal cuotaMensual) {
        if (esAptoParaCredito(cuotaMensual)) {
            return null;
        }
        if (calcularCapacidadDePago().compareTo(calcularSuperavitRequerido()) < 0) {
            return "El superávit mensual no alcanza el 35% de los ingresos";
        }
        return "La cuota mensual supera el 35% de los ingresos";
    }

    // -------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------
    @Override
    public String toString() {
        return "EmpleadoCorporativo{"
                + "nombreCompleto='" + getNombreCompleto() + '\''
                + ", empresa='" + (clienteCorporativo != null
                        ? clienteCorporativo.getRazonSocial()
                        : "sin empresa") + '\''
                + ", cargo='" + cargo + '\''
                + ", ingresos=" + ingresosMensuales
                + ", gastos=" + gastosMensuales
                + '}';
    }
}
