<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1 style="color: var(--bbc-navy); margin-top: 0;">Empleados Corporativos</h1>
    
    <c:if test="${not empty mensaje}">
        <div class="alert-success">${mensaje}</div>
    </c:if>
    
    <a href="${pageContext.request.contextPath}/empleado/registrar" class="btn-gold" style="margin-bottom: 20px;">
        + Registrar nuevo empleado
    </a>

    <table class="table">
        <thead>
            <tr>
                <th>Identificación</th>
                <th>Nombre Completo</th>
                <th>Empresa</th>
                <th>Cargo</th>
                <th>Salario Básico</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="emp" items="${empleados}">
                <tr>
                    <td>${emp.identificacion}</td>
                    <td><strong>${emp.nombreCompleto}</strong></td>
                    <td>${emp.clienteCorporativo.razonSocial}</td>
                    <td>${emp.cargo}</td>
                    <td style="color: #065f46; font-weight: bold;">
                        $${emp.salarioBasico}
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty empleados}">
                <tr>
                    <td colspan="5" style="text-align: center; padding: 20px; color: #6b7280;">
                        No hay empleados registrados todavía.
                    </td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/includes/footer.jsp" />