package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Resultado de la liquidación de salario de un empleado en un período.
 *
 * Es un value object: encapsula el resultado del cálculo, no la lógica.
 * La lógica vive en ServLiquidacionSalario.
 */
public class Liquidacion {

    private Long id;
    private Empleado empleado;
    private String periodo;
    private BigDecimal salarioBasico;
    private BigDecimal retencion;
    private BigDecimal totalComisiones;
    private BigDecimal salarioFinal;
    private LocalDateTime fechaCalculo;

    public Liquidacion(Empleado empleado, String periodo,
                       BigDecimal salarioBasico, BigDecimal retencion,
                       BigDecimal totalComisiones, BigDecimal salarioFinal) {
        setEmpleado(empleado);
        setPeriodo(periodo);
        setSalarioBasico(salarioBasico);
        setRetencion(retencion);
        setTotalComisiones(totalComisiones);
        setSalarioFinal(salarioFinal);
        this.fechaCalculo = LocalDateTime.now();
    }

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }
    public Empleado getEmpleado()             { return empleado; }
    public String getPeriodo()                { return periodo; }
    public BigDecimal getSalarioBasico()      { return salarioBasico; }
    public BigDecimal getRetencion()          { return retencion; }
    public BigDecimal getTotalComisiones()    { return totalComisiones; }
    public BigDecimal getSalarioFinal()       { return salarioFinal; }
    public LocalDateTime getFechaCalculo()    { return fechaCalculo; }

    public final void setEmpleado(Empleado empleado) {
        if (empleado == null) throw new IllegalArgumentException("La liquidación requiere un empleado");
        this.empleado = empleado;
    }

    public final void setPeriodo(String periodo) {
        if (periodo == null || periodo.trim().isEmpty()) throw new IllegalArgumentException("El período es obligatorio");
        this.periodo = periodo.trim();
    }

    public final void setSalarioBasico(BigDecimal salarioBasico) {
        if (salarioBasico == null || salarioBasico.signum() < 0) throw new IllegalArgumentException("El salario básico no puede ser negativo");
        this.salarioBasico = salarioBasico.setScale(2, RoundingMode.HALF_UP);
    }

    public final void setRetencion(BigDecimal retencion) {
        if (retencion == null || retencion.signum() < 0) throw new IllegalArgumentException("La retención no puede ser negativa");
        this.retencion = retencion.setScale(2, RoundingMode.HALF_UP);
    }

    public final void setTotalComisiones(BigDecimal totalComisiones) {
        if (totalComisiones == null || totalComisiones.signum() < 0) throw new IllegalArgumentException("Las comisiones no pueden ser negativas");
        this.totalComisiones = totalComisiones.setScale(2, RoundingMode.HALF_UP);
    }

    public final void setSalarioFinal(BigDecimal salarioFinal) {
        if (salarioFinal == null || salarioFinal.signum() < 0) throw new IllegalArgumentException("El salario final no puede ser negativo");
        this.salarioFinal = salarioFinal.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Liquidacion)) return false;
        Liquidacion that = (Liquidacion) o;
        return Objects.equals(empleado, that.empleado) && Objects.equals(periodo, that.periodo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empleado, periodo);
    }

    @Override
    public String toString() {
        return "Liquidacion{" +
                "empleado=" + empleado.getRol() + " " + empleado.getNombreCompleto() +
                ", periodo='" + periodo + '\'' +
                ", salarioBasico=" + salarioBasico +
                ", retencion=" + retencion +
                ", totalComisiones=" + totalComisiones +
                ", salarioFinal=" + salarioFinal +
                '}';
    }
}