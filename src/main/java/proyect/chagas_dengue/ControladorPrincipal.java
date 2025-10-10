package proyect.chagas_dengue;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.awt.event.ActionEvent;

public class ControladorPrincipal {

    @FXML private RadioButton radbtnDiaPaneDestPrepViaje;
    @FXML private RadioButton radbtnBarrPaneDestPrepViaje;
    @FXML private ToggleGroup tipoViajeOpcs;
    @FXML private AnchorPane anchorElementos;
    @FXML private ScrollPane scrPaneElmsPrepViaje;
    @FXML private Tab tabMenu;
    @FXML private TabPane mainTabPane;
    @FXML private Tab tabDestPrepViaje;
    @FXML private Tab tabElmsPrepViaje;
    @FXML private ChoiceBox chBoxDestPnDestPrepViaje;
    @FXML private DatePicker datePnDestPrepViaje;
    @FXML private Button btnSigPnDestPrepViaje;
    @FXML private TabPane tabPanePrepViaje;

    @FXML public void initialize(){
        mostrarPaneIncio();
    }

    @FXML public void mostrarPaneIncio(){
        mainTabPane.getSelectionModel().select(tabMenu);
    }

    @FXML private void mostrarPanelElementos(){
        tabPanePrepViaje.getSelectionModel().select(tabElmsPrepViaje);
        Platform.runLater(() -> {
            scrPaneElmsPrepViaje.setVvalue(0); // Vertical scroll al tope
            scrPaneElmsPrepViaje.setHvalue(0); // Horizontal scroll al inicio (opcional)
        });
    }

    /*@FXML
    private void mostrarSeleccion() {
        Toggle seleccionado = tipoViajeOpcs.getSelectedToggle();

        if (seleccionado == radbtnDiaPaneDestPrepViaje) {
            System.out.println("Seleccion: Opción Diario");
        } else if (seleccionado == radbtnBarrPaneDestPrepViaje) {
            System.out.println("Seleccion: Opción Barrido");
        } else {
            System.out.println("No hay ninguna opción seleccionada");
        }
    }*/
}
