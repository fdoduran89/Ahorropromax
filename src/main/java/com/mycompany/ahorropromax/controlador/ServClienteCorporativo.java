package com.mycompany.ahorropromax.controlador;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;
import com.mycompany.ahorropromax.servicios.ServicioClienteCorporativo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ServClienteCorporativo extends HttpServlet {

    // ¡OJO! Ya no creamos el servicio a nivel de clase.
    // Se crea dentro de cada método para evitar fallos al arrancar Tomcat.

    private ServicioClienteCorporativo getServicio() {
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
                request.getRequestDispatcher("/empresa/registroEmpresa.jsp").forward(request, response);
                break;
            case "/listar":
                listarEmpresas(request, response);
                break;
            case "/ver":
                verDetalle(request, response);
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
            registrarEmpresa(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void registrarEmpresa(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nit         = request.getParameter("nit");
        String razonSocial = request.getParameter("razonSocial");
        String sector      = request.getParameter("sector");
        String direccion   = request.getParameter("direccion");
        String telefono    = request.getParameter("telefono");
        String correo      = request.getParameter("correo");

        try {
            ClienteCorporativo empresa = new ClienteCorporativo(nit, razonSocial, sector);
            empresa.setDireccion(direccion);
            empresa.setTelefono(telefono);
            empresa.setCorreo(correo);

            getServicio().registrar(empresa);

            request.setAttribute("mensaje", "Empresa registrada con éxito: " + empresa.getRazonSocial());
            listarEmpresas(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/empresa/registroEmpresa.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error interno: " + e.getMessage());
            request.getRequestDispatcher("/empresa/registroEmpresa.jsp").forward(request, response);
        }
    }

    private void listarEmpresas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<ClienteCorporativo> empresas = getServicio().listar();
            request.setAttribute("empresas", empresas);
            request.getRequestDispatcher("/empresa/listaEmpresas.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error listando empresas: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void verDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el id");
            return;
        }
        try {
            Long id = Long.parseLong(idStr);
            ClienteCorporativo empresa = getServicio().buscarPorId(id);
            if (empresa == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Empresa no encontrada");
                return;
            }
            request.setAttribute("empresa", empresa);
            request.getRequestDispatcher("/empresa/detalleEmpresa.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
}