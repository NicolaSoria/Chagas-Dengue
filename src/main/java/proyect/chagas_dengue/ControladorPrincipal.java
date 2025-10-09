package proyect.chagas_dengue;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.swing.*;

public class ControladorPrincipal {
    @FXML private Button buttonAgregarSelPaneElmPrepViaje;
    @FXML private Button buttonQuitarSelPaneElmPrepViaje;
    @FXML private Button buttonSiguientePaneElmPrepViaje;
    @FXML private Button buttonCancelarPaneElmPrepViaje;
    @FXML private DatePicker datPckPaneDestinoPrepViaje;
    @FXML private ChoiceBox chBoxPaneDestPrepViaje;
    @FXML private Button buttonCancelarPaneDestinoPrepViaje;
    @FXML private Button buttonSiguientePaneDestPrepViaje;
    @FXML private Pane paneListado;
    @FXML private Pane paneDestino;
    @FXML private Pane paneInicio;
    @FXML private Pane paneElementos;
    @FXML private StackPane stackPanePrepViaje;

    @FXML public void initialize(){
        mostrarPaneInicio();
    }

    public void mostrarPaneInicio(){
        Pane paneInicio1 = paneInicio;
        paneInicio1.toFront();
    }

    @FXML private void mostrarPaneDestino(){
        Pane paneDestino1 = paneDestino;
        paneDestino1.toFront();
    }

    @FXML private void mostrarPaneElementos() {
        Pane paneElementos1 = paneElementos;
        paneElementos1.toFront();
    }

    @FXML private void mostrarPaneListado(){
        Pane paneListado1 = paneListado;
        paneListado1.toFront();
    }
}