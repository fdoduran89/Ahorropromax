package com.mycompany.ahorropromax.controlador;

import com.mycompany.ahorropromax.persistencia.ConexionDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//@WebServlet("/prueba-conexion")
public class ServPruebaConexion extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head>");
            out.println("<meta charset='UTF-8'><title>Prueba de conexión</title>");
            out.println("<style>body{font-family:sans-serif;padding:20px;}");
            out.println(".ok{color:green;font-weight:bold;}");
            out.println(".err{color:red;font-weight:bold;}</style></head><body>");
            out.println("<h1>Prueba de conexión - AhorroPromax</h1>");

            try (Connection cn = ConexionDB.getConexion()) {
                out.println("<p class='ok'>✅ Conexión a MySQL exitosa</p>");
                out.println("<p>URL: " + cn.getMetaData().getURL() + "</p>");
                out.println("<p>DB: " + cn.getMetaData().getDatabaseProductName()
                        + " " + cn.getMetaData().getDatabaseProductVersion() + "</p>");

                out.println("<h2>Tipos de crédito en la BD</h2>");
                out.println("<table border='1' cellpadding='8'>");
                out.println("<tr><th>ID</th><th>Nombre</th><th>Tasa mensual</th>"
                        + "<th>Plazo mín (m)</th><th>Plazo máx (m)</th></tr>");

                try (Statement st = cn.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT id, nombre, tasa_mensual, " +
                         "plazo_minimo_meses, plazo_maximo_meses FROM tipo_credito")) {
                    while (rs.next()) {
                        out.println("<tr>"
                                + "<td>" + rs.getInt("id") + "</td>"
                                + "<td>" + rs.getString("nombre") + "</td>"
                                + "<td>" + rs.getBigDecimal("tasa_mensual") + "</td>"
                                + "<td>" + rs.getObject("plazo_minimo_meses") + "</td>"
                                + "<td>" + rs.getObject("plazo_maximo_meses") + "</td>"
                                + "</tr>");
                    }
                }
                out.println("</table>");
                out.println("<p class='ok'>✅ Consulta ejecutada correctamente</p>");

            } catch (Exception e) {
                out.println("<p class='err'>❌ Error: " + e.getMessage() + "</p>");
                out.println("<pre>");
                e.printStackTrace(out);
                out.println("</pre>");
            }

            out.println("</body></html>");
        }
    }
}