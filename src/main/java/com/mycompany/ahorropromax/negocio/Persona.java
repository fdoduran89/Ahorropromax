package com.mycompany.ahorropromax.negocio;

import java.util.Objects;

/**
 * Clase base abstracta para todas las personas del sistema.
 *
 * Encapsulamiento: - atributos privados - setters con validación (no se acepta
 * null ni vacío en identificación) - getters públicos
 *
 * Abstracción: - no se puede instanciar directamente (abstract) - obliga a las
 * subclases a implementar getRol()
 */
public abstract class Persona {

    private Long id;
    private String identificacion;
    private String nombres;
    private String apellidos;

    // -------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------
    protected Persona() {
        // constructor vacío para frameworks / DAOs
    }

    protected Persona(String identificacion, String nombres, String apellidos) {
        setIdentificacion(identificacion);
        setNombres(nombres);
        setApellidos(apellidos);
    }

    // -------------------------------------------------------------------
    // Getters y setters con encapsulamiento
    // -------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public final void setIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación no puede estar vacía");
        }
        this.identificacion = identificacion.trim();
    }

    public String getNombres() {
        return nombres;
    }

    public final void setNombres(String nombres) {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres no pueden estar vacíos");
        }
        this.nombres = nombres.trim();
    }

    public String getApellidos() {
        return apellidos;
    }

    public final void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos no pueden estar vacíos");
        }
        this.apellidos = apellidos.trim();
    }

    // -------------------------------------------------------------------
    // Comportamiento común
    // -------------------------------------------------------------------
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * Cada subclase debe indicar su rol en el dominio. Se usa en reportes y en
     * la interfaz.
     */
    public abstract String getRol();

    // -------------------------------------------------------------------
    // equals / hashCode / toString
    // -------------------------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Persona)) {
            return false;
        }
        Persona persona = (Persona) o;
        return Objects.equals(identificacion, persona.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    @Override
    public String toString() {
        return getRol() + "{"
                + "identificacion='" + identificacion + '\''
                + ", nombreCompleto='" + getNombreCompleto() + '\''
                + '}';
    }
}
