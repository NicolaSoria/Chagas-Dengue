package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Manual;
import proyect.chagas_dengue.modelos.Maquina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManualDAO implements interfaceDAO<Manual, Integer>{

    private Connection conexion;

    public ManualDAO(Connection conexion) {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(Manual objeto) {
        String sqlMaquina = "INSERT INTO Maquinas (marca, modelo, M_ISA) VALUES (?, ?, ?)";
        String sqlManual = "INSERT INTO Manuales (MMCod, cargalts) VALUES (?, ?)";

        try (
                PreparedStatement psMaquina = conexion.prepareStatement(sqlMaquina, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psManual = conexion.prepareStatement(sqlManual)
        ) {
            psMaquina.setString(1, objeto.getMarca());
            psMaquina.setString(2, objeto.getModelo());
            psMaquina.setString(3, Maquina.TipoM.Manual.name());

            int filas = psMaquina.executeUpdate();
            if (filas > 0) {
                ResultSet rs = psMaquina.getGeneratedKeys();
                if (rs.next()) {
                    int idGen = rs.getInt(1);
                    objeto.setMCod(idGen);

                    psManual.setInt(1, idGen);
                    psManual.setString(2, objeto.getCargalts().name().substring(1)); // "_8" -> "8"
                    psManual.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Manual consultar(Integer id) {
        String sql = """
            SELECT m.MCod, m.marca, m.modelo, man.cargalts
            FROM Maquinas m
            INNER JOIN Manuales man ON m.MCod = man.MMCod
            WHERE m.MCod = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int MCod = rs.getInt("MCod");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String cargaltsStr = rs.getString("cargalts");

                Manual.cargaltsM cargalts = Manual.cargaltsM.valueOf("_" + cargaltsStr);

                return new Manual(MCod, marca, modelo, cargalts);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void actualizar(Manual objeto) {
        String sqlMaquina = "UPDATE Maquinas SET marca = ?, modelo = ? WHERE MCod = ?";
        String sqlManual = "UPDATE Manuales SET cargalts = ? WHERE MMCod = ?";

        try (
                PreparedStatement psMaquina = conexion.prepareStatement(sqlMaquina);
                PreparedStatement psManual = conexion.prepareStatement(sqlManual)
        ) {
            psMaquina.setString(1, objeto.getMarca());
            psMaquina.setString(2, objeto.getModelo());
            psMaquina.setInt(3, objeto.getMCod());
            psMaquina.executeUpdate();

            psManual.setString(1, objeto.getCargalts().name().substring(1));
            psManual.setInt(2, objeto.getMCod());
            psManual.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Integer id) {
        String sql = "DELETE FROM Maquinas WHERE MCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate(); // ON DELETE CASCADE eliminará en Manuales
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Manual> listarTodos() {
        List<Manual> lista = new ArrayList<>();

        String sql = """
            SELECT m.MCod, m.marca, m.modelo, man.cargalts
            FROM Maquinas m
            INNER JOIN Manuales man ON m.MCod = man.MMCod
        """;

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int MCod = rs.getInt("MCod");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String cargaltsStr = rs.getString("cargalts");

                Manual.cargaltsM cargalts = Manual.cargaltsM.valueOf("_" + cargaltsStr);

                Manual m = new Manual(MCod, marca, modelo, cargalts);
                lista.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
