package com.mycompany.ahorropromax.negocio;

/**
 * Estado de un crédito.
 *
 * PENDIENTE  → recién registrado, sin evaluar
 * APROBADO   → superó la evaluación financiera
 * RECHAZADO  → no superó la evaluación (guarda motivo)
 */
public enum EstadoCredito {
    PENDIENTE,
    APROBADO,
    RECHAZADO
}