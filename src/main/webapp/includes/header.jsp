<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Ahorro Promax - Banco BBC</title>
    <!-- Importación directa de Tailwind -->
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        :root {
            --bbc-navy: #0B1E36;
            --bbc-gold: #D4AF37;
        }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f3f4f6; margin: 0; padding: 0; }
        .bbc-header { background-color: var(--bbc-navy); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
        .bbc-header .logo { font-size: 24px; font-weight: 800; color: var(--bbc-gold); }
        .bbc-header a { color: white; text-decoration: none; margin-left: 20px; font-weight: 500; transition: color 0.2s; }
        .bbc-header a:hover { color: var(--bbc-gold); }
        .container { max-width: 1000px; margin: 40px auto; background: white; padding: 30px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        .table { width: 100%; border-collapse: collapse; }
        .table th { background-color: var(--bbc-navy); color: white; padding: 12px; text-align: left; }
        .table td { padding: 12px; border-bottom: 1px solid #e5e7eb; }
        .table tr:hover { background-color: #f9fafb; }
        .btn-gold { background-color: var(--bbc-gold); color: var(--bbc-navy); padding: 10px 20px; border-radius: 6px; text-decoration: none; font-weight: bold; display: inline-block; }
        .btn-gold:hover { background-color: #B8972E; }
        .alert-success { background-color: #d1fae5; color: #065f46; padding: 15px; border-radius: 6px; margin-bottom: 20px; }
        .alert-error { background-color: #fee2e2; color: #991b1b; padding: 15px; border-radius: 6px; margin-bottom: 20px; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: bold; color: var(--bbc-navy); }
        .form-control { width: 100%; padding: 10px; border: 1px solid #d1d5db; border-radius: 6px; box-sizing: border-box; }
    </style>
</head>
<body>
    <header class="bbc-header">
        <div class="logo">BANCO BBC</div>
        <nav>
            <a href="${pageContext.request.contextPath}/">Inicio</a>
            <a href="${pageContext.request.contextPath}/empresa/listar">Empresas</a>
            <a href="${pageContext.request.contextPath}/empleado/listar">Empleados</a>
            <a href="${pageContext.request.contextPath}/credito/registrar">Créditos</a>
            <a href="${pageContext.request.contextPath}/empleado/carga-masiva">Carga Masiva</a>
        </nav>
    </header>