package proyect.chagas_dengue.interfaz;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import proyect.chagas_dengue.dao.ViajeDAO;
import proyect.chagas_dengue.modelos.Empleado;
import proyect.chagas_dengue.modelos.Viaje;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GenerarController {

    @FXML private CheckBox zona1ReproduccionCheckBox;
    @FXML private TextArea zona1ObservacionesTextArea;
    @FXML private CheckBox zona2ReproduccionCheckBox;
    @FXML private TextArea zona2ObservacionesTextArea;
    @FXML private CheckBox zona3ReproduccionCheckBox;
    @FXML private TextArea zona3ObservacionesTextArea;
    @FXML private Button guardarButton;

    private Viaje viaje;

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    @FXML
    public void guardarReporte() {
        if (viaje == null) {
            System.out.println("Error: viaje es null.");
            return;
        }

        ViajeDAO viajeDAO = new ViajeDAO();
        Viaje v1 = viajeDAO.obtenerViajeConRecursos(viaje.getVCod());
        System.out.println("Empleados texto: " + v1.getEmpleadosTexto());

        String obs1 = zona1ReproduccionCheckBox.isSelected() ? zona1ObservacionesTextArea.getText() : null;
        String obs2 = zona2ReproduccionCheckBox.isSelected() ? zona2ObservacionesTextArea.getText() : null;
        String obs3 = zona3ReproduccionCheckBox.isSelected() ? zona3ObservacionesTextArea.getText() : null;

        // Elegir dónde guardar el PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte del Viaje");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("reporte_viaje_" + v1.getVCod() + ".pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                generarPDFConPDFBox(file, v1, obs1, obs2, obs3);
                System.out.println("PDF generado exitosamente en: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ======================== PDF GENERATOR ========================
    private void generarPDFConPDFBox(File file, Viaje viaje, String obs1, String obs2, String obs3) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            float margin = 50;
            float yPosition = 750;
            float lineHeight = 20;

            // === Título ===
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(margin, yPosition);
            content.showText("REPORTE DE VIAJE");
            content.endText();
            yPosition -= lineHeight * 2;

            // === Fecha y tipo ===
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaStr = viaje.getFechaInicio() != null ? viaje.getFechaInicio().format(formatter) : "Sin fecha";

            content.setFont(PDType1Font.HELVETICA, 12);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Fecha: " + fechaStr);
            content.endText();

            yPosition -= lineHeight;

            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Tipo de viaje: " + (viaje.getTipo() != null ? viaje.getTipo() : "No especificado"));
            content.endText();

            yPosition -= lineHeight * 2;

            // === Empleados ===
            String empleadosTexto = viaje.getEmpleadosTexto(); // 🔹 Usamos el String del viaje
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Empleados:");
            content.endText();
            yPosition -= lineHeight;

            content.setFont(PDType1Font.HELVETICA, 12);

            if (empleadosTexto != null && !empleadosTexto.isBlank()) {
                // Si los nombres están separados por comas, los mostramos uno por línea
                String[] empleadosArray = empleadosTexto.split(",");

                for (String emp : empleadosArray) {
                    if (yPosition < 100) { // Crear nueva página si falta espacio
                        content.close();
                        PDPage newPage = new PDPage();
                        document.addPage(newPage);
                        content = new PDPageContentStream(document, newPage);
                        yPosition = 750;
                    }

                    content.beginText();
                    content.newLineAtOffset(margin + 20, yPosition);
                    content.showText("- " + emp.trim());
                    content.endText();
                    yPosition -= lineHeight;
                }
            } else {
                content.beginText();
                content.newLineAtOffset(margin + 20, yPosition);
                content.showText("No hay empleados registrados.");
                content.endText();
                yPosition -= lineHeight;
            }

            yPosition -= lineHeight;


            // === Observaciones ===
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Observaciones:");
            content.endText();
            yPosition -= lineHeight;

            content.setFont(PDType1Font.HELVETICA, 12);
            if (obs1 != null && !obs1.isBlank()) {
                content.beginText();
                content.newLineAtOffset(margin + 20, yPosition);
                content.showText("Zona 1: " + obs1);
                content.endText();
                yPosition -= lineHeight;
            }
            if (obs2 != null && !obs2.isBlank()) {
                content.beginText();
                content.newLineAtOffset(margin + 20, yPosition);
                content.showText("Zona 2: " + obs2);
                content.endText();
                yPosition -= lineHeight;
            }
            if (obs3 != null && !obs3.isBlank()) {
                content.beginText();
                content.newLineAtOffset(margin + 20, yPosition);
                content.showText("Zona 3: " + obs3);
                content.endText();
                yPosition -= lineHeight;
            }

            if ((obs1 == null || obs1.isBlank()) &&
                    (obs2 == null || obs2.isBlank()) &&
                    (obs3 == null || obs3.isBlank())) {
                content.beginText();
                content.newLineAtOffset(margin + 20, yPosition);
                content.showText("No se registraron observaciones.");
                content.endText();
            }

            content.close();
            document.save(file);
        }
    }
}
