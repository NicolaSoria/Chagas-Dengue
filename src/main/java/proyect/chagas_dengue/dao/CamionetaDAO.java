package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Camioneta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CamionetaDAO {

    public void insertar(Camioneta camioneta) throws SQLException {
        String sql = "INSERT INTO Camionetas (patente, estado) VALUES (?, ?)";

        Connection conn = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, camioneta.getPatente());
            ps.setString(2, camioneta.getEstado());
            ps.executeUpdate();
        }
    }

    public List<Camioneta> listarDisponibles() throws SQLException {
        String sql = "SELECT patente, estado FROM Camionetas WHERE estado = 'Operativa'";
        Connection conn = ConexionBD.getInstancia().getConexion();
        List<Camioneta> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Camioneta(rs.getString("patente"), rs.getString("estado")));
            }
        }
        return lista;
    }
}
