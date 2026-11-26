<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1>Radicar Nuevo Crédito</h1>
    
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/credito/registrar" method="post">
        
        <div class="form-group">
            <label>Empleado Solicitante *</label>
            <select name="identificacionEmpleado" class="form-control" required>
                <option value="">Seleccione un empleado...</option>
                <c:forEach var="emp" items="${empleados}">
                    <option value="${emp.identificacion}">
                        ${emp.nombreCompleto} (C.C. ${emp.identificacion}) - ${emp.clienteCorporativo.razonSocial}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Asesor Asignado *</label>
            <select name="identificacionAsesor" class="form-control" required>
                <option value="">Seleccione un asesor...</option>
                <c:forEach var="ase" items="${asesores}">
                    <option value="${ase.identificacion}">
                        ${ase.nombreCompleto} (C.C. ${ase.identificacion})
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Tipo de Crédito *</label>
            <select name="tipoCredito" class="form-control" required>
                <option value="">Seleccione el tipo...</option>
                <c:forEach var="tipo" items="${tiposCredito}">
                    <option value="${tipo}">${tipo} (Tasa: ${tipo.tasaMensual * 100}% mensual)</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Monto Solicitado (COP) *</label>
            <input type="number" name="monto" class="form-control" required min="1000000" step="100000" placeholder="Ej: 10000000">
        </div>

        <div class="form-group">
            <label>Plazo en Años *</label>
            <input type="number" name="plazoAnios" class="form-control" required min="1" max="30" placeholder="Ej: 3">
            <small style="color: #6b7280; font-size: 12px;">El sistema validará si el plazo cumple con las reglas del tipo de crédito.</small>
        </div>

        <div style="margin-top: 30px;">
            <button type="submit" class="btn btn-gold" style="font-size: 16px; padding: 12px 24px;">
                Calcular y Aprobar Crédito
            </button>
            <a href="${pageContext.request.contextPath}/" style="margin-left: 15px; color: #6b7280; text-decoration: none;">Cancelar</a>
        </div>
    </form>
</div>

<jsp:include page="/includes/footer.jsp" />