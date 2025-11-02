package proyect.chagas_dengue.interfaz;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import proyect.chagas_dengue.dao.*;
import proyect.chagas_dengue.modelos.*;

import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Insets;


public class ConsultarController {

    @FXML private TextField txtBuscador;
    @FXML private DatePicker dpFechaFiltro;
    @FXML private ComboBox<Viaje.Estado> cbEstadoFiltro;
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
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destinosTexto"));

        // Cargar ComboBox de estados
        cbEstadoFiltro.getItems().add(null);
        cbEstadoFiltro.getItems().addAll(Viaje.Estado.values());
        cbEstadoFiltro.setPromptText("Seleccionar estado...");


        viajes.setAll(new ViajeDAO().filtrar(null, null, null)); //fix para mostrar correctamente todo al inicio

        tableViajes.setItems(viajes);

        // Cargar lista inicial
//        cargarViajes();

        // Evento de búsqueda
        txtBuscador.textProperty().addListener((obs, old, newVal) -> filtrar());
        dpFechaFiltro.valueProperty().addListener((obs, old, newVal) -> filtrar());
        cbEstadoFiltro.valueProperty().addListener((obs, old, newVal) -> filtrar());

        // Configurar botón detalle
        btnDetalle.disableProperty().bind(tableViajes.getSelectionModel().selectedItemProperty().isNull());

        btnDetalle.setOnAction(e -> mostrarDetalles());


    }

//    private void cargarViajes() {
//        ViajeDAO dao = new ViajeDAO();
//        List<Viaje> lista = dao.listarTodos(); // suponiendo que tenés este método
//        viajes.setAll(lista);
//    }

    private void filtrar() {
        // ejemplo básico de filtrado (ajustalo a tu lógica)
        String texto = txtBuscador.getText() != null ? txtBuscador.getText().toLowerCase() : "";
        LocalDate fecha = dpFechaFiltro.getValue();
       // String estado = cbEstadoFiltro.getValue();
        String estado = cbEstadoFiltro.getValue() != null ? cbEstadoFiltro.getValue().toString() : null;


        viajes.setAll(new ViajeDAO().filtrar(texto, fecha, estado));
    }

    @FXML
    private void onVerDetalle() {
        Viaje seleccionado = tableViajes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            System.out.println("Ver detalles del viaje: " + seleccionado.getVCod());
            // abrir popup o cargar otro panel con detalles
        }
    }

    public void recargarViajes() {
        viajes.setAll(new ViajeDAO().filtrar(null, null, null));
    }
    

private void mostrarDetalles() {
    Viaje viaje = tableViajes.getSelectionModel().getSelectedItem();
    if (viaje == null) return;

    Stage popup = new Stage();
    popup.setTitle("Detalles del viaje #" + viaje.getVCod());
    popup.initModality(Modality.APPLICATION_MODAL);

    // Contenedor principal
    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #2b2b2b;");

    // --- Título principal ---
    Label lblTitulo = new Label("Detalles del Viaje");
    lblTitulo.setStyle("""
        -fx-font-size: 20px;
        -fx-font-weight: bold;
        -fx-text-fill: #ffffff;
    """);

    Separator sep1 = new Separator();

    // --- Información general del viaje ---
    GridPane infoViaje = new GridPane();
    infoViaje.setHgap(10);
    infoViaje.setVgap(8);

    infoViaje.add(new Label("Código:"), 0, 0);
    infoViaje.add(new Label(String.valueOf(viaje.getVCod())), 1, 0);

    infoViaje.add(new Label("Fecha:"), 0, 1);
    infoViaje.add(new Label(String.valueOf(viaje.getFechaInicio())), 1, 1);

    infoViaje.add(new Label("Tipo:"), 0, 2);
    infoViaje.add(new Label(String.valueOf(viaje.getTipo())), 1, 2);

    infoViaje.add(new Label("Estado:"), 0, 3);
    infoViaje.add(new Label(String.valueOf(viaje.getEstado())), 1, 3);

    infoViaje.add(new Label("Destinos:"), 0, 4);
    infoViaje.add(new Label(String.valueOf(viaje.getDestinosTexto())), 1, 4);

    infoViaje.getChildren().forEach(node -> {
        if (node instanceof Label lbl) {
            lbl.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px;");
        }
    });

    // --- Sección de recursos asociados ---
    Separator sep2 = new Separator();
    Label lblRecursos = new Label("Recursos asignados:");
    lblRecursos.setStyle("""
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-text-fill: #ffffff;
    """);

    VBox recursosBox = new VBox(6);

    try {
        CamionetaDAO camDao = new CamionetaDAO();
        List<Camioneta> camList = camDao.listarPorViaje(viaje.getVCod());

        Label lblCamionetas = new Label("Camionetas: " + formatearLista(camList));
        lblCamionetas.setStyle("-fx-text-fill: #e0e0e0;");

        recursosBox.getChildren().add(lblCamionetas);

    } catch (Exception ex) {
        Label lblError = new Label("Error al cargar recursos: " + ex.getMessage());
        lblError.setStyle("-fx-text-fill: #ff6b6b;");
        recursosBox.getChildren().add(lblError);
    }

    // --- Botón de cierre ---
    Button btnCerrar = new Button("Cerrar");
    btnCerrar.setStyle("""
        -fx-background-color: #007acc;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-padding: 6px 12px;
        -fx-background-radius: 6px;
    """);
    btnCerrar.setOnAction(e -> popup.close());

    HBox footer = new HBox(btnCerrar);
    footer.setAlignment(Pos.CENTER_RIGHT);

    // --- Estructura final ---
    root.getChildren().addAll(lblTitulo, sep1, infoViaje, sep2, lblRecursos, recursosBox, footer);

    Scene scene = new Scene(root, 480, 420);
    popup.setScene(scene);
    popup.showAndWait();
}





    private String formatearLista(List<?> lista) {
        if (lista == null || lista.isEmpty()) return "—";
        return lista.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }


}
