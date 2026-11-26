<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1>Carga Masiva de Empleados (Excel)</h1>
    
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <p>Sube un archivo <strong>.xlsx</strong> con las columnas: Identificación, Nombres, Apellidos, Cargo, Salario, Ingresos, Gastos.</p>

    <form action="${pageContext.request.contextPath}/empleado/carga-masiva" method="post" enctype="multipart/form-data">
        
        <div class="form-group">
            <label>Seleccione la Empresa a la que pertenecen los empleados *</label>
            <select name="idEmpresa" class="form-control" required>
                <option value="">Seleccione una empresa...</option>
                <c:forEach var="emp" items="${empresas}">
                    <option value="${emp.id}">${emp.razonSocial}</option>
                </c:forEach>
            </select>
            <small style="color: red;">*(Si no ves empresas, primero debes registrar una en el módulo de Empresas)*</small>
        </div>

        <div class="form-group">
            <label>Archivo Excel (.xlsx) *</label>
            <input type="file" name="archivoExcel" class="form-control" accept=".xlsx" required>
        </div>

        <button type="submit" class="btn btn-gold">Procesar Archivo</button>
        <a href="${pageContext.request.contextPath}/" class="btn" style="background: #6b7280; margin-left: 10px;">Volver</a>
    </form>
</div>

<jsp:include page="/includes/footer.jsp" />