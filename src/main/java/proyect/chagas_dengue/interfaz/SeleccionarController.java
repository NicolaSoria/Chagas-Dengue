package proyect.chagas_dengue.interfaz;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import proyect.chagas_dengue.dao.CamionetaDAO;
import proyect.chagas_dengue.modelos.Camioneta;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class SeleccionarController {

    @FXML
    private Label lblTitulo;

    @FXML
    private ListView<Object> listItems; // usar Object en lugar de T

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnSeleccionar;

    private Object seleccionado;
    private Stage stage;

    @FXML
    public void initialize() {
        btnCancelar.setOnAction(e -> {
            seleccionado = null;
            cerrar();
        });

        btnSeleccionar.setOnAction(e -> {
            seleccionado = listItems.getSelectionModel().getSelectedItem();
            cerrar();
        });
    }

    public void configurar(String titulo, List<?> elementos, Function<Object, String> mostrar) {
        lblTitulo.setText(titulo);
        listItems.getItems().setAll(elementos);

        listItems.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : mostrar.apply(item));
            }
        });
    }

    public Object getSeleccionado() {
        return seleccionado;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void cerrar() {
        if (stage != null) {
            stage.close();
        }
    }

    private void cargarCamionetas() throws SQLException {
        CamionetaDAO dao = new CamionetaDAO();
        List<Camioneta> camionetas = dao.listarDisponibles();

        for (Camioneta c : camionetas) {
            listItems.getItems().add(
                    "Código: " + c.getCodigo() +
                            " | Patente: " + c.getPatente() +
                            " | Estado: " + c.getEstado()
            );
        }
    }
}
