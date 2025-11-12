package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViajeDAO implements interfaceDAO<Viaje,Integer>{

    private Connection conexion;

    public ViajeDAO(){this.conexion = ConexionBD.getInstancia().getConexion();}

    @Override
    public void insertar(Viaje viaje) {
        String sqlViaje = "INSERT INTO Viajes (fecha, tipo, estado) VALUES (?, ?, ?)";
        String sqlDestino = "INSERT INTO Destinos (VCod, ZCod) VALUES (?, ?)";
        String sqlUsan = "INSERT INTO Usan (VCod, CCod) VALUES (?, ?)";
        String sqlMaquinas = "INSERT INTO Transporta (VCod, MCod) VALUES (?, ?)";
        String sqlEmpleados = "INSERT INTO Realizan (ECod, VCod) VALUES (?, ?)";
        String sqlDetalle = "INSERT INTO Detalle (DVCod, DSPCod, cantTomada, cantUsada) VALUES (?, ?, ?, ?)";
        String sqlRegistro = "INSERT INTO Registro (ZCod, TCod, VCod, observacion) VALUES (?, ?, ?, ?)";
        String sqlStockUpdate = "UPDATE Stocks SET total = total - ? WHERE SPCod = ?";

        try {
            conexion.setAutoCommit(false); // iniciar transacción

            PreparedStatement psViaje = conexion.prepareStatement(sqlViaje, Statement.RETURN_GENERATED_KEYS);
            psViaje.setDate(1, java.sql.Date.valueOf(viaje.getFechaInicio()));
            psViaje.setString(2, viaje.getTipo().name());
            psViaje.setString(3, viaje.getEstado().name());
            psViaje.executeUpdate();

            ResultSet rs = psViaje.getGeneratedKeys();
            if (rs.next()) {
                viaje.setVCod(rs.getInt(1)); // asignar ID generado
            }

            if (viaje.getZonas() != null && !viaje.getZonas().isEmpty()) {
                try (PreparedStatement psDestino = conexion.prepareStatement(sqlDestino)) {
                    for (Zona z : viaje.getZonas()) {
                        psDestino.setInt(1, viaje.getVCod());
                        psDestino.setInt(2, z.getZCod());
                        psDestino.addBatch();
                    }
                    psDestino.executeBatch();
                }
            }

            if (viaje.getCamionetas() != null && !viaje.getCamionetas().isEmpty()) {
                try (PreparedStatement psUsan = conexion.prepareStatement(sqlUsan)) {
                    for (Camioneta c : viaje.getCamionetas()) {
                        psUsan.setInt(1, viaje.getVCod());
                        psUsan.setInt(2, c.getCCod());
                        psUsan.addBatch();
                    }
                    psUsan.executeBatch();
                }
            }

            if (viaje.getMaquinas() != null && !viaje.getMaquinas().isEmpty()) {
                try (PreparedStatement psMaquinas = conexion.prepareStatement(sqlMaquinas)) {
                    for (Maquina m : viaje.getMaquinas()) {
                        psMaquinas.setInt(1, viaje.getVCod());
                        psMaquinas.setInt(2, m.getMCod());
                        psMaquinas.addBatch();
                    }
                    psMaquinas.executeBatch();
                }
            }

            if (viaje.getEmpleados() != null && !viaje.getEmpleados().isEmpty()) {
                try (PreparedStatement psEmpleados = conexion.prepareStatement(sqlEmpleados)) {
                    for (Empleado e : viaje.getEmpleados()) {
                        psEmpleados.setInt(1,e.getECod());
                        psEmpleados.setInt(2, viaje.getVCod());
                        psEmpleados.addBatch();
                    }
                    psEmpleados.executeBatch();
                }
            }

            if (viaje.getDetalles() != null && !viaje.getDetalles().isEmpty()) {
                try (PreparedStatement psDet = conexion.prepareStatement(sqlDetalle);
                     PreparedStatement psStock = conexion.prepareStatement(sqlStockUpdate)) {

                    for (Detalle det : viaje.getDetalles()) {
                        int spCod = det.getStock().getSPCod();
                        int cantTomada = det.getCantTomada();

                        // Insertar detalle
                        psDet.setInt(1, viaje.getVCod());
                        psDet.setInt(2, spCod);
                        psDet.setInt(3, cantTomada);
                        psDet.setInt(4, det.getCantUsada()); // normalmente 0 al inicio
                        psDet.addBatch();

                        // Actualizar stock
                        psStock.setInt(1, cantTomada);
                        psStock.setInt(2, spCod);
                        psStock.addBatch();
                    }

                    psDet.executeBatch();
                    psStock.executeBatch();
                }
            }

            if (viaje.getRegistros() != null && !viaje.getRegistros().isEmpty()) {
                try (PreparedStatement psReg = conexion.prepareStatement(sqlRegistro)) {
                    for (Registro r : viaje.getRegistros()) {
                        psReg.setInt(1,r.getZona().getZCod());
                        psReg.setInt(2,r.getTarea().getTCod());
                        psReg.setInt(3,r.getViaje().getVCod());
                        psReg.setNull(4, java.sql.Types.VARCHAR);
                        psReg.addBatch();
                    }
                    psReg.executeBatch();
                }
            }

            conexion.commit(); //es correcto
            conexion.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                conexion.rollback(); // revertir si  falla
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /*public void insertar(Viaje viaje) {

        try{
            String sqlViaje = "INSERT INTO Viajes (fechainicio, tipo, estado) VALUES (?, ?, ?)";
            PreparedStatement psViaje = conexion.prepareStatement(sqlViaje, Statement.RETURN_GENERATED_KEYS);
            psViaje.setDate(1, java.sql.Date.valueOf(viaje.getFechaInicio()));
            psViaje.setString(2, viaje.getTipo().name());
            psViaje.setString(3, viaje.getEstado().name());
            psViaje.executeUpdate();

            ResultSet rs = psViaje.getGeneratedKeys();
            if (rs.next()) {
                viaje.setVCod(rs.getInt(1)); // asigna el ID generado
            }

            if (viaje.getZonas() != null) {
                String sqlDestino = "INSERT INTO Destinos (VCod, ZCod) VALUES (?, ?)";
                PreparedStatement psDestino = conexion.prepareStatement(sqlDestino);
                for (Zona z : viaje.getZonas()) {
                    psDestino.setInt(1, viaje.getVCod());
                    psDestino.setInt(2, z.getZCod());
                    psDestino.addBatch();
                }
                psDestino.executeBatch();
            }

            if (viaje.getCamionetas() != null) {
                String sqlUsan = "INSERT INTO Usan (VCod, CCod) VALUES (?, ?)";
                PreparedStatement psUsan = conexion.prepareStatement(sqlUsan);
                for (Camioneta c : viaje.getCamionetas()) {
                    psUsan.setInt(1, viaje.getVCod());
                    psUsan.setInt(2, c.getCCod());
                    psUsan.addBatch();
                }
                psUsan.executeBatch();
            }

            if (viaje.getMaquinas() != null) {
                String sqlMaquinas = "INSERT INTO Transporta (vCod, mCod) VALUES (?, ?)";
                PreparedStatement psMaquinas = conexion.prepareStatement(sqlMaquinas);
                for (Maquina m : viaje.getMaquinas()) {
                    psMaquinas.setInt(1, viaje.getVCod());
                    psMaquinas.setInt(2, m.getMCod());
                    psMaquinas.addBatch();
                }
                psMaquinas.executeBatch();
            }

        /*String sql = "INSERT INTO Viajes (fecha,tipo) VALUES (?,?)";

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
    }*/

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
        String sql = "UPDATE Viajes SET estado = ? WHERE VCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1,objeto.getEstado().name());
            ps.setInt(2,objeto.getVCod());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void actualizarCantU(Viaje objeto, Detalle detalle) {
        String sql = "UPDATE Detalle SET cantUsada = ? WHERE DCod = ? AND DVCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, detalle.getCantUsada());
            ps.setInt(2, detalle.getDCod());
            ps.setInt(3, objeto.getVCod());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarStock(Viaje objeto, Stock stock) {
        String sql = "UPDATE Stocks SET total = ? WHERE SPCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,stock.getTotal());
            ps.setInt(2,stock.getSPCod());
            ps.executeUpdate();
        } catch (SQLException e) {
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

    /*public void insertarDest(int VCod, int ZCod) {
        String sql = "INSERT INTO Destinos (VCod, ZCod) VALUES (?,?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,VCod);
            ps.setInt(2,ZCod);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertarUsan(int VCod, int CCod) {
        String sql = "INSERT INTO Usan (VCod, CCod) VALUES (?,?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,VCod);
            ps.setInt(2,CCod);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertTra(int VCod, int MCod) {
        String sql = "INSERT INTO Transporta (VCod, MCod) VALUES (?,?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,VCod);
            ps.setInt(2,MCod);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertRlz(int VCod, int ECod) {
        String sql = "INSERT INTO Realizan (VCod, ECod) VALUES (?,?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,VCod);
            ps.setInt(2,ECod);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }*/

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

    public List<Detalle> listarDetPorViaje(int VCod){
        List<Detalle> detalles = new ArrayList<>();
        String sql = "SELECT d.DCod, d.cantTomada, d.cantUsada, s.SPCod, s.total, p.nombre " +
                "FROM Detalle d " +
                "JOIN Stocks s ON d.DSPCod = s.SPCod " +
                "JOIN Productos p ON s.SPCod = p.PCod " +
                "WHERE d.DVCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1,VCod);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setSPCod(rs.getInt("SPCod"));
                stock.setTotal(rs.getInt("total"));
                ProductoQuimico producto = new ProductoQuimico();
                producto.setNombre(rs.getString("nombre"));
                stock.setProducto(producto);

                Detalle d = new Detalle();
                d.setDCod(rs.getInt("DCod"));
                d.setCantTomada(rs.getInt("cantTomada"));
                d.setCantUsada(rs.getInt("cantUsada"));
                d.setStock(stock);

                detalles.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }


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


    if (texto != null && !texto.isEmpty()) {
        sql.append(" AND (CAST(v.VCod AS CHAR) LIKE ? OR z.nombre LIKE ?)");
    }
    if (fecha != null) {
        sql.append(" AND v.fecha = ?");
    }
    if (estado != null && !estado.isEmpty()) {
        sql.append(" AND v.estado = ?");
    }
    sql.append(" GROUP BY v.VCod, v.fecha, v.tipo, v.estado ");
    sql.append(" ORDER BY v.fecha DESC");


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
            java.sql.Date d = rs.getDate("fecha");
            if (d != null) v.setFechaInicio(d.toLocalDate());
            if (rs.getString("tipo") != null) v.setTipo(Viaje.Tipo.valueOf(rs.getString("tipo")));
            if (rs.getString("estado") != null) v.setEstado(Viaje.Estado.valueOf(rs.getString("estado")));
            v.setDestinosTexto(rs.getString("destinosTexto"));
            lista.add(v);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return lista;
}

    public Viaje obtenerViajeConRecursos(int vCod) {
        Viaje viaje = null;

        String sql = """
        SELECT v.VCod, v.fecha, v.tipo, v.estado,
               GROUP_CONCAT(DISTINCT z.nombre SEPARATOR ', ') AS zonas,
               GROUP_CONCAT(DISTINCT c.patente SEPARATOR ', ') AS camionetas,
               GROUP_CONCAT(DISTINCT e.nombre SEPARATOR ', ') AS empleados,
               GROUP_CONCAT(DISTINCT m.modelo SEPARATOR ', ') AS maquinas,
               GROUP_CONCAT(DISTINCT p.nombre SEPARATOR ', ') AS productos
/*              GROUP_CONCAT(DISTINCT es.descripcion SEPARATOR ', ') AS elementos */
        FROM Viajes v
        LEFT JOIN destinos des ON v.VCod = des.VCod
        LEFT JOIN zonas z ON des.ZCod = z.ZCod
        LEFT JOIN Usan u ON v.VCod = u.VCod
        LEFT JOIN Camionetas c ON u.CCod = c.CCod
        LEFT JOIN Realizan r ON v.VCod = r.VCod
        LEFT JOIN Empleados e ON r.ECod = e.ECod
        LEFT JOIN transporta vm ON v.VCod = vm.VCod
        LEFT JOIN Maquinas m ON vm.MCod = m.MCod
        
 /*       LEFT JOIN detalle vp ON v.VCod = vp.VCod
        LEFT JOIN Productos p ON vp.PCod = p.PCod   */
        
        LEFT JOIN detalle d ON v.VCod = d.DVCod
        LEFT JOIN productos p ON d.DSPCod = p.PCod
        
/*        LEFT JOIN Viaje_Elemento ve ON v.VCod = ve.VCod */
/*        LEFT JOIN ElementosSeguridad es ON ve.ESCod = es.ESCod */
        WHERE v.VCod = ?
        GROUP BY v.VCod;
    """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vCod);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                viaje = new Viaje();
                viaje.setVCod(rs.getInt("VCod"));
                viaje.setFechaInicio(rs.getDate("fecha").toLocalDate());
                viaje.setTipo(Viaje.Tipo.valueOf(rs.getString("tipo")));
                viaje.setEstado(Viaje.Estado.valueOf(rs.getString("estado")));
                viaje.setDestinosTexto(rs.getString("zonas"));

                // Guardamos los recursos como texto concatenado
                viaje.setCamionetasTexto(rs.getString("camionetas"));
                viaje.setEmpleadosTexto(rs.getString("empleados"));
                viaje.setMaquinasTexto(rs.getString("maquinas"));
                viaje.setProductosTexto(rs.getString("productos"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return viaje;
    }

    public Viaje obtenerViajeConRecursosYTareas(int vCod) {
        Viaje viaje = null;

        String sql = """
    SELECT v.VCod, v.fecha, v.tipo, v.estado,
           GROUP_CONCAT(DISTINCT z.nombre SEPARATOR ', ') AS zonas,
           GROUP_CONCAT(DISTINCT c.patente SEPARATOR ', ') AS camionetas,
           GROUP_CONCAT(DISTINCT e.nombre SEPARATOR ', ') AS empleados,
           GROUP_CONCAT(DISTINCT m.modelo SEPARATOR ', ') AS maquinas,
           GROUP_CONCAT(DISTINCT p.nombre SEPARATOR ', ') AS productos,
           GROUP_CONCAT(DISTINCT t.tipo SEPARATOR ', ') AS tareas
    FROM Viajes v
    LEFT JOIN destinos des ON v.VCod = des.VCod
    LEFT JOIN zonas z ON des.ZCod = z.ZCod
    LEFT JOIN Usan u ON v.VCod = u.VCod
    LEFT JOIN Camionetas c ON u.CCod = c.CCod
    LEFT JOIN Realizan r ON v.VCod = r.VCod
    LEFT JOIN Empleados e ON r.ECod = e.ECod
    LEFT JOIN transporta vm ON v.VCod = vm.VCod
    LEFT JOIN Maquinas m ON vm.MCod = m.MCod
    LEFT JOIN detalle d ON v.VCod = d.DVCod
    LEFT JOIN productos p ON d.DSPCod = p.PCod
    LEFT JOIN Registro reg ON v.VCod = reg.VCod
    LEFT JOIN tareas t ON reg.TCod = t.TCod
    WHERE v.VCod = ?
    GROUP BY v.VCod;
    """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, vCod);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                viaje = new Viaje();
                viaje.setVCod(rs.getInt("VCod"));
                viaje.setFechaInicio(rs.getDate("fecha").toLocalDate());
                viaje.setTipo(Viaje.Tipo.valueOf(rs.getString("tipo")));
                viaje.setEstado(Viaje.Estado.valueOf(rs.getString("estado")));
                viaje.setDestinosTexto(rs.getString("zonas"));
                viaje.setCamionetasTexto(rs.getString("camionetas"));
                viaje.setEmpleadosTexto(rs.getString("empleados"));
                viaje.setMaquinasTexto(rs.getString("maquinas"));
                viaje.setProductosTexto(rs.getString("productos"));
                viaje.setRegistrosTexto(rs.getString("tareas"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return viaje;
    }
}



