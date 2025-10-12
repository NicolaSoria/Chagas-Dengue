package proyect.chagas_dengue;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import proyect.chagas_dengue.dao.CamionetaDAO;
import proyect.chagas_dengue.interfaz.SeleccionarController;
import proyect.chagas_dengue.modelos.Camioneta;

import java.awt.event.ActionEvent;
import java.util.List;

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

    @FXML
    private Button btnAñadirCamioneta;

    @FXML
    private void onAñadirCamioneta() {
        try {
            // Obtener camionetas reales desde la base de datos
            CamionetaDAO dao = new CamionetaDAO();
            List<Camioneta> camionetas = dao.listarDisponibles(); // o el método equivalente que tengas

            // Cargar FXML del popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Seleccionar1.fxml"));
            Parent root = loader.load();

            // Obtener controlador y configurar datos
            SeleccionarController controller = loader.getController();
            controller.configurar("Seleccionar Camioneta", camionetas, Object::toString);

            // Crear ventana popup
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.initOwner(btnAñadirCamioneta.getScene().getWindow()); // ventana padre
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Obtener elemento seleccionado
            Object seleccionado = controller.getSeleccionado();
            if (seleccionado != null) {
                System.out.println("Camioneta seleccionada: " + seleccionado);
                // acá agregás a la tabla o donde necesites
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

