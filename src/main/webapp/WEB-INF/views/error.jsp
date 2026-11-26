<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/includes/header.jsp" />

<div class="container" style="text-align: center;">
    <h1 style="color: #991b1b;">Ocurrió un error</h1>
    <div class="alert alert-error" style="display:inline-block; margin-top: 20px;">
        ${not empty error ? error : 'Error desconocido'}
    </div>
    <br><br>
    <a href="${pageContext.request.contextPath}/" class="btn">Volver al inicio</a>
</div>

<jsp:include page="/includes/footer.jsp" />