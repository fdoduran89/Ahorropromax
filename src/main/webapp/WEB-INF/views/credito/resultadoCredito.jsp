<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1 style="color: #065f46;">¡Crédito Aprobado Exitosamente!</h1>
    <p>El crédito ha sido evaluado, aprobado y la tabla de amortización ha sido generada.</p>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 30px; background: #f9fafb; padding: 20px; border-radius: 8px; border-left: 4px solid var(--bbc-gold);">
        <div>
            <p><strong>Empleado:</strong> ${credito.empleado.nombreCompleto}</p>
            <p><strong>Documento:</strong> ${credito.empleado.identificacion}</p>
            <p><strong>Asesor:</strong> ${credito.asesor.nombreCompleto}</p>
        </div>
        <div>
            <p><strong>Tipo de Crédito:</strong> ${credito.tipo}</p>
            <p><strong>Monto Solicitado:</strong> $<fmt:formatNumber value="${credito.montoSolicitado}" type="number" maxFractionDigits="0"/></p>
            <p><strong>Plazo:</strong> ${credito.plazoMeses} meses (${credito.plazoMeses / 12} años)</p>
            <p><strong>Cuota Mensual:</strong> <span style="font-size: 18px; font-weight: bold; color: #b45309;">$<fmt:formatNumber value="${credito.cuotaMensual}" type="number" maxFractionDigits="2"/></span></p>
        </div>
    </div>

    <h2 style="margin-top: 40px;">Tabla de Amortización</h2>
    <div style="overflow-x: auto;">
        <table class="table">
            <thead>
                <tr>
                    <th>Mes</th>
                    <th>Deuda Inicial</th>
                    <th>Cuota Fija</th>
                    <th>Interés</th>
                    <th>Abono a Capital</th>
                    <th>Saldo Pendiente</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="fila" items="${tabla}">
                    <tr>
                        <td>${fila.numeroMes}</td>
                        <td>$<fmt:formatNumber value="${fila.deudaInicial}" type="number" maxFractionDigits="2"/></td>
                        <td>$<fmt:formatNumber value="${fila.cuotaFija}" type="number" maxFractionDigits="2"/></td>
                        <td style="color: #b91c1c;">$<fmt:formatNumber value="${fila.interes}" type="number" maxFractionDigits="2"/></td>
                        <td style="color: #047857;">$<fmt:formatNumber value="${fila.abonoCapital}" type="number" maxFractionDigits="2"/></td>
                        <td>$<fmt:formatNumber value="${fila.saldoPendiente}" type="number" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div style="margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/credito/registrar" class="btn btn-gold">Radicar otro crédito</a>
        <a href="${pageContext.request.contextPath}/" class="btn" style="background: #6b7280; margin-left: 10px;">Volver al inicio</a>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />