package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Usan;
import proyect.chagas_dengue.modelos.UsanID;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsanDAO implements interfaceDAO<Usan, UsanID> {

    private Connection conexion;

    public UsanDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void actualizar(Usan usan) {
        // No hay campos que actualizar en esta tabla intermedia
        // ya que solo contiene las claves (VCod, CCod)
    }


    @Override
    public void insertar(Usan usan) {
        String sql = "INSERT INTO Usan (VCod, CCod) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usan.getVCod());
            ps.setInt(2, usan.getCCod());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Usan consultar(UsanID id) {
        String sql = "SELECT * FROM Usan WHERE VCod = ? AND CCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id.getVCod());
            ps.setInt(2, id.getCCod());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usan(rs.getInt("VCod"), rs.getInt("CCod"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void eliminar(UsanID id) {
        String sql = "DELETE FROM Usan WHERE VCod = ? AND CCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id.getVCod());
            ps.setInt(2, id.getCCod());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Usan> listarTodos() {
        List<Usan> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usan";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int VCod = rs.getInt("VCod");
                int CCod = rs.getInt("CCod");
                lista.add(new Usan(VCod, CCod));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Usan> listarPorVCod(int VCod) {
        List<Usan> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usan WHERE VCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, VCod);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int CCod = rs.getInt("CCod");
                lista.add(new Usan(VCod, CCod));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}



//package proyect.chagas_dengue.dao;
//
//import proyect.chagas_dengue.conexion.ConexionBD;
//import proyect.chagas_dengue.modelos.Usan;
//import proyect.chagas_dengue.modelos.UsanID;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class UsanDAO implements interfaceDAO<Usan,UsanID>{
//
//    private Connection conexion;
//
//    public UsanDAO() {
//        this.conexion = ConexionBD.getInstancia().getConexion();
//    }
//
//    public void insertar(Usan usan) {
//        String sql = "INSERT INTO Usan (VCod, CCod) VALUES (?, ?)";
//        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
//            ps.setInt(1, usan.getVCod());
//            ps.setInt(2, usan.getCCod());
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Usan consultar(UsanID id) {
//        String sql = "SELECT * FROM Usan WHERE VCod = ? AND CCod = ?";
//        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
//            ps.setInt(1, id.getVCod());
//            ps.setInt(2, id.getCCod());
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                return new Usan(rs.getInt("VCod"), rs.getInt("CCod"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public void eliminar(UsanID id) {
//        String sql = "DELETE FROM Usan WHERE VCod = ? AND CCod = ?";
//        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
//            ps.setInt(1, id.getVCod());
//            ps.setInt(2, id.getCCod());
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public List<Usan> listarTodos() {
//        List<Usan> lista = new ArrayList<>();
//        String sql = "SELECT * FROM Usan";
//        try (Statement st = conexion.createStatement();
//             ResultSet rs = st.executeQuery(sql)) {
//
//            while (rs.next()) {
//                int VCod = rs.getInt("VCod");
//                int CCod = rs.getInt("CCod");
//                lista.add(new Usan(VCod, CCod));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return lista;
//    }
//
//    public List<Usan> listarPorVCod(int VCod) {
//        List<Usan> lista = new ArrayList<>();
//        String sql = "SELECT * FROM Usan WHERE VCod = ?";
//        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
//            ps.setInt(1, VCod);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                int CCod = rs.getInt("CCod");
//                lista.add(new Usan(VCod, CCod));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return lista;
//    }
//}
