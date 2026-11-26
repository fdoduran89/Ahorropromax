package com.mycompany.ahorropromax.servicios;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.persistencia.EmpleadoCorporativoDAO;

import java.util.List;

/**
 * Servicio de gestión de empleados corporativos (sujetos de libranza).
 *
 * Encapsula la lógica de negocio:
 *   - validar que la empresa exista
 *   - validar que la identificación no esté duplicada
 *   - coordinar el DAO
 *
 * Los Servlets llaman a este servicio, no al DAO directamente.
 */
public class ServicioEmpleadoCorporativo {

    private final EmpleadoCorporativoDAO dao = new EmpleadoCorporativoDAO();
    private final ServicioClienteCorporativo servicioCliente = new ServicioClienteCorporativo();

    /**
     * Registra un empleado. Valida:
     *   - empleado no nulo
     *   - empresa existente
     *   - identificación no duplicada
     *
     * @param emp        el empleado (sin id)
     * @param idEmpresa  el id de la empresa a la que pertenece
     * @return el empleado con id asignado
     * @throws IllegalArgumentException si hay error de validación
     * @throws RuntimeException si hay error de BD
     */
    public EmpleadoCorporativo registrar(EmpleadoCorporativo emp, Long idEmpresa) {
        if (emp == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo");
        }
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException("Debe indicar la empresa");
        }

        // 1. Verificar que la empresa exista
        ClienteCorporativo empresa = servicioCliente.buscarPorId(idEmpresa);
        if (empresa == null) {
            throw new IllegalArgumentException(
                "No existe una empresa con id " + idEmpresa);
        }

        // 2. Verificar identificación duplicada
        if (existeIdentificacion(emp.getIdentificacion())) {
            throw new IllegalArgumentException(
                "Ya existe una persona con identificación " + emp.getIdentificacion());
        }

        // 3. Asociar la empresa al empleado
        emp.setClienteCorporativo(empresa);

        // 4. Insertar
        return dao.insertar(emp, empresa);
    }

    public EmpleadoCorporativo buscarPorIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación es obligatoria");
        }
        return dao.buscarPorIdentificacion(identificacion);
    }

    public List<EmpleadoCorporativo> listarPorEmpresa(Long idEmpresa) {
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException("El id de empresa es obligatorio");
        }
        return dao.listarPorEmpresa(idEmpresa);
    }

    public boolean existeIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            return false;
        }
        return dao.buscarPorIdentificacion(identificacion) != null;
    }

    public boolean actualizar(EmpleadoCorporativo emp) {
        if (emp == null || emp.getId() == null) {
            throw new IllegalArgumentException("El empleado y su ID son obligatorios");
        }
        return dao.actualizar(emp);
    }

    public boolean eliminar(Long idPersona) {
        if (idPersona == null || idPersona <= 0) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
        return dao.eliminar(idPersona);
    }
}