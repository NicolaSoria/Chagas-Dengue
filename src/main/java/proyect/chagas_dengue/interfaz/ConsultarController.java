package proyect.chagas_dengue.interfaz;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import proyect.chagas_dengue.dao.ViajeDAO;
import proyect.chagas_dengue.modelos.Viaje;
import java.time.LocalDate;
import java.util.List;

public class ConsultarController {

    @FXML private TextField txtBuscador;
    @FXML private DatePicker dpFechaFiltro;
    @FXML private ComboBox<String> cbEstadoFiltro;
    @FXML private TableView<Viaje> tableViajes;
    @FXML private TableColumn<Viaje, Integer> colCodigo;
    @FXML private TableColumn<Viaje, String> colDestino;
    @FXML private TableColumn<Viaje, LocalDate> colFecha;
    @FXML private TableColumn<Viaje, String> colEstado;
    @FXML private Button btnDetalle;

    private final ObservableList<Viaje> viajes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("VCod"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destino"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));


        tableViajes.setItems(viajes);

        // Cargar lista inicial
        cargarViajes();

        // Evento de búsqueda
        txtBuscador.textProperty().addListener((obs, old, newVal) -> filtrar());
        dpFechaFiltro.valueProperty().addListener((obs, old, newVal) -> filtrar());
        cbEstadoFiltro.valueProperty().addListener((obs, old, newVal) -> filtrar());

        // Configurar botón detalle
        btnDetalle.disableProperty().bind(tableViajes.getSelectionModel().selectedItemProperty().isNull());
    }

    private void cargarViajes() {
        ViajeDAO dao = new ViajeDAO();
        List<Viaje> lista = dao.listarTodos(); // suponiendo que tenés este método
        viajes.setAll(lista);
    }

    private void filtrar() {
        // ejemplo básico de filtrado (ajustalo a tu lógica)
        String texto = txtBuscador.getText() != null ? txtBuscador.getText().toLowerCase() : "";
        LocalDate fecha = dpFechaFiltro.getValue();
        String estado = cbEstadoFiltro.getValue();

        viajes.setAll(new ViajeDAO().filtrar(texto, fecha, estado)); // podés implementar esto
    }

    @FXML
    private void onVerDetalle() {
        Viaje seleccionado = tableViajes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            System.out.println("Ver detalles del viaje: " + seleccionado.getVCod());
            // abrir popup o cargar otro panel con detalles
        }
    }
}
