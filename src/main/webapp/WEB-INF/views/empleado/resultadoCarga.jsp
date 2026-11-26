<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1>Resumen de Carga Masiva</h1>

    <div style="background: #f9fafb; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
        <p><strong>Total de filas procesadas:</strong> ${resultado.totalFilas}</p>
        <p style="color: #047857;"><strong>Insertados exitosamente:</strong> ${resultado.totalInsertados}</p>
        <p style="color: #b91c1c;"><strong>Errores:</strong> ${resultado.totalErrores}</p>
    </div>

    <c:if test="${resultado.totalInsertados > 0}">
        <h2>Empleados Insertados</h2>
        <table class="table">
            <thead>
                <tr><th>Identificación</th><th>Nombre</th><th>Cargo</th></tr>
            </thead>
            <tbody>
                <c:forEach var="emp" items="${resultado.insertados}">
                    <tr>
                        <td>${emp.identificacion}</td>
                        <td>${emp.nombreCompleto}</td>
                        <td>${emp.cargo}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <c:if test="${resultado.totalErrores > 0}">
        <h2 style="color: #b91c1c;">Errores Encontrados</h2>
        <table class="table" style="border-color: #fecaca;">
            <thead style="background: #fecaca; color: #991b1b;">
                <tr><th>Fila</th><th>Identificación</th><th>Mensaje</th></tr>
            </thead>
            <tbody>
                <c:forEach var="err" items="${resultado.errores}">
                    <tr>
                        <td>${err.numeroFila}</td>
                        <td>${err.identificacion}</td>
                        <td>${err.mensaje}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div style="margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/empleado/carga-masiva" class="btn btn-gold">Subir otro archivo</a>
        <a href="${pageContext.request.contextPath}/empleado/listar" class="btn" style="background: #6b7280; margin-left: 10px;">Ver Empleados</a>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />