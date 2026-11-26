package com.mycompany.ahorropromax.servicios;

import com.mycompany.ahorropromax.negocio.Asesor;
import com.mycompany.ahorropromax.negocio.Credito;
import com.mycompany.ahorropromax.negocio.CuotaAmortizacion;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.negocio.EstadoCredito;
import com.mycompany.ahorropromax.negocio.TipoCredito;
import com.mycompany.ahorropromax.persistencia.AsesorDAO;
import com.mycompany.ahorropromax.persistencia.CreditoDAO;
import com.mycompany.ahorropromax.persistencia.CuotaAmortizacionDAO;
import com.mycompany.ahorropromax.persistencia.EmpleadoCorporativoDAO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de registro de créditos.
 *
 * Orquesta todo el flujo:
 *   1. Validar plazo
 *   2. Buscar empleado y asesor
 *   3. Crear crédito + calcular cuota
 *   4. Evaluar (RN4)
 *   5. Insertar crédito
 *   6. Si aprobado: generar tabla + insertar cuotas
 *   7. Devolver ResultadoCredito
 */
public class ServicioCredito {

    private final CreditoDAO creditoDAO = new CreditoDAO();
    private final CuotaAmortizacionDAO cuotaDAO = new CuotaAmortizacionDAO();
    private final EmpleadoCorporativoDAO empleadoDAO = new EmpleadoCorporativoDAO();
    private final AsesorDAO asesorDAO = new AsesorDAO();

    /**
     * Registra un crédito completo.
     *
     * @param identificacionEmpleado identificación del empleado corporativo
     * @param identificacionAsesor   identificación del asesor
     * @param tipo                   tipo de crédito
     * @param monto                  monto solicitado
     * @param aniosSolicitados       plazo en años
     * @return ResultadoCredito con el crédito, la tabla (si aprobado), o el error
     */
    public ResultadoCredito registrarCredito(String identificacionEmpleado,
                                              String identificacionAsesor,
                                              TipoCredito tipo,
                                              BigDecimal monto,
                                              int aniosSolicitados) {

        // -----------------------------------------------------------------
        // 1. Validaciones básicas
        // -----------------------------------------------------------------
        if (identificacionEmpleado == null || identificacionEmpleado.trim().isEmpty()) {
            return ResultadoCredito.errorValidacion("La identificación del empleado es obligatoria",
                    null, null);
        }
        if (identificacionAsesor == null || identificacionAsesor.trim().isEmpty()) {
            return ResultadoCredito.errorValidacion("La identificación del asesor es obligatoria",
                    null, null);
        }
        if (tipo == null) {
            return ResultadoCredito.errorValidacion("El tipo de crédito es obligatorio",
                    null, null);
        }
        if (monto == null || monto.signum() <= 0) {
            return ResultadoCredito.errorValidacion("El monto debe ser mayor que cero",
                    null, null);
        }
        if (aniosSolicitados <= 0) {
            return ResultadoCredito.errorValidacion("El plazo debe ser mayor que cero",
                    null, null);
        }

        int plazoMeses = aniosSolicitados * 12;

        // -----------------------------------------------------------------
        // 2. Validar plazo contra el tipo
        // -----------------------------------------------------------------
        if (!tipo.esPlazoValido(plazoMeses)) {
            return ResultadoCredito.errorValidacion(
                    "El plazo de " + aniosSolicitados + " años no es válido para " + tipo
                    + ". " + tipo.describirLimitesPlazo() + ".",
                    tipo.getPlazoMinimoMeses() != null ? tipo.getPlazoMinimoMeses() / 12 : null,
                    tipo.getPlazoMaximoMeses() != null ? tipo.getPlazoMaximoMeses() / 12 : null);
        }

        // -----------------------------------------------------------------
        // 3. Buscar empleado y asesor
        // -----------------------------------------------------------------
        EmpleadoCorporativo empleado = empleadoDAO.buscarPorIdentificacion(identificacionEmpleado);
        if (empleado == null) {
            return ResultadoCredito.errorInterno(
                    "No existe un empleado con identificación " + identificacionEmpleado);
        }

        Asesor asesor = asesorDAO.buscarPorIdentificacion(identificacionAsesor);
        if (asesor == null) {
            return ResultadoCredito.errorInterno(
                    "No existe un asesor con identificación " + identificacionAsesor);
        }

        // -----------------------------------------------------------------
        // 4. Crear crédito y calcular cuota
        // -----------------------------------------------------------------
        Credito credito;
        try {
            credito = new Credito(empleado, asesor, tipo, monto, plazoMeses);
            credito.calcularCuota();
        } catch (IllegalArgumentException e) {
            return ResultadoCredito.errorValidacion(e.getMessage(), null, null);
        }

        // -----------------------------------------------------------------
        // 5. Evaluar (RN4)
        // -----------------------------------------------------------------
        EstadoCredito estado = credito.evaluar();

        // -----------------------------------------------------------------
        // 6. Persistir crédito
        // -----------------------------------------------------------------
        try {
            creditoDAO.insertar(credito);
        } catch (Exception e) {
            return ResultadoCredito.errorInterno(
                    "Error guardando el crédito: " + e.getMessage());
        }

        // -----------------------------------------------------------------
        // 7. Si aprobado: generar y persistir tabla
        // -----------------------------------------------------------------
        if (estado == EstadoCredito.APROBADO) {
            try {
                List<CuotaAmortizacion> tabla = credito.generarTablaAmortizacion();
                cuotaDAO.insertarLote(tabla, credito.getId());
                return ResultadoCredito.exito(credito, tabla);
            } catch (Exception e) {
                return ResultadoCredito.errorInterno(
                        "El crédito fue aprobado pero falló la generación de la tabla: "
                        + e.getMessage());
            }
        }

        // -----------------------------------------------------------------
        // 8. Crédito rechazado
        // -----------------------------------------------------------------
        return ResultadoCredito.rechazo(credito, credito.getMotivoRechazo());
    }

    /**
     * Lista créditos por asesor.
     */
    public List<Credito> listarPorAsesor(String identificacionAsesor) {
        Asesor asesor = asesorDAO.buscarPorIdentificacion(identificacionAsesor);
        if (asesor == null) {
            throw new IllegalArgumentException(
                "No existe un asesor con identificación " + identificacionAsesor);
        }
        return creditoDAO.listarPorAsesor(asesor.getId());
    }

    /**
     * Lista créditos aprobados en un período.
     */
    public List<Credito> listarAprobadosPorPeriodo(String periodo) {
        return creditoDAO.listarAprobadosPorPeriodo(periodo);
    }

    /**
     * Busca un crédito por ID con toda su información.
     */
    public Credito buscarPorId(Long id) {
        return creditoDAO.buscarPorId(id);
    }
}