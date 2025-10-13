package proyect.chagas_dengue;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javafx.stage.Modality;
import javafx.stage.Stage;
import proyect.chagas_dengue.dao.CamionetaDAO;
import proyect.chagas_dengue.dao.MaquinaDAO;
import proyect.chagas_dengue.dao.ProductoDAO;
import proyect.chagas_dengue.dao.ZonaDAO;
import proyect.chagas_dengue.interfaz.SeleccionarController;
import proyect.chagas_dengue.modelos.Camioneta;
import proyect.chagas_dengue.modelos.Maquina;
import proyect.chagas_dengue.modelos.ProductoQuimico;
import proyect.chagas_dengue.modelos.Zona;

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
    @FXML private ChoiceBox<String> chBoxDestPnDestPrepViaje;
    @FXML private DatePicker datePnDestPrepViaje;
    @FXML private Button btnSigPnDestPrepViaje;
    @FXML private TabPane tabPanePrepViaje;
    @FXML private VBox listaContainer;
    @FXML private Label lblMensajePDF;

    private ZonaDAO zonaDAO = new ZonaDAO();
    private ObservableList<Camioneta> listaCamionetas = FXCollections.observableArrayList();

    // Datos de ejemplo para la lista
    private Map<String, Integer> objetos = new HashMap<>();

    @FXML public void initialize(){
        inicializarDatos();
        actualizarListaVisual();
        mostrarPaneIncio();
        configurarColumnas();
        cargarDatos();
        cargarZonasChoiceBox();
        fechasCont();
    }

    private void fechasCont(){
        datePnDestPrepViaje.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date,empty);
                if (date.isBefore(LocalDate.now())){
                    setDisable(true);
                    setStyle("-fx-background-color: #ffcccc;");
                }
            }
        });
    }

    @FXML private void cargarZonasChoiceBox(){
        List<Zona> zonas = zonaDAO.listarTodos();

        for(Zona z : zonas){
            chBoxDestPnDestPrepViaje.getItems().add(z.getNombre());
        }
    }

    @FXML private void clearZonasChoiceBox(){
        chBoxDestPnDestPrepViaje.getItems().clear();
        chBoxDestPnDestPrepViaje.setValue(null);
        //configurar para boton Ingresar viaje onAction="#clearZonasChoiceBox"
        //llamar nuevamente cargarZonasChoiceBox() tras actualizacion

        // Asignar la lista observable a la tabla
        tableCamionetas.setItems(listaCamionetas);

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
        // esto se debe de generar dinamicamente segun los elementos asignados
        objetos.put("Camioneta", 1);
        objetos.put("Cipermetrina", 2);
        objetos.put("Permetrina", 2);
        objetos.put("Irritante", 2);
        objetos.put("Maquina Manual", 1);
        objetos.put("Maquina Motomocjila", 1);
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

    // ========== CAMIONETAS ==========
    @FXML private TableView<Camioneta> tableCamionetas;
    @FXML private TableColumn<Camioneta, Integer> colCamionetaCodigo;
    @FXML private TableColumn<Camioneta, String> colCamionetaPatente;
    @FXML private TableColumn<Camioneta, String> colCamionetaEstado;

    // ========== PRODUCTOS ==========
    @FXML private TableView<ProductoQuimico> tableQuimicos;
    @FXML private TableColumn<ProductoQuimico, Integer> colProdCodigo;
    @FXML private TableColumn<ProductoQuimico, String> colProdNombre;
    @FXML private TableColumn<ProductoQuimico, String> colProdMarca;
    @FXML private TableColumn<ProductoQuimico, Double> colProdPresentacion;

    // ========== MAQUINAS ==========
    @FXML private TableView<Maquina> tableMaquinas;
    @FXML private TableColumn<Maquina, Integer> colMaqCodigo;
    @FXML private TableColumn<Maquina, String> colMaqMarca;
    @FXML private TableColumn<Maquina, String> colMaqModelo;
    @FXML private TableColumn<Maquina, Maquina.TipoM> colMaqTipo;

    private void configurarColumnas() {
        // Camionetas

        colCamionetaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCamionetaPatente.setCellValueFactory(new PropertyValueFactory<>("patente"));
        colCamionetaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Productos
        colProdCodigo.setCellValueFactory(new PropertyValueFactory<>("PCod"));
        colProdNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colProdMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colProdPresentacion.setCellValueFactory(new PropertyValueFactory<>("presentacion"));

        // Máquinas
        colMaqCodigo.setCellValueFactory(new PropertyValueFactory<>("MCod"));
        colMaqMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colMaqModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colMaqTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
    }

    private void cargarDatos() {
        try {
            tableCamionetas.getItems().setAll(new CamionetaDAO().listarDisponibles());
            tableQuimicos.getItems().setAll(new ProductoDAO().listarTodos());
            tableMaquinas.getItems().setAll(new MaquinaDAO().listarTodos());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private Button btnAñadirCamioneta;

    @FXML
    private void onAñadirCamioneta() {


        try {
            CamionetaDAO dao = new CamionetaDAO();
            List<Camioneta> camionetas = dao.listarDisponibles();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Seleccionar1.fxml"));
            Parent root = loader.load();

            SeleccionarController controller = loader.getController();
            controller.configurar(
                    "Seleccionar Camioneta",
                    camionetas,
                    c -> {
                        Camioneta cam = (Camioneta)c;
                        return "Patente: " + cam.getPatente() + " | Estado: " + cam.getEstado();
                    }
            );

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.initOwner(btnAñadirCamioneta.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Obtener el objeto real
            Object seleccionadoObj = controller.getSeleccionado();
            if (seleccionadoObj != null) {
                Camioneta seleccionado = (Camioneta) seleccionadoObj;

                listaCamionetas.add(seleccionado); // agregar a la lista observable

//                tableCamionetas.getItems().add(seleccionado);
                System.out.println("Camioneta seleccionada: " + seleccionado.getPatente());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

