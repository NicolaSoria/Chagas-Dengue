package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Manual;
import proyect.chagas_dengue.modelos.Maquina;
import proyect.chagas_dengue.modelos.Pulverizadora;
import proyect.chagas_dengue.modelos.ULV;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaquinaDAO implements interfaceDAO<Maquina,Integer>{

    private Connection conexion;

    public MaquinaDAO(Connection conexion){this.conexion= ConexionBD.getInstancia().getConexion();}

    public MaquinaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(Maquina objeto) {
        String sql = "INSERT INTO Maquinas (marca, modelo, M_ISA) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, objeto.getMarca());
            ps.setString(2, objeto.getModelo());
            ps.setString(3, objeto.getTipo().name()); // el enum se guarda como string

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGen = rs.getInt(1);
                    objeto.setMCod(idGen);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Maquina consultar(Integer id) {
        String sql = "SELECT * FROM Maquinas WHERE MCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int MCod = rs.getInt("MCod");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String tipoStr = rs.getString("M_ISA");
                Maquina.TipoM tipo = Maquina.TipoM.valueOf(tipoStr);

                return new Maquina(MCod, marca, modelo, tipo) {};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(Maquina objeto) {
        String sql = "UPDATE Maquinas SET marca = ?, modelo = ?, M_ISA = ? WHERE MCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, objeto.getMarca());
            ps.setString(2, objeto.getModelo());
            ps.setString(3, objeto.getTipo().name());
            ps.setInt(4, objeto.getMCod());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Integer id) {
        String sql = "DELETE FROM Maquinas WHERE MCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Maquina> listarTodos() {
        List<Maquina> lista = new ArrayList<>();
        String sql = "SELECT * FROM Maquinas";

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int MCod = rs.getInt("MCod");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String tipoStr = rs.getString("M_ISA");
                Maquina.TipoM tipo = Maquina.TipoM.valueOf(tipoStr);

                Maquina maquina = new Maquina(MCod, marca, modelo, tipo) {};
                lista.add(maquina);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

//    public List<Maquina> listarPorViaje(int vCod) {
//        List<Maquina> lista = new ArrayList<>();
//        String sql = """
//        SELECT m.*
//        FROM Maquinas m
//        JOIN Viaje_Maquina vm ON m.MCod = vm.MCod
//        WHERE vm.VCod = ?
//    """;
//
//        try (Connection conn = ConexionBD.getInstancia().getConexion();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, vCod);
//            ResultSet rs = ps.executeQuery();
//
//
//
//
//            while (rs.next()) {
//                Maquina m = new Maquina();
//                m.setMCod(rs.getInt("MCod"));
//                m.setMarca(rs.getString("marca"));
//                m.setModelo(rs.getString("modelo"));
//                m.setTipo(Maquina.TipoM.valueOf(rs.getString("tipo")));
//
//
//                lista.add(m);
//            }
//
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        return lista;
//    }

    public List<Maquina> listarPorViaje(int vCod) {
        List<Maquina> lista = new ArrayList<>();
        String sql = """
        SELECT m.*
        FROM Maquinas m
        JOIN Viaje_Maquina vm ON m.MCod = vm.MCod
        WHERE vm.VCod = ?
    """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vCod);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("M_ISA"); // según tu esquema, esta columna indica el tipo
                Maquina m;

                if (tipo.equalsIgnoreCase("Manual")) {
                    m = new Manual();
                } else if (tipo.equalsIgnoreCase("ULV")) {
                    m = new ULV();
                } else if (tipo.equalsIgnoreCase("Pulverizadora")) {
                    m = new Pulverizadora();
                } else {
                    System.out.println("Tipo de máquina desconocido: " + tipo);
                    continue; // o lanzás una excepción si querés ser estricto
                }

                m.setMCod(rs.getInt("MCod"));
                m.setMarca(rs.getString("marca"));
                m.setModelo(rs.getString("modelo"));
                m.setTipo(Maquina.TipoM.valueOf(tipo.toUpperCase()));

                lista.add(m);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

}
