package com.mycompany.ahorropromax.servicios;

import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de una carga masiva de empleados desde Excel.
 *
 * Encapsula:
 *   - los empleados insertados exitosamente
 *   - los errores por fila (con número de fila y mensaje)
 *   - el total de filas procesadas
 *
 * Los Servlets usan este objeto para mostrar el resumen al usuario.
 */
public class ResultadoCargaMasiva {

    public static class ErrorFila {
        private final int numeroFila;
        private final String identificacion;
        private final String mensaje;

        public ErrorFila(int numeroFila, String identificacion, String mensaje) {
            this.numeroFila = numeroFila;
            this.identificacion = identificacion;
            this.mensaje = mensaje;
        }

        public int getNumeroFila()              { return numeroFila; }
        public String getIdentificacion()       { return identificacion; }
        public String getMensaje()              { return mensaje; }

        @Override
        public String toString() {
            return "Fila " + numeroFila + " (" + identificacion + "): " + mensaje;
        }
    }

    private final List<EmpleadoCorporativo> insertados;
    private final List<ErrorFila> errores;
    private final int totalFilas;

    public ResultadoCargaMasiva(int totalFilas) {
        this.totalFilas = totalFilas;
        this.insertados = new ArrayList<>();
        this.errores = new ArrayList<>();
    }

    public void agregarInsertado(EmpleadoCorporativo emp) {
        insertados.add(emp);
    }

    public void agregarError(int numeroFila, String identificacion, String mensaje) {
        errores.add(new ErrorFila(numeroFila, identificacion, mensaje));
    }

    public List<EmpleadoCorporativo> getInsertados() {
        return Collections.unmodifiableList(insertados);
    }

    public List<ErrorFila> getErrores() {
        return Collections.unmodifiableList(errores);
    }

    public int getTotalFilas()                  { return totalFilas; }
    public int getTotalInsertados()             { return insertados.size(); }
    public int getTotalErrores()                { return errores.size(); }
    public boolean isTodoExitoso()              { return errores.isEmpty(); }

    @Override
    public String toString() {
        return "ResultadoCargaMasiva{" +
                "total=" + totalFilas +
                ", insertados=" + insertados.size() +
                ", errores=" + errores.size() +
                '}';
    }
}