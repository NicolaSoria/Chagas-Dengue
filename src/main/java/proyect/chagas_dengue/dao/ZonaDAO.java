package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Zona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZonaDAO implements interfaceDAO<Zona,String>{

    private Connection conexion;

    public ZonaDAO(){
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(Zona objeto) {
        String sql = "INSERT INTO Zonas (nombre,direccion,localidad,departamento) VALUES (?,?,?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,objeto.getNombre());
            ps.setString(2,objeto.getDireccion());
            ps.setString(3,objeto.getLocalidad());
            ps.setString(4,objeto.getDepartamento());

            int filas = ps.executeUpdate();
            if (filas > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    int idGen = rs.getInt(1);
                    objeto.setZCod(idGen);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Zona consultar(String id) {
        String sql = "SELECT * FROM Zonas WHERE nombre = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                Zona zona = new Zona(
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("localidad"),
                        rs.getString("departamento")
                );
                zona.setZCod(rs.getInt("ZCod"));
                return zona;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(Zona objeto) {
        String sql = "UPDATE Zonas SET nombre = ?,direccion = ?, localidad = ?, departamento = ? WHERE ZCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,objeto.getNombre());
            ps.setString(2,objeto.getDireccion());
            ps.setString(3,objeto.getLocalidad());
            ps.setString(4, objeto.getDepartamento());
            ps.setInt(5,objeto.getZCod());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(String id) {
        String sql = "DELETE FROM Zonas WHERE nombre = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Zona> listarTodos(){
        List<Zona> lista = new ArrayList<>();
        String sql = "SELECT * FROM Zonas";

        try (Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql)){

            while (rs.next()){
                int ZCod = rs.getInt("ZCod");
                String nombre = rs.getString("nombre");
                String direccion = rs.getString("direccion");
                String localidad = rs.getString("localidad");
                String departamento = rs.getString("departamento");

                Zona zona = new Zona(ZCod,nombre,direccion,localidad,departamento);
                lista.add(zona);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }
}
