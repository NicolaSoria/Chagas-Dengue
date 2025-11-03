package proyect.chagas_dengue.interfaz;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
// ... otras importaciones

public class GenerarController {

    // Campo para registrar si se encontraron zonas de reproducción
    @FXML
    private CheckBox zonasReproduccionCheckBox;

    // Campo para las observaciones detalladas
    @FXML
    private TextArea observacionesTextArea;

    // ... otros métodos del controlador (handleVinchucaCheck, etc.)

    // Ejemplo de cómo obtener los datos:
    public void guardarReporte() {
        boolean hayZonas = zonasReproduccionCheckBox.isSelected();
        String observaciones = observacionesTextArea.getText();

        // Aquí puedes procesar o guardar 'hayZonas' y 'observaciones'
        System.out.println("¿Hay zonas de reproducción?: " + hayZonas);
        System.out.println("Observaciones: " + observaciones);
    }
}