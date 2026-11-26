package com.mycompany.ahorropromax.negocio;

import java.util.Objects;

/**
 * Empresa que tiene convenio de libranza con el banco.
 *
 * No hereda de Persona: una empresa no es una persona. Se asocia N:1 con
 * EmpleadoCorporativo (una empresa tiene muchos empleados).
 *
 * Encapsulamiento: - atributos privados - setters con validación
 */
public class ClienteCorporativo {

    private Long id;
    private String nit;
    private String razonSocial;
    private String sector;
    private String direccion;
    private String telefono;
    private String correo;

    // -------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------
    public ClienteCorporativo() {
    }

    public ClienteCorporativo(String nit, String razonSocial, String sector) {
        setNit(nit);
        setRazonSocial(razonSocial);
        setSector(sector);
    }

    // -------------------------------------------------------------------
    // Getters y setters con validación
    // -------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public final void setNit(String nit) {
        if (nit == null || nit.trim().isEmpty()) {
            throw new IllegalArgumentException("El NIT no puede estar vacío");
        }
        this.nit = nit.trim();
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public final void setRazonSocial(String razonSocial) {
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new IllegalArgumentException("La razón social no puede estar vacía");
        }
        this.razonSocial = razonSocial.trim();
    }

    public String getSector() {
        return sector;
    }

    public final void setSector(String sector) {
        if (sector == null || sector.trim().isEmpty()) {
            throw new IllegalArgumentException("El sector no puede estar vacío");
        }
        this.sector = sector.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // -------------------------------------------------------------------
    // equals / hashCode / toString
    // -------------------------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClienteCorporativo)) {
            return false;
        }
        ClienteCorporativo that = (ClienteCorporativo) o;
        return Objects.equals(nit, that.nit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nit);
    }

    @Override
    public String toString() {
        return "ClienteCorporativo{"
                + "nit='" + nit + '\''
                + ", razonSocial='" + razonSocial + '\''
                + ", sector='" + sector + '\''
                + '}';
    }
}
