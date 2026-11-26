package com.mycompany.ahorropromax.servicios;

import com.mycompany.ahorropromax.negocio.Credito;
import com.mycompany.ahorropromax.negocio.Empleado;
import com.mycompany.ahorropromax.negocio.EstadoCredito;
import com.mycompany.ahorropromax.negocio.Liquidacion;
import com.mycompany.ahorropromax.persistencia.LiquidacionDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de liquidación de salarios.
 *
 * Ahora hace dos cosas:
 *   1. Calcula la liquidación (salario + comisiones con polimorfismo)
 *   2. Persiste la liquidación en BD
 *
 * Los Servlets llaman a liquidarYPersistir() y obtienen el resultado completo.
 */
public class ServLiquidacionSalario {

    private final LiquidacionDAO dao = new LiquidacionDAO();

    /**
     * Calcula y persiste la liquidación de un empleado.
     */
    public Liquidacion liquidarYPersistir(Empleado empleado, List<Credito> creditos, String periodo) {
        Liquidacion liq = calcular(empleado, creditos, periodo);
        dao.insertar(liq);
        return liq;
    }

    /**
     * Calcula la liquidación (sin persistir).
     */
    public Liquidacion calcular(Empleado empleado, List<Credito> creditos, String periodo) {
        if (empleado == null) throw new IllegalArgumentException("El empleado es obligatorio");
        if (periodo == null || periodo.trim().isEmpty()) {
            throw new IllegalArgumentException("El período es obligatorio");
        }
        if (creditos == null) creditos = new ArrayList<>();

        BigDecimal totalComisiones = calcularTotalComisiones(empleado, creditos);
        BigDecimal retencion       = empleado.calcularRetencion();
        BigDecimal salarioFinal    = empleado.calcularSalarioFinal(totalComisiones);

        return new Liquidacion(empleado, periodo,
                empleado.getSalarioBasico(), retencion,
                totalComisiones, salarioFinal);
    }

    public BigDecimal calcularTotalComisiones(Empleado empleado, List<Credito> creditos) {
        if (creditos == null || creditos.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Credito credito : creditos) {
            if (credito.getEstado() != EstadoCredito.APROBADO) continue;
            if (!esCreditoDelEmpleado(credito, empleado)) continue;
            BigDecimal comision = empleado.calcularComision(
                    credito.getTipo(), credito.getMontoSolicitado());
            total = total.add(comision);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularComisionIndividual(Empleado empleado, Credito credito) {
        if (empleado == null || credito == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return empleado.calcularComision(credito.getTipo(), credito.getMontoSolicitado());
    }

    private boolean esCreditoDelEmpleado(Credito credito, Empleado empleado) {
        if (credito.getAsesor() == null) return false;
        if (credito.getAsesor().equals(empleado)) return true;
        return "Director".equals(empleado.getRol());
    }
}