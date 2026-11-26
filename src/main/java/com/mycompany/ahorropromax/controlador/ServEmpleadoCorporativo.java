package com.mycompany.ahorropromax.controlador;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.negocio.EmpleadoCorporativo;
import com.mycompany.ahorropromax.servicios.ServicioClienteCorporativo;
import com.mycompany.ahorropromax.servicios.ServicioEmpleadoCorporativo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ServEmpleadoCorporativo extends HttpServlet {

    private ServicioEmpleadoCorporativo getServicioEmp() {
        return new ServicioEmpleadoCorporativo();
    }

    private ServicioClienteCorporativo getServicioEmpresa() {
        return new ServicioClienteCorporativo();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/listar";
        }

        switch (path) {
            case "/registrar":
                mostrarFormulario(request, response);
                break;
            case "/listar":
                listarEmpleados(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if ("/registrar".equals(path)) {
            registrarEmpleado(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<ClienteCorporativo> empresas = getServicioEmpresa().listar();
            request.setAttribute("empresas", empresas);
            request.getRequestDispatcher("/empleado/registroEmpleado.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error cargando empresas: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void registrarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String identificacion = request.getParameter("identificacion");
            String nombres        = request.getParameter("nombres");
            String apellidos      = request.getParameter("apellidos");
            String cargo          = request.getParameter("cargo");
            String salarioStr     = request.getParameter("salarioBasico");
            String ingresosStr    = request.getParameter("ingresosMensuales");
            String gastosStr      = request.getParameter("gastosMensuales");
            Long idEmpresa        = Long.parseLong(request.getParameter("idEmpresa"));

            ClienteCorporativo empresa = getServicioEmpresa().buscarPorId(idEmpresa);

            EmpleadoCorporativo emp = new EmpleadoCorporativo(
                    identificacion, nombres, apellidos, empresa, cargo,
                    new BigDecimal(salarioStr),
                    new BigDecimal(ingresosStr),
                    new BigDecimal(gastosStr)
            );

            getServicioEmp().registrar(emp, idEmpresa);

            request.setAttribute("mensaje", "Empleado registrado con éxito: " + emp.getNombreCompleto());
            listarEmpleados(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al registrar: " + e.getMessage());
            List<ClienteCorporativo> empresas = getServicioEmpresa().listar();
            request.setAttribute("empresas", empresas);
            request.getRequestDispatcher("/empleado/registroEmpleado.jsp").forward(request, response);
        }
    }

    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<ClienteCorporativo> empresas = getServicioEmpresa().listar();
            java.util.List<EmpleadoCorporativo> todosLosEmpleados = new java.util.ArrayList<>();
            
            for (ClienteCorporativo c : empresas) {
                todosLosEmpleados.addAll(getServicioEmp().listarPorEmpresa(c.getId()));
            }

            request.setAttribute("empleados", todosLosEmpleados);
            request.getRequestDispatcher("/empleado/listaEmpleados.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error listando empleados: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}