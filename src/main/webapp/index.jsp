<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- 1. HEADER -->
<jsp:include page="/includes/header.jsp" />

<!-- 2. CONTENIDO -->
<div class="container">
    <h1 style="color: var(--bbc-navy); margin-top: 0;">Bienvenido a Ahorro Promax</h1>
    <p>Sistema de gestión de créditos de libranza corporativa para el Banco BBC.</p>
    
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-top: 30px;">
        
        <a href="${pageContext.request.contextPath}/empresa/listar" style="text-decoration: none;">
            <div style="padding: 20px; background: var(--bbc-navy); color: white; border-radius: 8px; text-align: center; border-bottom: 4px solid var(--bbc-gold); transition: transform 0.2s;" onmouseover="this.style.transform='translateY(-5px)'" onmouseout="this.style.transform='translateY(0)'">
                <h3 style="color: var(--bbc-gold); margin: 0 0 5px 0;">Empresas</h3>
                <p style="margin:0; font-size: 13px; color: #d1d5db;">Clientes corporativos</p>
            </div>
        </a>
        
        <a href="${pageContext.request.contextPath}/prueba-conexion" style="text-decoration: none;">
            <div style="padding: 20px; background: var(--bbc-navy); color: white; border-radius: 8px; text-align: center; border-bottom: 4px solid var(--bbc-gold); transition: transform 0.2s;" onmouseover="this.style.transform='translateY(-5px)'" onmouseout="this.style.transform='translateY(0)'">
                <h3 style="color: var(--bbc-gold); margin: 0 0 5px 0;">Prueba BD</h3>
                <p style="margin:0; font-size: 13px; color: #d1d5db;">Verificar MySQL</p>
            </div>
        </a>

    </div>
</div>

<!-- 3. FOOTER -->
<jsp:include page="/includes/footer.jsp" />