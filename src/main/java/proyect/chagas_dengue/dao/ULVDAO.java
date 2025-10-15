package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Manual;
import proyect.chagas_dengue.modelos.Maquina;
import proyect.chagas_dengue.modelos.ULV;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ULVDAO implements interfaceDAO<ULV,Integer>{

    private Connection conexion;

    public ULVDAO(Connection conexion) {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(ULV objeto) {
        String sqlMaquina = "INSERT INTO Maquinas (marca, modelo, M_ISA) VALUES (?, ?, ?)";
        String sqlULV = "INSERT INTO ULVs (UMCod) VALUES (?)";

        try (
                PreparedStatement psMaquina = conexion.prepareStatement(sqlMaquina, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psULV = conexion.prepareStatement(sqlULV)
        ) {
            psMaquina.setString(1, objeto.getMarca());
            psMaquina.setString(2, objeto.getModelo());
            psMaquina.setString(3, Maquina.TipoM.ULV.name());

            int filas = psMaquina.executeUpdate();
            if (filas > 0) {
                ResultSet rs = psMaquina.getGeneratedKeys();
                if (rs.next()) {
                    int idGen = rs.getInt(1);
                    objeto.setMCod(idGen);

                    psULV.setInt(1, idGen);
                    psULV.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ULV consultar(Integer id) {
        String sql = """
            SELECT m.MCod, m.marca, m.modelo
            FROM Maquinas m
            INNER JOIN ULVs ulv ON m.MCod = ulv.MMCod
            WHERE m.MCod = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int MCod = rs.getInt("MCod");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");

                return new ULV(MCod, marca, modelo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void actualizar(ULV objeto) {
        String sqlMaquina = "UPDATE Maquinas SET marca = ?, modelo = ? WHERE MCod = ?";

        try (PreparedStatement psMaquina = conexion.prepareStatement(sqlMaquina)) {
            psMaquina.setString(1, objeto.getMarca());
            psMaquina.setString(2, objeto.getModelo());
            psMaquina.setInt(3, objeto.getMCod());
            psMaquina.executeUpdate();;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Integer id) {
        String sql = "DELETE FROM Maquinas WHERE MCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate(); // ON DELETE CASCADE elimina ULVs
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ULV> listarTodos() {
        List<ULV> lista = new ArrayList<>();

        String sql = """
            SELECT m.MCod, m.marca, m.modelo
            FROM Maquinas m
            INNER JOIN ULVs u ON m.MCod = u.UMCod
        """;

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int MCod = rs.getInt("MCod");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");

                ULV ulv = new ULV(MCod, marca, modelo);
                lista.add(ulv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

}
