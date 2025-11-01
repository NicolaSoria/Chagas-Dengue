package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Viaje;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViajeDAO implements interfaceDAO<Viaje,Integer>{

    private Connection conexion;

    public ViajeDAO(){this.conexion = ConexionBD.getInstancia().getConexion();}

    @Override
    public void insertar(Viaje objeto) {
        String sql = "INSERT INTO Viajes (fecha,tipo) VALUES (?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setDate(1,java.sql.Date.valueOf(objeto.getFechaInicio()));
            ps.setString(2,objeto.getTipo().name());

            int filas = ps.executeUpdate();
            if (filas > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    int idGen = rs.getInt(1);
                    objeto.setVCod(idGen);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Viaje consultar(Integer id) {
        String sql = "SELECT * FROM Viajes WHERE VCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                Viaje.Tipo tipo = Viaje.Tipo.valueOf(rs.getString("tipo"));
                Viaje viaje = new Viaje(
                        rs.getInt("VCod"),
                        fecha,tipo
                );
                return viaje;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(Viaje objeto) {
        String sql = "UPDATE Viajes SET fecha = ?, tipo = ? WHERE VCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setDate(1,java.sql.Date.valueOf(objeto.getFechaInicio()));
            ps.setString(2,objeto.getTipo().name());
            ps.setInt(3,objeto.getVCod());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Integer id) {
        String sql = "DELETE FROM Viajes WHERE VCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Viaje> listarTodos() {
        List<Viaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM Viajes";

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int VCod = rs.getInt("VCod");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                Viaje.Tipo tipo = Viaje.Tipo.valueOf(rs.getString("tipo"));

                Viaje viaje = new Viaje(VCod, fecha, tipo);
                lista.add(viaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Viaje> filtrar(String texto, LocalDate fecha, String estado) {
        List<Viaje> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM Viajes WHERE 1=1");

        // Filtros dinámicos
        if (texto != null && !texto.isEmpty()) {
            sql.append(" AND (VCod LIKE ? OR VCod IN (SELECT VCod FROM Destino d JOIN Zonas z ON d.ZCod = z.ZCod WHERE z.nombre LIKE ?))");
        }
        if (fecha != null) {
            sql.append(" AND fecha_inicio = ?");
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND estado = ?");
        }

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (texto != null && !texto.isEmpty()) {
                ps.setString(index++, "%" + texto + "%");
                ps.setString(index++, "%" + texto + "%");
            }
            if (fecha != null) {
                ps.setDate(index++, java.sql.Date.valueOf(fecha));
            }
            if (estado != null && !estado.isEmpty()) {
                ps.setString(index++, estado);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Viaje v = new Viaje();
                v.setVCod(rs.getInt("VCod"));
                v.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                v.setTipo(Viaje.Tipo.valueOf(rs.getString("tipo")));
                v.setEstado(Viaje.Estado.valueOf(rs.getString("estado")));
                lista.add(v);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

}



