package com.mycompany.ahorropromax.negocio;

import java.math.BigDecimal;

/**
 * Fila de la tabla de amortización. INMUTABLE.
 *
 * numeroMes = 0 → fila inicial (deuda inicial = monto, sin pago aún)
 * numeroMes = 1..n → cada mensualidad
 *
 * Decisión de diseño: no tiene setters. Una cuota calculada no cambia.
 */
public class CuotaAmortizacion {

    private final int numeroMes;
    private final BigDecimal deudaInicial;
    private final BigDecimal cuotaFija;
    private final BigDecimal interes;
    private final BigDecimal abonoCapital;
    private final BigDecimal saldoPendiente;

    public CuotaAmortizacion(int numeroMes,
                             BigDecimal deudaInicial,
                             BigDecimal cuotaFija,
                             BigDecimal interes,
                             BigDecimal abonoCapital,
                             BigDecimal saldoPendiente) {
        this.numeroMes       = numeroMes;
        this.deudaInicial    = deudaInicial;
        this.cuotaFija       = cuotaFija;
        this.interes         = interes;
        this.abonoCapital    = abonoCapital;
        this.saldoPendiente  = saldoPendiente;
    }

    // Getters (no hay setters: es inmutable)
    public int getNumeroMes()                { return numeroMes; }
    public BigDecimal getDeudaInicial()      { return deudaInicial; }
    public BigDecimal getCuotaFija()         { return cuotaFija; }
    public BigDecimal getInteres()           { return interes; }
    public BigDecimal getAbonoCapital()      { return abonoCapital; }
    public BigDecimal getSaldoPendiente()    { return saldoPendiente; }

    @Override
    public String toString() {
        return String.format(
            "Mes %2d | Deuda: %12s | Cuota: %10s | Interés: %10s | Capital: %10s | Saldo: %12s",
            numeroMes, deudaInicial, cuotaFija, interes, abonoCapital, saldoPendiente);
    }
}