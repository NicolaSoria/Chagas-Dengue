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


        // Evento de búsqueda
        txtBuscador.textProperty().addListener((obs, old, newVal) -> filtrar());
        dpFechaFiltro.valueProperty().addListener((obs, old, newVal) -> filtrar());
        cbEstadoFiltro.valueProperty().addListener((obs, old, newVal) -> filtrar());

        // Configurar botón detalle
        btnDetalle.disableProperty().bind(tableViajes.getSelectionModel().selectedItemProperty().isNull());

        btnDetalle.setOnAction(e -> mostrarDetalles());


    }


    private void filtrar() {

        String texto = txtBuscador.getText() != null ? txtBuscador.getText().toLowerCase() : "";
        LocalDate fecha = dpFechaFiltro.getValue();
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
        Viaje viajeSeleccionado = tableViajes.getSelectionModel().getSelectedItem();
        if (viajeSeleccionado == null) return;

        // Obtenemos los datos completos del viaje (incluyendo recursos)
        ViajeDAO dao = new ViajeDAO();
        Viaje viaje = dao.obtenerViajeConRecursos(viajeSeleccionado.getVCod());
        if (viaje == null) return;

        Stage popup = new Stage();
        popup.setTitle("Detalles del viaje #" + viaje.getVCod());
        popup.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(18);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #3a3a3a; -fx-border-radius: 8px;");

        // Título
        Label lblTitulo = new Label("Detalles del Viaje");
        lblTitulo.setStyle("""
        -fx-font-size: 20px;
        -fx-font-weight: bold;
        -fx-text-fill: #ffffff;
    """);

        Separator sep1 = new Separator();

        // Informacion principal del viaje
        GridPane info = new GridPane();
        info.setHgap(12);
        info.setVgap(8);
        info.setPadding(new Insets(5, 0, 10, 0));

        addInfoRow(info, 0, "Código:", String.valueOf(viaje.getVCod()));
        addInfoRow(info, 1, "Fecha:", String.valueOf(viaje.getFechaInicio()));
        addInfoRow(info, 2, "Tipo:", String.valueOf(viaje.getTipo()));
        addInfoRow(info, 3, "Estado:", String.valueOf(viaje.getEstado()));
        addInfoRow(info, 4, "Destinos:", viaje.getDestinosTexto());

        Separator sep2 = new Separator();

        Label lblRecursos = new Label("Recursos asignados");
        lblRecursos.setStyle("""
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-text-fill: #ffffff;
    """);

        // Recursos (cada uno en su propio bloque si tiene datos)
        VBox recursosBox = new VBox(6);
        recursosBox.setPadding(new Insets(5, 0, 0, 0));


        agregarRecurso(recursosBox, "Camionetas", viaje.getCamionetasTexto());
        agregarRecurso(recursosBox, "Empleados", viaje.getEmpleadosTexto());
        agregarRecurso(recursosBox, "Máquinas", viaje.getMaquinasTexto());
        agregarRecurso(recursosBox, "Productos Químicos", viaje.getProductosTexto());

        // Botón cerrar
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
        footer.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(lblTitulo, sep1, info, sep2, lblRecursos, recursosBox, footer);

        Scene scene = new Scene(root, 520, 460);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #cccccc; -fx-font-weight: bold;");
        Label val = new Label(value != null ? value : "-");
        val.setStyle("-fx-text-fill: #e0e0e0;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private void agregarRecurso(VBox box, String titulo, String contenido) {
        if (contenido == null || contenido.isEmpty()) return;
        Label lbl = new Label(titulo + ": " + contenido);
        lbl.setStyle("-fx-text-fill: #d0d0d0; -fx-font-size: 13px;");
        box.getChildren().add(lbl);
    }

    private String formatearLista(List<?> lista) {
        if (lista == null || lista.isEmpty()) return "—";
        return lista.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public void actualizarListaViajes() {
        try {
            ViajeDAO dao = new ViajeDAO();
            List<Viaje> lista = dao.filtrar(null, null, null);
            viajes.setAll(lista);
            tableViajes.setItems(viajes);
            System.out.println("DEBUG: lista de viajes actualizada (" + lista.size() + " registros).");

            if (lista.isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Sin viajes cargados");
                alerta.setHeaderText(null);
                alerta.setContentText("No hay viajes registrados en el sistema.");
                alerta.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al actualizar la lista de viajes.");
        }
    }


}
