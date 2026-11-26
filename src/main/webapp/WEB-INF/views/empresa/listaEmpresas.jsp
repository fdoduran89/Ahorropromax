<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1>Empresas Registradas</h1>
    
    <c:if test="${not empty mensaje}">
        <div class="alert alert-success">${mensaje}</div>
    </c:if>
    
    <a href="${pageContext.request.contextPath}/empresa/registrar" class="btn btn-gold" style="margin-bottom: 20px;">
        + Registrar nueva empresa
    </a>

    <table class="table">
        <thead>
            <tr>
                <th>ID</th><th>NIT</th><th>Razón social</th><th>Sector</th><th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="emp" items="${empresas}">
                <tr>
                    <td>${emp.id}</td>
                    <td>${emp.nit}</td>
                    <td><strong>${emp.razonSocial}</strong></td>
                    <td>${emp.sector}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/empresa/ver?id=${emp.id}" style="color: var(--bbc-gold); font-weight: bold; text-decoration: none;">
                            Ver detalle
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/includes/footer.jsp" />