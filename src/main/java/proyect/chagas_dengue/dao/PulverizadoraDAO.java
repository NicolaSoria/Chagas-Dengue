package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Maquina;
import proyect.chagas_dengue.modelos.Pulverizadora;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PulverizadoraDAO implements interfaceDAO<Pulverizadora, Integer>{

    private Connection conexion;

    public PulverizadoraDAO(Connection conexion) {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(Pulverizadora objeto) {
        String sqlMaquina = "INSERT INTO Maquinas (marca, modelo, M_ISA) VALUES (?, ?, ?)";
        String sqlPulverizadora = "INSERT INTO Pulverizadoras (PMCod, cargalts) VALUES (?, ?)";

        try (
                PreparedStatement psMaquina = conexion.prepareStatement(sqlMaquina, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psPulverizadora = conexion.prepareStatement(sqlPulverizadora)
        ) {
            // Insertar en Maquinas
            psMaquina.setString(1, objeto.getMarca());
            psMaquina.setString(2, objeto.getModelo());
            psMaquina.setString(3, Maquina.TipoM.Pulverizadora.name());

            int filas = psMaquina.executeUpdate();
            if (filas > 0) {
                ResultSet rs = psMaquina.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    objeto.setMCod(idGenerado);

                    // Insertar en Pulverizadoras
                    psPulverizadora.setInt(1, idGenerado);
                    psPulverizadora.setString(2, objeto.getCargalts().name().substring(1)); // "_13" → "13"
                    psPulverizadora.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pulverizadora consultar(Integer id) {
        String sql = """
            SELECT m.MCod, m.marca, m.modelo, p.cargalts
            FROM Maquinas m
            INNER JOIN Pulverizadoras p ON m.MCod = p.PMCod
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

                Pulverizadora.cargaltsP cargalts = Pulverizadora.cargaltsP.valueOf("_" + cargaltsStr); // "13" → "_13"

                return new Pulverizadora(MCod, marca, modelo, cargalts);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void actualizar(Pulverizadora objeto) {
        String sqlMaquina = "UPDATE Maquinas SET marca = ?, modelo = ? WHERE MCod = ?";
        String sqlPulverizadora = "UPDATE Pulverizadoras SET cargalts = ? WHERE PMCod = ?";

        try (
                PreparedStatement psMaquina = conexion.prepareStatement(sqlMaquina);
                PreparedStatement psPulverizadora = conexion.prepareStatement(sqlPulverizadora)
        ) {
            // Maquina
            psMaquina.setString(1, objeto.getMarca());
            psMaquina.setString(2, objeto.getModelo());
            psMaquina.setInt(3, objeto.getMCod());
            psMaquina.executeUpdate();

            // Pulverizadora
            psPulverizadora.setString(1, objeto.getCargalts().name().substring(1)); // "_13" → "13"
            psPulverizadora.setInt(2, objeto.getMCod());
            psPulverizadora.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Maquinas WHERE MCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            // No es necesario eliminar de Pulverizadoras: ON DELETE CASCADE lo hace
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Pulverizadora> listarTodos() {
        List<Pulverizadora> lista = new ArrayList<>();

        String sql = """
            SELECT m.MCod, m.marca, m.modelo, p.cargalts
            FROM Maquinas m
            INNER JOIN Pulverizadoras p ON m.MCod = p.PMCod
        """;

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int MCod = rs.getInt("MCod");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String cargaltsStr = rs.getString("cargalts");

                Pulverizadora.cargaltsP cargalts = Pulverizadora.cargaltsP.valueOf("_" + cargaltsStr);

                Pulverizadora p = new Pulverizadora(MCod, marca, modelo, cargalts);
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}

