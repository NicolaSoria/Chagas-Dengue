package proyect.chagas_dengue;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    @FXML private VBox listaContainer;
    @FXML private Label lblMensajePDF;

    // Datos de ejemplo para la lista
    private Map<String, Integer> objetos = new HashMap<>();

    @FXML public void initialize(){
        inicializarDatos();
        actualizarListaVisual();
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

    private void inicializarDatos() {
        objetos.put("Guantes de seguridad", 5);
        objetos.put("Máscaras protectoras", 2);
        objetos.put("Trampas para insectos", 4);
        objetos.put("Insecticida", 1);
        objetos.put("Jeringas", 3);
        objetos.put("Tubos de ensayo", 7);
        objetos.put("Termómetros", 2);
        objetos.put("Maquina Pulverizadora", 1);
    }

    @FXML
    private void actualizarLista() {
        actualizarListaVisual();
        lblMensajePDF.setText("Lista actualizada correctamente");
    }

    private void actualizarListaVisual() {
        listaContainer.getChildren().clear();

        int contador = 1;
        for (Map.Entry<String, Integer> entry : objetos.entrySet()) {
            Label label = new Label(String.format("Objeto %d: %s x%d", contador, entry.getKey(), entry.getValue()));
            label.setStyle("-fx-font-size: 16px; -fx-padding: 5px; -fx-text-fill: #363535;");
            listaContainer.getChildren().add(label);
            contador++;
        }
    }

    @FXML
    private void generarPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("lista_elementos_viaje.pdf");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                generarPDFConPDFBox(file);
                lblMensajePDF.setText("PDF generado exitosamente: " + file.getName());
                lblMensajePDF.setStyle("-fx-text-fill: #28a745;");
            } catch (IOException e) {
                e.printStackTrace();
                lblMensajePDF.setText("Error al generar PDF: " + e.getMessage());
                lblMensajePDF.setStyle("-fx-text-fill: #dc3545;");
            }
        }
    }

    private void generarPDFConPDFBox(File file) throws IOException {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float margin = 50;
            float yPosition = 750;
            float lineHeight = 20;

            // ====== Título ======
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Elementos de viaje");
            contentStream.endText();

            yPosition -= lineHeight * 2;

            // ====== Fecha ======
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Generado: " + java.time.LocalDateTime.now());
            contentStream.endText();

            yPosition -= lineHeight * 2;

            // ====== Línea separadora ======
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(margin + 500, yPosition);
            contentStream.stroke();

            yPosition -= lineHeight;

            // ====== Encabezados ======
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Nº");
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 30, yPosition);
            contentStream.showText("ELEMENTO");
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 350, yPosition);
            contentStream.showText("CANTIDAD");
            contentStream.endText();

            yPosition -= lineHeight;

            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(margin + 500, yPosition);
            contentStream.stroke();

            yPosition -= lineHeight;

            // ====== Contenido dinámico ======
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            int contador = 1;

            for (Map.Entry<String, Integer> entry : objetos.entrySet()) {
                // Verificar si se necesita una nueva página
                if (yPosition < margin + 50) {
                    // Cerrar el contentStream actual
                    contentStream.close();

                    // Crear nueva página
                    PDPage newPage = new PDPage();
                    document.addPage(newPage);
                    contentStream = new PDPageContentStream(document, newPage);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);

                    yPosition = 750;
                }

                // Número
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(contador + ".");
                contentStream.endText();

                // Nombre
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 30, yPosition);
                contentStream.showText(entry.getKey());
                contentStream.endText();

                // Cantidad
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 350, yPosition);
                contentStream.showText("x" + entry.getValue());
                contentStream.endText();

                yPosition -= lineHeight;
                contador++;
            }

            // ====== Línea final ======
            yPosition -= lineHeight;
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(margin + 500, yPosition);
            contentStream.stroke();

            yPosition -= lineHeight;

            // ====== Total ======
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            int totalElementos = objetos.values().stream().mapToInt(Integer::intValue).sum();
            contentStream.showText("TOTAL DE ELEMENTOS: " + totalElementos);
            contentStream.endText();

            // Cerrar el último contentStream
            contentStream.close();

            // Guardar documento
            document.save(file);
        }
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
