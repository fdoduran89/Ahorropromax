package com.mycompany.ahorropromax.persistencia;

import com.mycompany.ahorropromax.negocio.ClienteCorporativo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de ClienteCorporativo (empresa).
 *
 * Métodos:
 *   - insertar(ClienteCorporativo): INSERT y asigna el id autogenerado
 *   - buscarPorId(Long): SELECT por PK
 *   - buscarPorNit(String): SELECT por NIT (único)
 *   - listar(): SELECT de todas las empresas
 *   - actualizar(ClienteCorporativo): UPDATE
 *   - eliminar(Long): DELETE
 *
 * Patrón:
 *   - try-with-resources para cerrar Connection/PreparedStatement/ResultSet
 *   - SQLException se envuelve en RuntimeException
 *   - mapeo manual ResultSet → ClienteCorporativo
 */
public class ClienteCorporativoDAO {

    public ClienteCorporativo insertar(ClienteCorporativo empresa) {
        String sql = "INSERT INTO cliente_corporativo " +
                "(nit, razon_social, sector, direccion, telefono, correo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empresa.getNit());
            ps.setString(2, empresa.getRazonSocial());
            ps.setString(3, empresa.getSector());
            ps.setString(4, empresa.getDireccion());
            ps.setString(5, empresa.getTelefono());
            ps.setString(6, empresa.getCorreo());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se pudo insertar la empresa");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    empresa.setId(rs.getLong(1));
                }
            }
            return empresa;

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando empresa: " + e.getMessage(), e);
        }
    }

    public ClienteCorporativo buscarPorId(Long id) {
        String sql = "SELECT id, nit, razon_social, sector, direccion, telefono, correo " +
                "FROM cliente_corporativo WHERE id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando empresa por id: " + e.getMessage(), e);
        }
    }

    public ClienteCorporativo buscarPorNit(String nit) {
        String sql = "SELECT id, nit, razon_social, sector, direccion, telefono, correo " +
                "FROM cliente_corporativo WHERE nit = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nit);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando empresa por NIT: " + e.getMessage(), e);
        }
    }

    public List<ClienteCorporativo> listar() {
        String sql = "SELECT id, nit, razon_social, sector, direccion, telefono, correo " +
                "FROM cliente_corporativo ORDER BY razon_social";

        List<ClienteCorporativo> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando empresas: " + e.getMessage(), e);
        }
    }

    public boolean actualizar(ClienteCorporativo empresa) {
        String sql = "UPDATE cliente_corporativo SET nit = ?, razon_social = ?, sector = ?, " +
                "direccion = ?, telefono = ?, correo = ? WHERE id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, empresa.getNit());
            ps.setString(2, empresa.getRazonSocial());
            ps.setString(3, empresa.getSector());
            ps.setString(4, empresa.getDireccion());
            ps.setString(5, empresa.getTelefono());
            ps.setString(6, empresa.getCorreo());
            ps.setLong(7, empresa.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando empresa: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(Long id) {
        String sql = "DELETE FROM cliente_corporativo WHERE id = ?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando empresa: " + e.getMessage(), e);
        }
    }

    private ClienteCorporativo mapear(ResultSet rs) throws SQLException {
        ClienteCorporativo c = new ClienteCorporativo(
                rs.getString("nit"),
                rs.getString("razon_social"),
                rs.getString("sector"));
        c.setId(rs.getLong("id"));
        c.setDireccion(rs.getString("direccion"));
        c.setTelefono(rs.getString("telefono"));
        c.setCorreo(rs.getString("correo"));
        return c;
    }
}