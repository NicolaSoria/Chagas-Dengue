package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.ElementoDeSeguridad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ElementoSeguridadDAO {

    private Connection conn;

    public ElementoSeguridadDAO() {
        this.conn = ConexionBD.getInstancia().getConexion();
    }


    public ElementoSeguridadDAO(Connection conn) {
        this.conn = conn;
    }

    // Devuelve los elementos según tipo de viaje
    public List<ElementoDeSeguridad> listarPorTipoViaje(String tipoViaje) throws SQLException {
        List<ElementoDeSeguridad> lista = new ArrayList<>();

        // Definir los TESCod según tipo de viaje
        int[] tipos;
        switch (tipoViaje) {
            case "Barrido":
                tipos = new int[]{1, 2, 3, 4}; // Casco, Chaleco, Guantes, Botas
                break;
            case "Diario":
                tipos = new int[]{1, 2, 3}; // Casco, Chaleco, Guantes
                break;
            default:
                tipos = new int[]{}; // Ninguno
        }

        if (tipos.length == 0) return lista;

        // Construir consulta dinámica
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < tipos.length; i++) {
            inClause.append("?");
            if (i < tipos.length - 1) inClause.append(",");
        }

        String sql = "SELECT e.ESCod, e.TESCod, e.descripcion " +
                "FROM Elementos e " +
                "WHERE e.TESCod IN (" + inClause + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < tipos.length; i++) {
                ps.setInt(i + 1, tipos[i]);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new ElementoDeSeguridad(
                        rs.getInt("ESCod"),
                        rs.getInt("TESCod"),
                        rs.getString("descripcion")
//                        rs.getString("estado")
                ));
            }
        }

        return lista;
    }

    public List<ElementoDeSeguridad> listarPorViaje(int vCod) {
        List<ElementoDeSeguridad> lista = new ArrayList<>();
        String sql = """
        SELECT e.*
        FROM Elementos e
        JOIN Viaje_ElementoSeguridad ve ON e.ESCod = ve.ESCod
        WHERE ve.VCod = ?
    """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vCod);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ElementoDeSeguridad e = new ElementoDeSeguridad();
                e.setESCod(rs.getInt("ESCod"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

}



//package proyect.chagas_dengue.dao;
//
//import java.sql.Connection;
//import java.util.List;
//
//public class ElementosDAO implements interfaceDAO{
//
//    private Connection conexion;
//
//
//
//    @Override
//    public void insertar(Object objeto) {
//
//    }
//
//    @Override
//    public Object consultar(Object o) {
//        return null;
//    }
//
//    @Override
//    public void actualizar(Object objeto) {
//
//    }
//
//    @Override
//    public void eliminar(int id) {
//
//    }
//
//    @Override
//    public List listarTodos() {
//        return List.of();
//    }
//}
