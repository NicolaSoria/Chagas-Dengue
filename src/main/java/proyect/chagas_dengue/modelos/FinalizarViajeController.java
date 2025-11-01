package proyect.chagas_dengue.modelos;

import proyect.chagas_dengue.dao.ViajeDAO;

import java.util.ArrayList;
import java.util.List;

public class FinalizarViajeController {

    public List<Viaje> listarViajes() {
        List<Viaje> lista = new ArrayList<>();
        ViajeDAO viajeDAO = new ViajeDAO();
        lista = viajeDAO.listarPendientes();
        for (Viaje viaje : lista) {
            System.out.println("Cod: " + viaje.getVCod() + "Estado: " + viaje.getEstado());
        }
        return lista;
    }
}
