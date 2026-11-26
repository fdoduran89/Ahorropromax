package com.mycompany.ahorropromax.controlador;

import com.mycompany.ahorropromax.servicios.ResultadoCargaMasiva;
import com.mycompany.ahorropromax.servicios.ServicioExcel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

@WebServlet("/empleado/carga-masiva")
@MultipartConfig
public class ServCargaMasiva extends HttpServlet {

    private final ServicioExcel servicioExcel = new ServicioExcel();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Muestra el formulario de subida
        request.getRequestDispatcher("/WEB-INF/views/empleado/cargaEmpleados.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // 1. Obtener el ID de la empresa y el archivo
            Long idEmpresa = Long.parseLong(request.getParameter("idEmpresa"));
            Part filePart = request.getPart("archivoExcel");

            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "Debe seleccionar un archivo Excel válido.");
                doGet(request, response);
                return;
            }

            // 2. Convertir el archivo a InputStream y llamar al servicio
            try (InputStream inputStream = filePart.getInputStream()) {
                ResultadoCargaMasiva resultado = servicioExcel.cargar(inputStream, idEmpresa);
                
                // 3. Poner el resultado en el request para mostrarlo en la JSP
                request.setAttribute("resultado", resultado);
                request.getRequestDispatcher("/WEB-INF/views/empleado/resultadoCarga.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar el archivo: " + e.getMessage());
            doGet(request, response);
        }
    }
}