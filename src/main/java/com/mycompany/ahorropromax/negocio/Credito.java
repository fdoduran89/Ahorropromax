package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Crédito de libranza.
 *
 * Encapsula:
 *   - datos del crédito (empleado, asesor, tipo, monto, plazo, tasa)
 *   - cálculo de la cuota fija (fórmula francesa)
 *   - evaluación financiera (delegada a EmpleadoCorporativo)
 *   - generación de la tabla de amortización
 *
 * Fórmula francesa (RN2):
 *   R = A * i / (1 - (1 + i)^(-n))
 *   A = monto, i = tasa mensual, n = plazo en meses
 */
public class Credito {

    private Long id;
    private EmpleadoCorporativo empleado;
    private Asesor asesor;
    private TipoCredito tipo;
    private BigDecimal montoSolicitado;
    private int plazoMeses;
    private BigDecimal tasaAplicada;
    private BigDecimal cuotaMensual;
    private EstadoCredito estado;
    private String motivoRechazo;

    // -------------------------------------------------------------------
    // Constructor estándar: la tasa viene del tipo de crédito
    // -------------------------------------------------------------------

    public Credito(EmpleadoCorporativo empleado, Asesor asesor, TipoCredito tipo,
                   BigDecimal montoSolicitado, int plazoMeses) {
        if (empleado == null)  throw new IllegalArgumentException("Empleado obligatorio");
        if (asesor == null)    throw new IllegalArgumentException("Asesor obligatorio");
        if (tipo == null)      throw new IllegalArgumentException("Tipo obligatorio");
        if (montoSolicitado == null || montoSolicitado.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        if (plazoMeses <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor que cero");
        }
        if (!tipo.esPlazoValido(plazoMeses)) {
            throw new IllegalArgumentException(
                "Plazo " + plazoMeses + " meses inválido para " + tipo
                + " (válido: " + tipo.describirLimitesPlazo() + ")");
        }

        this.empleado = empleado;
        this.asesor = asesor;
        this.tipo = tipo;
        this.montoSolicitado = montoSolicitado;
        this.plazoMeses = plazoMeses;
        this.tasaAplicada = tipo.getTasaMensual();
        this.estado = EstadoCredito.PENDIENTE;
    }

    // -------------------------------------------------------------------
    // Constructor alterno para pruebas y simulaciones con tasa forzada.
    // NO se debe usar en producción: la tasa debe venir siempre del tipo.
    // -------------------------------------------------------------------

    public Credito(EmpleadoCorporativo empleado, Asesor asesor, TipoCredito tipo,
                   BigDecimal montoSolicitado, int plazoMeses,
                   BigDecimal tasaForzada) {
        this(empleado, asesor, tipo, montoSolicitado, plazoMeses);
        if (tasaForzada == null || tasaForzada.signum() < 0
                || tasaForzada.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalArgumentException("La tasa forzada debe estar en [0, 1)");
        }
        this.tasaAplicada = tasaForzada;
    }

    // -------------------------------------------------------------------
    // Getters y setters
    // -------------------------------------------------------------------

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }
    public EmpleadoCorporativo getEmpleado()         { return empleado; }
    public Asesor getAsesor()                        { return asesor; }
    public TipoCredito getTipo()                     { return tipo; }
    public BigDecimal getMontoSolicitado()           { return montoSolicitado; }
    public int getPlazoMeses()                       { return plazoMeses; }
    public BigDecimal getTasaAplicada()              { return tasaAplicada; }
    public BigDecimal getCuotaMensual()              { return cuotaMensual; }
    public EstadoCredito getEstado()                 { return estado; }
    public String getMotivoRechazo()                 { return motivoRechazo; }

    // Setters usados por el DAO al reconstruir el crédito desde la BD
    public void setEstado(EstadoCredito estado) {
        this.estado = estado;
    }

    public void setCuotaMensual(BigDecimal cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    // -------------------------------------------------------------------
    // Cálculo de la cuota fija (RN2)
    // -------------------------------------------------------------------

    public BigDecimal calcularCuota() {
        if (tasaAplicada.signum() == 0) {
            this.cuotaMensual = montoSolicitado
                    .divide(BigDecimal.valueOf(plazoMeses), 2, RoundingMode.HALF_UP);
            return cuotaMensual;
        }

        BigDecimal uno = BigDecimal.ONE;
        BigDecimal factor = uno.add(tasaAplicada)
                .pow(-plazoMeses, new MathContext(15));
        BigDecimal denominador = uno.subtract(factor);

        this.cuotaMensual = montoSolicitado
                .multiply(tasaAplicada)
                .divide(denominador, 2, RoundingMode.HALF_UP);

        return cuotaMensual;
    }

    // -------------------------------------------------------------------
    // Evaluación (RN4)
    // -------------------------------------------------------------------

    public EstadoCredito evaluar() {
        calcularCuota();
        if (empleado.esAptoParaCredito(cuotaMensual)) {
            this.estado = EstadoCredito.APROBADO;
            this.motivoRechazo = null;
        } else {
            this.estado = EstadoCredito.RECHAZADO;
            this.motivoRechazo = empleado.getMotivoNoApto(cuotaMensual);
        }
        return estado;
    }

    // -------------------------------------------------------------------
    // Tabla de amortización
    // -------------------------------------------------------------------

    public List<CuotaAmortizacion> generarTablaAmortizacion() {
        if (estado != EstadoCredito.APROBADO) {
            throw new IllegalStateException(
                "Solo se puede generar la tabla si el crédito está APROBADO");
        }
        if (cuotaMensual == null) {
            calcularCuota();
        }

        List<CuotaAmortizacion> tabla = new ArrayList<>();
        BigDecimal cero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // Fila 0 (inicial)
        tabla.add(new CuotaAmortizacion(0, montoSolicitado, cero, cero, cero, montoSolicitado));

        BigDecimal saldo = montoSolicitado;
        BigDecimal cuotaFija = cuotaMensual;

        for (int mes = 1; mes <= plazoMeses; mes++) {
            BigDecimal interes = saldo.multiply(tasaAplicada)
                                      .setScale(2, RoundingMode.HALF_UP);
            BigDecimal abonoCapital = cuotaFija.subtract(interes);
            BigDecimal cuotaEsteMes = cuotaFija;

            // Ajuste del último mes: forzar saldo cero
            if (mes == plazoMeses) {
                abonoCapital = saldo;
                cuotaEsteMes = interes.add(abonoCapital);
            }

            BigDecimal nuevoSaldo = saldo.subtract(abonoCapital);
            if (nuevoSaldo.abs().compareTo(new BigDecimal("0.01")) < 0) {
                nuevoSaldo = cero;
            }

            tabla.add(new CuotaAmortizacion(
                mes, saldo, cuotaEsteMes, interes, abonoCapital, nuevoSaldo));

            saldo = nuevoSaldo;
        }

        return Collections.unmodifiableList(tabla);
    }

    // -------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------

    @Override
    public String toString() {
        return "Credito{" +
                "empleado=" + empleado.getNombreCompleto() +
                ", tipo=" + tipo +
                ", monto=" + montoSolicitado +
                ", plazo=" + plazoMeses + " meses" +
                ", tasa=" + tasaAplicada +
                ", cuota=" + cuotaMensual +
                ", estado=" + estado +
                '}';
    }
}