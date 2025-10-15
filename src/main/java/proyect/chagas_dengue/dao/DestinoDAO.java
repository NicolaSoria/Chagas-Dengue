package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Destino;
import proyect.chagas_dengue.modelos.DestinoID;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinoDAO implements interfaceDAO<Destino,DestinoID>{

    private Connection conexion;

    public DestinoDAO(){this.conexion = ConexionBD.getInstancia().getConexion();}

    @Override
    public void insertar(Destino obj) {
        String sql = "INSERT INTO Destinos (VCod, ZCod) VALUES (?,?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,obj.getDVCod());
            ps.setInt(2,obj.getDZCod());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Destino consultar(DestinoID id) {
        String sql = "SELECT * FROM Destinos WHERE VCod = ? AND ZCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id.getDVCod());
            ps.setInt(2,id.getDZCod());
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new Destino(rs.getInt("VCod"),rs.getInt("ZCod"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(Destino objeto) {

    }

    @Override
    public void eliminar(DestinoID id) {
        String sql = "DELETE FROM Destinos WHERE VCod = ? AND ZCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id.getDVCod());
            ps.setInt(2,id.getDZCod());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Destino> listarTodos() {
        List<Destino> lista = new ArrayList<>();
        String sql = "SELECT * FROM Destinos";

        try (Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            while (rs.next()){
                int DVCod = rs.getInt("VCod");
                int DZCod = rs.getInt("ZCod");

                Destino destino = new Destino(DVCod,DZCod);
                lista.add(destino);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

}
