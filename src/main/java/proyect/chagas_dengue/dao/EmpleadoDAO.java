package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {
    private Connection conn = ConexionBD.getInstancia().getConexion();

    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM Empleados";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Empleado(
                        rs.getInt("ECod"),
                        rs.getString("nombre"),
                        rs.getInt("dni"),
                        rs.getString("direccion"),
                        rs.getString("telefono")
                ));
            }
        }
        return lista;
    }

    public List<Empleado> listarPorViaje(int vCod) {
        List<Empleado> lista = new ArrayList<>();
        String sql = """
        SELECT e.*
        FROM Empleados e
        JOIN Viaje_Empleado ve ON e.ECod = ve.ECod
        WHERE ve.VCod = ?
    """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vCod);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setECod(rs.getInt("ECod"));
                e.setNombre(rs.getString("nombre"));
                e.setDni(rs.getInt("dni"));
                e.setDireccion(rs.getString("direccion"));
                e.setTelefono(rs.getString("telefono"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

}


