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

    public List<Viaje> listarPendientes(){
        List<Viaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM Viajes WHERE estado = 'Pendiente';";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int VCod = rs.getInt("VCod");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                Viaje.Tipo tipo = Viaje.Tipo.valueOf(rs.getString("tipo"));
                Viaje.Estado estado = Viaje.Estado.valueOf(rs.getString("estado"));
                Viaje viaje = new Viaje(VCod, fecha, tipo, estado);
                lista.add(viaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

//    public List<Viaje> filtrar(String texto, LocalDate fecha, String estado) {
//        List<Viaje> lista = new ArrayList<>();
////    public List<Viaje> filtrar(String texto, LocalDate fecha, String estado) {
////        List<Viaje> lista = new ArrayList<>();
////
////        StringBuilder sql = new StringBuilder("        SELECT v.VCod, v.fecha, v.tipo, v.estado,\n" +
////                "                GROUP_CONCAT(z.nombre SEPARATOR ', ') AS destinos\n" +
////                "        FROM Viajes v\n" +
////                "        LEFT JOIN Destinos d ON v.VCod = d.VCod\n" +
////                "        LEFT JOIN Zonas z ON d.ZCod = z.ZCod\n" +
////                "        WHERE 1=1\n" +
////                "        \"\"\"");
////
////
////
//////        SELECT * FROM Viajes WHERE 1=1
////
////
////                // Filtros dinámicos
////                if (texto != null && !texto.isEmpty()) {
////                    sql.append(" AND (VCod LIKE ? OR VCod IN (SELECT VCod FROM destinos d JOIN zonas z ON d.ZCod = z.ZCod WHERE z.nombre LIKE ?))");
////                }
////                if (fecha != null) {
////                    sql.append(" AND fecha = ?");
////                }
////                if (estado != null && !estado.isEmpty()) {
////                    sql.append(" AND estado = ?");
////                }
////
////                try (Connection conn = ConexionBD.getInstancia().getConexion();
////                     PreparedStatement ps = conn.prepareStatement(sql.toString())) {
////
////                    int index = 1;
////
////                    if (texto != null && !texto.isEmpty()) {
////                        ps.setString(index++, "%" + texto + "%");
////                        ps.setString(index++, "%" + texto + "%");
////                    }
////                    if (fecha != null) {
////                        ps.setDate(index++, java.sql.Date.valueOf(fecha));
////                    }
////                    if (estado != null && !estado.isEmpty()) {
////                        ps.setString(index++, estado);
////                    }
////
////                    ResultSet rs = ps.executeQuery();
////                    while (rs.next()) {
////                        Viaje v = new Viaje();
////                        v.setVCod(rs.getInt("VCod"));
////                        v.setFechaInicio(rs.getDate("fecha").toLocalDate());
////                        v.setTipo(Viaje.Tipo.valueOf(rs.getString("tipo")));
////                        v.setEstado(Viaje.Estado.valueOf(rs.getString("estado")));
////                        v.setDestinosTexto(rs.getString("destinosTexto"));
////                        lista.add(v);
////                    }
////
////                } catch (SQLException e) {
////                    e.printStackTrace();
////                }
////
////                return lista;
////            }


public List<Viaje> filtrar(String texto, LocalDate fecha, String estado) {
    List<Viaje> lista = new ArrayList<>();



    StringBuilder sql = new StringBuilder(
            "SELECT v.VCod, v.fecha AS fecha, v.tipo, v.estado, " +
                    "       COALESCE(GROUP_CONCAT(z.nombre SEPARATOR ', '), '') AS destinosTexto " +
                    "FROM Viajes v " +
                    "LEFT JOIN Destinos d ON v.VCod = d.VCod " +
                    "LEFT JOIN Zonas z ON d.ZCod = z.ZCod " +
                    "WHERE 1=1 "
    );

    // filtros dinámicos
    if (texto != null && !texto.isEmpty()) {
        // casteamos VCod a CHAR para que el LIKE funcione también sobre el código si lo escriben
        sql.append(" AND (CAST(v.VCod AS CHAR) LIKE ? OR z.nombre LIKE ?)");
    }
    if (fecha != null) {
        sql.append(" AND v.fecha = ?");
    }
    if (estado != null && !estado.isEmpty()) {
        sql.append(" AND v.estado = ?");
    }

    // Agrupamos por todas las columnas no agregadas (evita problemas con ONLY_FULL_GROUP_BY)
    sql.append(" GROUP BY v.VCod, v.fecha, v.tipo, v.estado ");
    sql.append(" ORDER BY v.fecha DESC");

    System.out.println("DEBUG SQL filtrar: " + sql.toString()); // LOG para debug

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

        // DEBUG: imprimir meta-datos y filas devueltas
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        System.out.println("DEBUG columnas devueltas: " + cols);
        for (int i = 1; i <= cols; i++) {
            System.out.println("  col[" + i + "] label=" + md.getColumnLabel(i));
        }

        while (rs.next()) {
            // DEBUG: imprimir cada columna de la fila para ver qué está viniendo
            System.out.print("DEBUG fila: ");
            for (int i = 1; i <= cols; i++) {
                System.out.print(md.getColumnLabel(i) + "=" + rs.getString(i) + " ; ");
            }
            System.out.println();

            Viaje v = new Viaje();
            v.setVCod(rs.getInt("VCod"));
            java.sql.Date d = rs.getDate("fecha");
            if (d != null) v.setFechaInicio(d.toLocalDate());
            if (rs.getString("tipo") != null) v.setTipo(Viaje.Tipo.valueOf(rs.getString("tipo")));
            if (rs.getString("estado") != null) v.setEstado(Viaje.Estado.valueOf(rs.getString("estado")));
            v.setDestinosTexto(rs.getString("destinosTexto")); // <-- toma la columna alias exacto
            lista.add(v);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return lista;
}

}



