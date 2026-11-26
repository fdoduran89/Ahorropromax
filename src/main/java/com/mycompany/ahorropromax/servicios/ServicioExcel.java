package com.mycompany.ahorropromax.servicios;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Servicio de carga masiva de empleados desde Excel (.xlsx).
 *
 * Formato esperado del archivo (fila 0 = encabezados):
 *   A: identificación
 *   B: nombres
 *   C: apellidos
 *   D: cargo
 *   E: salario básico
 *   F: ingresos mensuales
 *   G: gastos mensuales
 *
 * Valida cada fila:
 *   - campos obligatorios
 *   - identificación no duplicada (ni en BD ni en el archivo)
 *   - empresa existente
 *   - valores numéricos > 0
 *
 * Devuelve un ResultadoCargaMasiva con el resumen.
 */
public class ServicioExcel {

    private final ServicioEmpleadoCorporativo servicioEmpleado;

    public ServicioExcel() {
        this.servicioEmpleado = new ServicioEmpleadoCorporativo();
    }

    /**
     * Carga masiva desde un InputStream de Excel.
     *
     * @param excel      InputStream del archivo .xlsx
     * @param idEmpresa  la empresa a la que pertenecen todos los empleados
     * @return ResultadoCargaMasiva con insertados + errores
     */
    public ResultadoCargaMasiva cargar(InputStream excel, Long idEmpresa) {
        if (excel == null) {
            throw new IllegalArgumentException("El archivo Excel no puede ser nulo");
        }
        if (idEmpresa == null || idEmpresa <= 0) {
            throw new IllegalArgumentException("Debe indicar la empresa");
        }

        // Verificar que la empresa exista
        ServicioClienteCorporativo servicioCliente = new ServicioClienteCorporativo();
        ClienteCorporativo empresa = servicioCliente.buscarPorId(idEmpresa);
        if (empresa == null) {
            throw new IllegalArgumentException("No existe la empresa con id " + idEmpresa);
        }

        ResultadoCargaMasiva resultado;
        try (Workbook workbook = new XSSFWorkbook(excel)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalFilas = sheet.getLastRowNum(); // sin contar encabezado
            resultado = new ResultadoCargaMasiva(totalFilas);

            // Saltamos la fila 0 (encabezados)
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // skip header
            }

            int numeroFila = 2; // los datos empiezan en la fila 2 (después de encabezado)
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                procesarFila(row, numeroFila, empresa, idEmpresa, resultado);
                numeroFila++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo el archivo Excel: " + e.getMessage(), e);
        }

        return resultado;
    }

    private void procesarFila(Row row, int numeroFila, ClienteCorporativo empresa,
                              Long idEmpresa, ResultadoCargaMasiva resultado) {
        try {
            String identificacion = leerString(row, 0);
            String nombres        = leerString(row, 1);
            String apellidos      = leerString(row, 2);
            String cargo          = leerString(row, 3);
            BigDecimal salario    = leerBigDecimal(row, 4);
            BigDecimal ingresos   = leerBigDecimal(row, 5);
            BigDecimal gastos     = leerBigDecimal(row, 6);

            // Validaciones básicas
            if (identificacion == null) {
                resultado.agregarError(numeroFila, "(vacío)",
                        "Identificación obligatoria");
                return;
            }
            if (nombres == null || apellidos == null) {
                resultado.agregarError(numeroFila, identificacion,
                        "Nombres y apellidos obligatorios");
                return;
            }
            if (cargo == null) {
                resultado.agregarError(numeroFila, identificacion,
                        "Cargo obligatorio");
                return;
            }
            if (salario == null || salario.signum() < 0) {
                resultado.agregarError(numeroFila, identificacion,
                        "Salario inválido");
                return;
            }
            if (ingresos == null || ingresos.signum() < 0) {
                resultado.agregarError(numeroFila, identificacion,
                        "Ingresos inválidos");
                return;
            }
            if (gastos == null || gastos.signum() < 0) {
                resultado.agregarError(numeroFila, identificacion,
                        "Gastos inválidos");
                return;
            }

            // Crear el empleado
            EmpleadoCorporativo emp = new EmpleadoCorporativo(
                    identificacion, nombres, apellidos, empresa, cargo,
                    salario, ingresos, gastos);

            // Insertar vía servicio (valida duplicados, empresa, etc.)
            servicioEmpleado.registrar(emp, idEmpresa);
            resultado.agregarInsertado(emp);

        } catch (IllegalArgumentException e) {
            resultado.agregarError(numeroFila, leerString(row, 0), e.getMessage());
        } catch (Exception e) {
            resultado.agregarError(numeroFila, leerString(row, 0),
                    "Error inesperado: " + e.getMessage());
        }
    }

    // --- Helpers de lectura de celda ---

    private String leerString(Row row, int columna) {
        Cell cell = row.getCell(columna);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) {
            String s = cell.getStringCellValue();
            return (s == null || s.trim().isEmpty()) ? null : s.trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return null;
    }

    private BigDecimal leerBigDecimal(Row row, int columna) {
        Cell cell = row.getCell(columna);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return new BigDecimal(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}