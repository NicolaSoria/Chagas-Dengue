package proyect.chagas_dengue.interfaz;

import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import proyect.chagas_dengue.dao.ViajeDAO;
import proyect.chagas_dengue.modelos.Viaje;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class GenerarController {

    public void generarReporte(Viaje viaje) {
        if (viaje == null) {
            mostrarAlerta("Error", "El viaje es nulo, no se puede generar el informe.", Alert.AlertType.ERROR);
            return;
        }

        // Obtener datos completos del viaje (con tareas incluidas)
        ViajeDAO viajeDAO = new ViajeDAO();
        Viaje v1 = viajeDAO.obtenerViajeConRecursosYTareas(viaje.getVCod());

        // Elegir dónde guardar el PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte del Viaje");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("reporte_viaje_" + v1.getVCod() + ".pdf");

        File archivoDestino = fileChooser.showSaveDialog(null);
        if (archivoDestino == null) {
            return; // Usuario canceló
        }

        try {
            generarPDFConPDFBox(archivoDestino, v1);
            mostrarAlerta("Éxito", "PDF generado exitosamente en:\n" + archivoDestino.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo generar el PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ============================================
    //        GENERADOR DE PDF (PDFBOX)
    // ============================================
    private void generarPDFConPDFBox(File file, Viaje viaje) throws IOException {
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
            String fechaStr = viaje.getFechaInicio() != null
                    ? viaje.getFechaInicio().format(formatter)
                    : "Sin fecha";

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
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Empleados:");
            content.endText();
            yPosition -= lineHeight;

            content.setFont(PDType1Font.HELVETICA, 12);
            yPosition = agregarListaTexto(content, document, viaje.getEmpleadosTexto(), margin + 20, yPosition, lineHeight);
            yPosition -= lineHeight;

            // === Zonas ===
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Zonas visitadas:");
            content.endText();
            yPosition -= lineHeight;

            content.setFont(PDType1Font.HELVETICA, 12);
            yPosition = agregarListaTexto(content, document, viaje.getDestinosTexto(), margin + 20, yPosition, lineHeight);
            yPosition -= lineHeight;

            // === Camionetas ===
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Camionetas utilizadas:");
            content.endText();
            yPosition -= lineHeight;

            content.setFont(PDType1Font.HELVETICA, 12);
            yPosition = agregarListaTexto(content, document, viaje.getCamionetasTexto(), margin + 20, yPosition, lineHeight);
            yPosition -= lineHeight;

            // === Productos ===
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Productos utilizados:");
            content.endText();
            yPosition -= lineHeight;

            content.setFont(PDType1Font.HELVETICA, 12);
            yPosition = agregarListaTexto(content, document, viaje.getProductosTexto(), margin + 20, yPosition, lineHeight);
            yPosition -= lineHeight;

            // === Tareas (nuevo bloque) ===
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Tareas realizadas:");
            content.endText();
            yPosition -= lineHeight;

            content.setFont(PDType1Font.HELVETICA, 12);
            yPosition = agregarListaTexto(content, document, viaje.getRegistrosTexto(), margin + 20, yPosition, lineHeight);

            content.close();
            document.save(file);
        }
    }

    // ============================================
    //          FUNCIONES AUXILIARES
    // ============================================
    private float agregarListaTexto(PDPageContentStream content, PDDocument document,
                                    String texto, float x, float y, float lineHeight) throws IOException {
        if (texto == null || texto.isBlank()) {
            content.beginText();
            content.newLineAtOffset(x, y);
            content.showText("No hay datos registrados.");
            content.endText();
            return y - lineHeight;
        }

        String[] elementos = texto.split(",");
        for (String elemento : elementos) {
            if (y < 100) { // Crear nueva página si no hay espacio
                content.close();
                PDPage newPage = new PDPage();
                document.addPage(newPage);
                content = new PDPageContentStream(document, newPage);
                y = 750;
            }

            content.beginText();
            content.newLineAtOffset(x, y);
            content.showText("- " + elemento.trim());
            content.endText();
            y -= lineHeight;
        }
        return y;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
