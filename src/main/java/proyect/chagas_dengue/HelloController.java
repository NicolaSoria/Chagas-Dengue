package proyect.chagas_dengue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    public void manejarNuevo(javafx.event.ActionEvent actionEvent) {
        System.out.println("Nuevo archivo creado");
    }

    @FXML
    public void manejarAbrir(javafx.event.ActionEvent actionEvent) {
        System.out.println("Abrir archivo");
    }

    @FXML
    public void manejarSalir(ActionEvent actionEvent) {
        System.exit(0);
    }
}
