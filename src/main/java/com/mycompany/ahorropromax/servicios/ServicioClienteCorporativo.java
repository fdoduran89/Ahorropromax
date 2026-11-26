package com.mycompany.ahorropromax.servicios;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.persistencia.ClienteCorporativoDAO;

import java.util.List;

/**
 * Servicio de gestión de clientes corporativos (empresas).
 *
 * Encapsula la lógica de negocio:
 *   - validar NIT único antes de insertar
 *   - validar campos obligatorios
 *   - coordinar el DAO
 *
 * Los Servlets llaman a este servicio, no al DAO directamente.
 */
public class ServicioClienteCorporativo {

    private final ClienteCorporativoDAO dao = new ClienteCorporativoDAO();

    /**
     * Registra una empresa. Valida:
     *   - NIT no vacío
     *   - Razón social no vacía
     *   - Sector no vacío
     *   - NIT no duplicado en BD
     *
     * @return la empresa con id asignado
     * @throws IllegalArgumentException si hay error de validación
     * @throws RuntimeException si hay error de BD
     */
    public ClienteCorporativo registrar(ClienteCorporativo empresa) {
        // 1. Validaciones básicas (el modelo ya las hace, pero reforzamos)
        if (empresa == null) {
            throw new IllegalArgumentException("La empresa no puede ser nula");
        }

        // 2. Verificar que el NIT no exista ya
        ClienteCorporativo existente = dao.buscarPorNit(empresa.getNit());
        if (existente != null) {
            throw new IllegalArgumentException(
                "Ya existe una empresa con NIT " + empresa.getNit()
                + " (razón social: " + existente.getRazonSocial() + ")");
        }

        // 3. Insertar
        return dao.insertar(empresa);
    }

    public ClienteCorporativo buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
        return dao.buscarPorId(id);
    }

    public ClienteCorporativo buscarPorNit(String nit) {
        if (nit == null || nit.trim().isEmpty()) {
            throw new IllegalArgumentException("El NIT es obligatorio");
        }
        return dao.buscarPorNit(nit);
    }

    public List<ClienteCorporativo> listar() {
        return dao.listar();
    }

    public boolean actualizar(ClienteCorporativo empresa) {
        if (empresa == null || empresa.getId() == null) {
            throw new IllegalArgumentException("La empresa y su ID son obligatorios");
        }
        return dao.actualizar(empresa);
    }

    public boolean eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
        return dao.eliminar(id);
    }
}