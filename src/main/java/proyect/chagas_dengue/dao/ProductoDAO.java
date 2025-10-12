package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.ProductoQuimico;
import proyect.chagas_dengue.modelos.Viaje;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements interfaceDAO<ProductoQuimico,Integer> {

    private Connection conexion;

    public ProductoDAO(){
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(ProductoQuimico objeto) {
        String sql = "INSERT INTO Productos (nombre,marca,presentacion) VALUES (?,?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,objeto.getNombre());
            ps.setString(2,objeto.getMarca());
            ps.setDouble(3,objeto.getPresentacion());

            int filas = ps.executeUpdate();
            if (filas > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    int idGen = rs.getInt(1);
                    objeto.setPCod(idGen);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public ProductoQuimico consultar(Integer id) {
        String sql = "SELECT * FROM Productos WHERE PCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                ProductoQuimico producto = new ProductoQuimico(
                        rs.getInt("PCod"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getDouble("presentacion")
                );
                return producto;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(ProductoQuimico objeto) {
        String sql = "UPDATE Productos SET nombre = ?, marca = ?, presentacion = ? WHERE PCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,objeto.getNombre());
            ps.setString(2,objeto.getMarca());
            ps.setDouble(3,objeto.getPresentacion());
            ps.setInt(4,objeto.getPCod());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Productos WHERE PCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<ProductoQuimico> listarTodos() {
        List<ProductoQuimico> lista = new ArrayList<>();
        String sql = "SELECT * FROM Productos";

        try (Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            while (rs.next()){
                int PCod = rs.getInt("PCod");
                String nombre = rs.getString("nombre");
                String marca = rs.getString("marca");
                Double presentacion = rs.getDouble("presentacion");
                ProductoQuimico producto = new ProductoQuimico(PCod,nombre,marca,presentacion);
                lista.add(producto);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }
}
