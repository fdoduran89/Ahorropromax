package com.mycompany.ahorropromax.servicios;

import com.mycompany.ahorropromax.negocio.Credito;
import com.mycompany.ahorropromax.negocio.CuotaAmortizacion;
import com.mycompany.ahorropromax.negocio.EstadoCredito;

import java.util.Collections;
import java.util.List;

/**
 * Resultado del flujo de registro de un crédito.
 *
 * Encapsula:
 *   - el crédito (con su estado y motivo de rechazo)
 *   - la tabla de amortización (solo si fue aprobado)
 *   - un mensaje general para mostrar al usuario
 *   - un indicador de si requiere reintento (ej. plazo fuera de rango)
 *
 * Es un Value Object: inmutable una vez construido.
 */
public class ResultadoCredito {

    public enum Tipo {
        EXITO,           // Crédito aprobado, tabla generada
        RECHAZO,         // Crédito rechazado por evaluación (superávit/cuota)
        ERROR_VALIDACION,// Plazo inválido, datos faltantes, etc.
        ERROR_INTERNO    // Error inesperado (BD, etc.)
    }

    private final Tipo tipo;
    private final String mensaje;
    private final Credito credito;
    private final List<CuotaAmortizacion> tabla;
    private final Integer plazoMinimoSugerido;
    private final Integer plazoMaximoSugerido;

    private ResultadoCredito(Tipo tipo, String mensaje, Credito credito,
                             List<CuotaAmortizacion> tabla,
                             Integer plazoMinimoSugerido,
                             Integer plazoMaximoSugerido) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.credito = credito;
        this.tabla = tabla != null ? Collections.unmodifiableList(tabla) : Collections.emptyList();
        this.plazoMinimoSugerido = plazoMinimoSugerido;
        this.plazoMaximoSugerido = plazoMaximoSugerido;
    }

    // --- Constructores de fábrica ---

    public static ResultadoCredito exito(Credito credito, List<CuotaAmortizacion> tabla) {
        return new ResultadoCredito(Tipo.EXITO,
                "Crédito aprobado exitosamente",
                credito, tabla, null, null);
    }

    public static ResultadoCredito rechazo(Credito credito, String motivo) {
        return new ResultadoCredito(Tipo.RECHAZO, motivo,
                credito, null, null, null);
    }

    public static ResultadoCredito errorValidacion(String mensaje,
                                                    Integer plazoMin,
                                                    Integer plazoMax) {
        return new ResultadoCredito(Tipo.ERROR_VALIDACION, mensaje,
                null, null, plazoMin, plazoMax);
    }

    public static ResultadoCredito errorInterno(String mensaje) {
        return new ResultadoCredito(Tipo.ERROR_INTERNO, mensaje,
                null, null, null, null);
    }

    // --- Getters ---

    public Tipo getTipo()                        { return tipo; }
    public String getMensaje()                   { return mensaje; }
    public Credito getCredito()                  { return credito; }
    public List<CuotaAmortizacion> getTabla()    { return tabla; }
    public Integer getPlazoMinimoSugerido()      { return plazoMinimoSugerido; }
    public Integer getPlazoMaximoSugerido()      { return plazoMaximoSugerido; }

    // --- Helpers para la JSP ---

    public boolean isExitoso() {
        return tipo == Tipo.EXITO;
    }

    public boolean isRechazado() {
        return tipo == Tipo.RECHAZO;
    }

    public boolean isErrorValidacion() {
        return tipo == Tipo.ERROR_VALIDACION;
    }

    public boolean requiereReintento() {
        return plazoMinimoSugerido != null || plazoMaximoSugerido != null;
    }

    public boolean isAprobado() {
        return credito != null && credito.getEstado() == EstadoCredito.APROBADO;
    }

    @Override
    public String toString() {
        return "ResultadoCredito{" +
                "tipo=" + tipo +
                ", mensaje='" + mensaje + '\'' +
                ", creditoId=" + (credito != null ? credito.getId() : null) +
                ", filasTabla=" + tabla.size() +
                '}';
    }
}