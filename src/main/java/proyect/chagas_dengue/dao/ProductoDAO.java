package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.ProductoQuimico;
import proyect.chagas_dengue.modelos.Stock;


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
        String sql = "INSERT INTO Productos (nombre,presentacion) VALUES (?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,objeto.getNombre());
            ps.setDouble(2,objeto.getPresentacion());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    int idGen = rs.getInt(1);
                    objeto.setPCod(idGen);
                }
            }

            String sqlSt = "INSERT INTO Stocks (SPCod,total) VALUES (?,0)";
            try (PreparedStatement psSt = conexion.prepareStatement(sqlSt)){
                psSt.setInt(1,objeto.getPCod());
                psSt.executeUpdate();
            }
            /*int filas = ps.executeUpdate();
            if (filas > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    int idGen = rs.getInt(1);
                    objeto.setPCod(idGen);
                }
            }*/
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public ProductoQuimico consultar(Integer id) {
        /*String sql = "SELECT * FROM Productos WHERE PCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                ProductoQuimico producto = new ProductoQuimico(
                        rs.getInt("PCod"),
                        rs.getString("nombre"),
//                        rs.getString("marca"),
                        rs.getDouble("presentacion")
                );
                return producto;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;*/
        String sql = """
                SELECT p.PCod, p.nombre, p.presentacion,
                        COALESCE(s.total),0) AS total
                FROM Productos p
                LEFT JOIN Stocks s ON p.PCod = s.SPCod
                WHERE p.PCod = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                int PCod = rs.getInt("PCod");
                String nombre = rs.getString("nombre");
                double presentacion = rs.getDouble("presentacion");
                int total = rs.getInt("total");

                ProductoQuimico producto = new ProductoQuimico(PCod,nombre,presentacion);
                Stock stock = new Stock(PCod,producto,total);
                producto.setStock(stock);

                return producto;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(ProductoQuimico objeto) {
        String sql = "UPDATE Productos SET nombre = ?, presentacion = ? WHERE PCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,objeto.getNombre());
//            ps.setString(2,objeto.getMarca());
            ps.setDouble(2,objeto.getPresentacion());
            ps.setInt(3,objeto.getPCod());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Integer id) {
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

        String sql = """
                SELECT p.PCod, p.nombre, p.presentacion,
                        COALESCE(s.total,0) AS total
                FROM Productos p
                LEFT JOIN Stocks s ON p.PCod = s.SPCod
                ORDER BY p.PCod
                """;

        try (Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()){
                int PCod = rs.getInt("PCod");
                String nombre = rs.getString("nombre");
                double presentacion = rs.getDouble("presentacion");
                int total = rs.getInt("total");

                ProductoQuimico producto = new ProductoQuimico(PCod, nombre, presentacion);
                Stock stock = new Stock(PCod,producto,total);
                producto.setStock(stock);

                lista.add(producto);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return lista;
        /*List<ProductoQuimico> lista = new ArrayList<>();
        String sql = "SELECT * FROM Productos";

        try (Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            while (rs.next()){
                int PCod = rs.getInt("PCod");
                String nombre = rs.getString("nombre");
//                String marca = rs.getString("marca");
                Double presentacion = rs.getDouble("presentacion");
                ProductoQuimico producto = new ProductoQuimico(PCod,nombre,presentacion);
                lista.add(producto);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;*/
    }

    public List<ProductoQuimico> listarPorViaje(int vCod) {
        List<ProductoQuimico> lista = new ArrayList<>();
        String sql = """
        SELECT p.*
        FROM ProductosQuimicos p
        JOIN Viaje_ProductoQuimico vp ON p.PCod = vp.PCod
        WHERE vp.VCod = ?
    """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vCod);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductoQuimico p = new ProductoQuimico();
                p.setPCod(rs.getInt("PCod"));
                p.setNombre(rs.getString("nombre"));
                p.setPresentacion(rs.getDouble("presentacion"));

                lista.add(p);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

}
