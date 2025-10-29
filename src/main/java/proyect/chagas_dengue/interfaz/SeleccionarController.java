package proyect.chagas_dengue.interfaz;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import proyect.chagas_dengue.dao.CamionetaDAO;
import proyect.chagas_dengue.modelos.Camioneta;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SeleccionarController<Object> {

    @FXML
    private Label lblTitulo;

    @FXML
    private ListView<Object> listItems; // usar Object en lugar de T

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnSeleccionar;

    @FXML private TextField txtBuscar;

    @FXML private Label lblBuscar;

    private Object seleccionado;
    private Stage stage;

    private List<Object> elementosOriginales = new ArrayList<>();
    private Function<Object, String> mostrarFuncion;

    @FXML
    public void initialize() {
        btnCancelar.setOnAction(e -> {
            seleccionado = null;
            cerrar();
        });

        btnSeleccionar.setOnAction(e -> {
            seleccionado = listItems.getSelectionModel().getSelectedItem();
            System.out.println("Seleccionado en popup: " + seleccionado);
            cerrar();
        });
    }

    public void configurar(String titulo, List<Object> elementos, Function<Object, String> mostrar,boolean conBusqueda) {

        this.mostrarFuncion = mostrar;

        lblTitulo.setText(titulo);

        elementosOriginales.clear();
        elementosOriginales.addAll(elementos);

        // Agregar los objetos reales al ListView
        listItems.getItems().setAll(elementos);

        System.out.println("ListView cargado con: " + listItems.getItems());

        // Configurar cómo se muestra cada elemento
        listItems.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(mostrar.apply(item)); // mostrar texto bonito
                }
            }
        });

        if (txtBuscar != null) {
            txtBuscar.setVisible(conBusqueda);
            txtBuscar.setManaged(conBusqueda);
            lblBuscar.setVisible(conBusqueda);


            if (conBusqueda) {
                txtBuscar.textProperty().addListener((obs, old, newText) -> filtrarLista(newText));
            }
        }

//        lblTitulo.setText(titulo);
//        listItems.getItems().setAll(elementos);
//
//        listItems.setCellFactory(list -> new ListCell<>() {
//            @Override
//            protected void updateItem(Object item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? "" : mostrar.apply(item));
//            }
//        });
    }

    private void filtrarLista(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            listItems.getItems().setAll(elementosOriginales);
        } else {
            String f = filtro.toLowerCase();
            List<Object> filtrados = elementosOriginales.stream()
                    .filter(obj -> mostrarFuncion.apply(obj).toLowerCase().contains(f))
                    .collect(Collectors.toList());
            listItems.getItems().setAll(filtrados);
        }
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

            listItems.getItems().add((Object) c); // <-- objeto Camioneta real


//            listItems.getItems().add(
//                    "Código: " + c.getCodigo() +
//                            " | Patente: " + c.getPatente() +
//                            " | Estado: " + c.getEstado()
//            );
        }
    }
}
