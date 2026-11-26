<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/includes/header.jsp" />

<div class="container">
    <h1>Detalle de la Empresa</h1>
    
    <div style="background: #f9fafb; padding: 20px; border-left: 4px solid var(--bbc-gold); border-radius: 6px;">
        <p><strong>ID:</strong> ${empresa.id}</p>
        <p><strong>NIT:</strong> ${empresa.nit}</p>
        <p><strong>Razón social:</strong> ${empresa.razonSocial}</p>
        <p><strong>Sector:</strong> ${empresa.sector}</p>
        <p><strong>Dirección:</strong> ${empresa.direccion}</p>
        <p><strong>Teléfono:</strong> ${empresa.telefono}</p>
        <p><strong>Correo:</strong> ${empresa.correo}</p>
    </div>
    
    <br>
    <a href="${pageContext.request.contextPath}/empresa/listar" class="btn">&larr; Volver a la lista</a>
</div>

<jsp:include page="/includes/footer.jsp" />