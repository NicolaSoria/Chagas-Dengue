package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    private Connection conn = ConexionBD.getInstancia().getConexion();

    public Usuario Autentucacion(String Usuario) {
        Connection conexion = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conexion = ConexionBD.getInstancia().getConexion();

            String sql = "SELECT username, password_hash FROM usuarios WHERE username = ?";
            stmt = conexion.prepareStatement(sql);
            stmt.setString(1, Usuario);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario(rs.getString("username"), rs.getString("password_hash"));
                return usuario;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, password_hash) VALUES (?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getPassword());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
}
