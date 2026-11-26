package com.mycompany.ahorropromax.controlador;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filtro global para forzar UTF-8 en todas las peticiones y respuestas.
 * Evita problemas con tildes y caracteres especiales (ñ, á, é, etc).
 */
@WebFilter("/*")
public class FiltroUTF8 implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nada
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=UTF-8");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // nada
    }
}