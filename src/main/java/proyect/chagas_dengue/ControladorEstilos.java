package proyect.chagas_dengue;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class ControladorEstilos {
    @FXML
    private void initialize() {
        // Código de inicialización si es necesario
        System.out.println("Controlador inicializado");
    }

    @FXML
    private void handleNuevoViaje() {
        showAlert("Nuevo Viaje", "Iniciando nuevo viaje...");
    }

    @FXML
    private void handleHistorial() {
        showAlert("Historial", "Mostrando historial de viajes...");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
