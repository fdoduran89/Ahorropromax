package com.mycompany.ahorropromax.controlador;

import com.mycompany.ahorropromax.negocio.Asesor;
import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.negocio.TipoCredito;
import com.mycompany.ahorropromax.servicios.ResultadoCredito;
import com.mycompany.ahorropromax.servicios.ServicioClienteCorporativo;
import com.mycompany.ahorropromax.servicios.ServicioCredito;
import com.mycompany.ahorropromax.servicios.ServicioEmpleadoCorporativo;
import com.mycompany.ahorropromax.persistencia.AsesorDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ServCredito extends HttpServlet {

    private ServicioCredito getServicioCredito() {
        return new ServicioCredito();
    }

    private ServicioEmpleadoCorporativo getServicioEmpleado() {
        return new ServicioEmpleadoCorporativo();
    }

    private ServicioClienteCorporativo getServicioEmpresa() {
        return new ServicioClienteCorporativo();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null || path.equals("/") || path.equals("/registrar")) {
            mostrarFormulario(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();
        
        if ("/registrar".equals(path)) {
            procesarCredito(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Cargar todas las empresas
            List<ClienteCorporativo> empresas = getServicioEmpresa().listar();

            // 2. Cargar todos los empleados de todas las empresas
            List<EmpleadoCorporativo> empleados = new ArrayList<>();
            for (ClienteCorporativo emp : empresas) {
                empleados.addAll(getServicioEmpleado().listarPorEmpresa(emp.getId()));
            }
            request.setAttribute("empleados", empleados);

            // 3. Cargar los asesores
            AsesorDAO asesorDAO = new AsesorDAO();
            List<Asesor> asesores = asesorDAO.listar();
            request.setAttribute("asesores", asesores);

            // 4. Cargar los tipos de crédito
            request.setAttribute("tiposCredito", TipoCredito.values());

            request.getRequestDispatcher("/WEB-INF/views/credito/registroCredito.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error cargando el formulario: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void procesarCredito(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String identificacionEmpleado = request.getParameter("identificacionEmpleado");
            String identificacionAsesor = request.getParameter("identificacionAsesor");
            String tipoStr = request.getParameter("tipoCredito");
            BigDecimal monto = new BigDecimal(request.getParameter("monto"));
            int anios = Integer.parseInt(request.getParameter("plazoAnios"));

            TipoCredito tipo = TipoCredito.valueOf(tipoStr);

            ResultadoCredito resultado = getServicioCredito().registrarCredito(
                    identificacionEmpleado,
                    identificacionAsesor,
                    tipo,
                    monto,
                    anios
            );

            if (resultado.isExitoso()) {
                request.setAttribute("resultado", resultado);
                request.setAttribute("credito", resultado.getCredito());
                request.setAttribute("tabla", resultado.getTabla());
                request.getRequestDispatcher("/WEB-INF/views/credito/resultadoCredito.jsp").forward(request, response);
            } else {
                request.setAttribute("error", resultado.getMensaje());
                mostrarFormulario(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error procesando el crédito: " + e.getMessage());
            mostrarFormulario(request, response);
        }
    }
}