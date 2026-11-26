<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1>Registrar Empresa</h1>
    
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/empresa/registrar" method="post">
        <div class="form-group">
            <label>NIT *</label>
            <input type="text" name="nit" class="form-control" required value="${nit}">
        </div>
        <div class="form-group">
            <label>Razón social *</label>
            <input type="text" name="razonSocial" class="form-control" required value="${razonSocial}">
        </div>
        <div class="form-group">
            <label>Sector *</label>
            <input type="text" name="sector" class="form-control" required value="${sector}">
        </div>
        <div class="form-group">
            <label>Dirección</label>
            <input type="text" name="direccion" class="form-control" value="${direccion}">
        </div>
        <div class="form-group">
            <label>Teléfono</label>
            <input type="text" name="telefono" class="form-control" value="${telefono}">
        </div>
        <div class="form-group">
            <label>Correo</label>
            <input type="email" name="correo" class="form-control" value="${correo}">
        </div>
        
        <button type="submit" class="btn btn-gold">Registrar empresa</button>
        <a href="${pageContext.request.contextPath}/empresa/listar" class="btn" style="background:#6b7280;">Cancelar</a>
    </form>
</div>

<jsp:include page="/includes/footer.jsp" />