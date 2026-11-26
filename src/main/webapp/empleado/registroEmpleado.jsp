<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1 style="color: var(--bbc-navy); margin-top: 0;">Registrar Empleado</h1>
    
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/empleado/registrar" method="post">
        
        <div class="form-group">
            <label>Empresa a la que pertenece *</label>
            <select name="idEmpresa" class="form-control" required>
                <option value="">Seleccione una empresa...</option>
                <c:forEach var="emp" items="${empresas}">
                    <option value="${emp.id}">${emp.razonSocial} (NIT: ${emp.nit})</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Identificación (Cédula) *</label>
            <input type="text" name="identificacion" class="form-control" required>
        </div>
        
        <div class="form-group">
            <label>Nombres *</label>
            <input type="text" name="nombres" class="form-control" required>
        </div>
        
        <div class="form-group">
            <label>Apellidos *</label>
            <input type="text" name="apellidos" class="form-control" required>
        </div>
        
        <div class="form-group">
            <label>Cargo *</label>
            <input type="text" name="cargo" class="form-control" required>
        </div>

        <div style="display: flex; gap: 15px;">
            <div class="form-group" style="flex: 1;">
                <label>Salario Básico *</label>
                <input type="number" name="salarioBasico" class="form-control" required min="0">
            </div>
            <div class="form-group" style="flex: 1;">
                <label>Ingresos Mensuales *</label>
                <input type="number" name="ingresosMensuales" class="form-control" required min="0">
            </div>
            <div class="form-group" style="flex: 1;">
                <label>Gastos Mensuales *</label>
                <input type="number" name="gastosMensuales" class="form-control" required min="0">
            </div>
        </div>

        <div style="margin-top: 20px;">
            <button type="submit" class="btn-gold">Registrar empleado</button>
            <a href="${pageContext.request.contextPath}/empleado/listar" style="margin-left: 10px; color: #6b7280; text-decoration: none;">Cancelar</a>
        </div>
    </form>
</div>

<jsp:include page="/includes/footer.jsp" />