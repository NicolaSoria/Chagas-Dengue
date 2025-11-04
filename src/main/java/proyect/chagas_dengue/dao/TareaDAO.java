package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Tarea;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO implements interfaceDAO<Tarea,String>{

    private Connection conexion;

    public TareaDAO(){ this.conexion = ConexionBD.getInstancia().getConexion(); }


    @Override
    public void insertar(Tarea objeto) {

    }

    @Override
    public Tarea consultar(String id) {
        String sql = "SELECT * FROM Tareas WHERE tipo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                Tarea tarea = new Tarea(
                        rs.getString("tipo"),
                        rs.getString("descripcion")
                );
                tarea.setTCod(rs.getInt("TCod"));
                return tarea;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(Tarea objeto) {

    }

    @Override
    public void eliminar(String s) {

    }

    @Override
    public List<Tarea> listarTodos() {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tareas";

        try (Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql)){

            while (rs.next()){
                int TCod = rs.getInt("TCod");
                String tipo = rs.getString("tipo");
                String descripcion = rs.getString("descripcion");

                Tarea tarea = new Tarea(TCod,tipo,descripcion);
                lista.add(tarea);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }
}
