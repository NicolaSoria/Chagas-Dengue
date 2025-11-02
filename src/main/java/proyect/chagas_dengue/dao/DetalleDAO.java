package proyect.chagas_dengue.dao;

import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.modelos.Detalle;
import proyect.chagas_dengue.modelos.Lote;
import proyect.chagas_dengue.modelos.ProductoQuimico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleDAO {

    private Connection conexion;

    public DetalleDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    /*// Insertar un nuevo detalle
    public void insertar(Detalle detalle) {
        String sql = "INSERT INTO Detalle (DVCod, DLCod, cantTomada, cantUsada) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detalle.getViaje().getVCod());
            ps.setInt(2, detalle.getLote().getDLCod());
            ps.setInt(3, detalle.getCantTomada());
            ps.setInt(4, detalle.getCantUsada());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    detalle.setDCod(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar cantidad usada
    public void actualizarCantUsada(int detalleId, int cantUsada) {
        String sql = "UPDATE Detalle SET cantUsada = ? WHERE DCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cantUsada);
            ps.setInt(2, detalleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtener todos los detalles de un viaje
    public List<Detalle> obtenerDetallesPorViaje(int viajeId) {
        List<Detalle> lista = new ArrayList<>();
        String sql = "SELECT d.DCod, d.DVCod, d.DLCod, d.cantTomada, d.cantUsada, " +
                "l.PCod, l.nombre AS productoNombre, l.presentacion " +
                "FROM Detalle d " +
                "JOIN Lotes l ON d.DLCod = l.DLCod " +
                "WHERE d.DVCod = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, viajeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {


                // Crear el Detalle
                Detalle detalle = new Detalle(
                        rs.getInt("DCod"), viaje,
                        lote,
                        rs.getInt("cantTomada"),
                        rs.getInt("cantUsada")
                );

                lista.add(detalle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    // Devolver sobrante al lote
    public void devolverSobrante(Detalle detalle) {
        int sobrante = detalle.getSobrante();
        if (sobrante <= 0) return;

        String sql = "UPDATE Lotes SET cantidad = cantidad + ? WHERE LCod = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, sobrante);
            ps.setInt(2, detalle.getLote().getDLCod());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void finalizarViaje(List<Detalle> detalleConCantUsada) {
        for(Detalle detalle : detalleConCantUsada) {
            actualizarCantUsada(detalle.getDCod(),detalle.getCantUsada());

            devolverSobrante(detalle);
        }
    }*/
}

