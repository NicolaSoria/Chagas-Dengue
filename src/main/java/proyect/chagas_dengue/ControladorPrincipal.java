package proyect.chagas_dengue;


import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javafx.stage.Modality;
import javafx.stage.Stage;
import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.dao.*;
import proyect.chagas_dengue.interfaz.ConsultarController;
import proyect.chagas_dengue.interfaz.GenerarController;
import proyect.chagas_dengue.interfaz.PlanificarViajeController;
import proyect.chagas_dengue.interfaz.SeleccionarController;
import proyect.chagas_dengue.modelos.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ControladorPrincipal {

    @FXML private ChoiceBox<Zona> chBoxDestPnDestPrepViaje2;
    @FXML private ChoiceBox<Zona> chBoxDestPnDestPrepViaje3;
    @FXML private RadioButton radbtnDiaPaneDestPrepViaje;
    @FXML private RadioButton radbtnBarrPaneDestPrepViaje;
    @FXML private ToggleGroup tipoViajeOpcs;
    @FXML private AnchorPane anchorElementos;
    @FXML private ScrollPane scrPaneElmsPrepViaje;
    @FXML private Tab tabMenu;
    @FXML private TabPane mainTabPane;
    @FXML private Tab tabDestPrepViaje;
    @FXML private Tab tabElmsPrepViaje;
    @FXML private ChoiceBox<Zona> chBoxDestPnDestPrepViaje;
    @FXML private ChoiceBox<Tarea> chBoxDest1Tr1;
    @FXML private ChoiceBox<Tarea> chBoxDest1Tr2;
    @FXML private ChoiceBox<Tarea> chBoxDest2Tr1;
    @FXML private ChoiceBox<Tarea> chBoxDest2Tr2;
    @FXML private ChoiceBox<Tarea> chBoxDest3Tr1;
    @FXML private ChoiceBox<Tarea> chBoxDest3Tr2;
    @FXML private DatePicker datePnDestPrepViaje;
    @FXML private TabPane tabPanePrepViaje;
    @FXML private VBox listaContainer;
    @FXML private Label lblMensajePDF;
    @FXML private Tab tabFin;
    @FXML private VBox listaFinalizar;
    //@FXML private ListView<Tarea> tablaTareas;
    @FXML private Button btnCancelar;
    @FXML private Tab tabPanificar;
    @FXML private Tab tabConsultar;
    @FXML private ConsultarController consultarController;

    //este contador se usar para hacer aparecer el boton de cancelar
    private  int cancelarBtnCont = 0;

   // private ObservableList<Camioneta> listaCamionetas = FXCollections.observableArrayList();

    // private ObservableList<Camioneta> listaCamionetas = FXCollections.observableArrayList();

    private Map<String, Integer> objetos = new HashMap<>();

    @FXML public void initialize(){
        tabFin.setOnSelectionChanged(event -> {
            if (tabFin.isSelected()) {
                ontabFinalizar();
            }
        });
        // Escuchar cambios de pestaña boton cancelar
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabPanificar) {
                btnCancelar.setVisible(true);
            } else {
                btnCancelar.setVisible(false);
            }
        });

        // Ocultar por defecto al iniciar
        btnCancelar.setVisible(false);
        actualizarLista();
        mostrarPaneIncio();
        configurarColumnas();
//        cargarDatos();
        cargarZonas();
        cargarTareas2();
        //cargarTareas();
        //configurarEvents(chBoxDestPnDestPrepViaje,tablaTareas);
        //configurarEvents(chBoxDestPnDestPrepViaje2,tablaTareas);
        //configurarEvents(chBoxDestPnDestPrepViaje3, tablaTareas);
        configurarTareasPorDestino(chBoxDestPnDestPrepViaje, chBoxDest1Tr1, chBoxDest1Tr2);
        configurarTareasPorDestino(chBoxDestPnDestPrepViaje2, chBoxDest2Tr1, chBoxDest2Tr2);
        configurarTareasPorDestino(chBoxDestPnDestPrepViaje3, chBoxDest3Tr1, chBoxDest3Tr2);

        fechasCont();
        // Asignar la lista observable a la tabla
        //tableCamionetas.setItems(listaCamionetas);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Consultar.fxml"));
            Parent contenidoConsultar = loader.load();

            consultarController = loader.getController();

            // Agregar el contenido al tab
            tabConsultar.setContent(contenidoConsultar);

            tabConsultar.setOnSelectionChanged(event -> {
                if (tabConsultar.isSelected()) {
                    consultarController.actualizarListaViajes();
                }
            });



        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar Consultar.fxml");
        }

        tablaDets.setPlaceholder(new Label("Viaje no seleccionado"));


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

    @FXML private void cargarZonas(){
        ZonaDAO zonaDAO = new ZonaDAO();
        List<Zona> zonas = zonaDAO.listarTodos();
        chBoxDestPnDestPrepViaje.getItems().setAll(zonas);
        chBoxDestPnDestPrepViaje2.getItems().setAll(zonas);
        chBoxDestPnDestPrepViaje3.getItems().setAll(zonas);
    }

    @FXML private void cargarTareas2(){
        TareaDAO tareaDAO = new TareaDAO();
        List<Tarea> tareas = tareaDAO.listarTodos();
        chBoxDest1Tr1.getItems().setAll(tareas);
        chBoxDest1Tr2.getItems().setAll(tareas);
        chBoxDest2Tr1.getItems().setAll(tareas);
        chBoxDest2Tr2.getItems().setAll(tareas);
        chBoxDest3Tr1.getItems().setAll(tareas);
        chBoxDest3Tr2.getItems().setAll(tareas);
    }

    /*@FXML private void cargarTareas(){
        TareaDAO tareaDAO = new TareaDAO();
        List<Tarea> tareas = tareaDAO.listarTodos();
        ObservableList<Tarea> tareasObs = FXCollections.observableArrayList(tareas);
        tablaTareas.setItems(tareasObs);
        tablaTareas.setDisable(true);
        tablaTareas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tablaTareas.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tarea>) change -> {
            if(tablaTareas.getSelectionModel().getSelectedItems().size() > 3) {
                Tarea last = tablaTareas.getSelectionModel().getSelectedItems().get(tablaTareas.getSelectionModel().getSelectedItems().size() - 1);
                tablaTareas.getSelectionModel().clearSelection(tablaTareas.getItems().indexOf(last));
                mostrarAlerta("Advertencia","Solo se puede seleccionar 3 tareas.");
            }
        });
    }*/

    /*private void configurarEvents(ChoiceBox<Zona> dest, ListView<Tarea> list) {
        dest.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                list.setDisable(false);
                list.getSelectionModel().clearSelection();
            }
        });
    }*/

    private void configurarTareasPorDestino(ChoiceBox<Zona> destino, ChoiceBox<Tarea> t1, ChoiceBox<Tarea> t2) {
        destino.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean habilitado = newVal != null;
            t1.setDisable(!habilitado);
            t2.setDisable(!habilitado);

            if (!habilitado) {
                t1.setValue(null);
                t2.setValue(null);
            }
        });
    }


    @FXML private void clearZonas(){
        chBoxDestPnDestPrepViaje.setValue(null);
        chBoxDestPnDestPrepViaje2.setValue(null);
        chBoxDestPnDestPrepViaje3.setValue(null);
        chBoxDest1Tr1.setValue(null);
        chBoxDest1Tr2.setValue(null);
        chBoxDest2Tr1.setValue(null);
        chBoxDest2Tr2.setValue(null);
        chBoxDest3Tr1.setValue(null);
        chBoxDest3Tr1.setValue(null);
        tipoViajeOpcs.selectToggle(null);
        datePnDestPrepViaje.setValue(null);

        if (tableEmpleados != null) tableEmpleados.getItems().clear();
        if (tableQuimicos != null) tableQuimicos.getItems().clear();
        if (tableElementos != null) tableElementos.getItems().clear();
        if (tableCamionetas != null) tableCamionetas.getItems().clear();
        if (tableMaquinas != null) tableMaquinas.getItems().clear();

        chBoxDestPnDestPrepViaje.setDisable(false);
        chBoxDestPnDestPrepViaje2.setDisable(false);
        chBoxDestPnDestPrepViaje3.setDisable(false);

        chBoxDest1Tr1.setDisable(false);
        chBoxDest1Tr2.setDisable(false);
        chBoxDest2Tr1.setDisable(false);
        chBoxDest2Tr2.setDisable(false);
        chBoxDest3Tr1.setDisable(false);
        chBoxDest3Tr2.setDisable(false);

        radbtnBarrPaneDestPrepViaje.setDisable(false);
        radbtnDiaPaneDestPrepViaje.setDisable(false);
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

    private void mostrarAlerta(String titulo, String msj){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }

    @FXML
    private void ingresarViaje() {
        LocalDate fechainicio = datePnDestPrepViaje.getValue();

        if (fechainicio == null) {
            mostrarAlerta("Error", "Fecha no seleccionada.");
            return;
        }

        Viaje.Tipo tipo;
        if (radbtnDiaPaneDestPrepViaje.isSelected()) {
            tipo = Viaje.Tipo.Diario;
        } else if (radbtnBarrPaneDestPrepViaje.isSelected()) {
            tipo = Viaje.Tipo.Barrido;
        } else {
            mostrarAlerta("Error", "Debe seleccionar un tipo de viaje.");
            return;
        }

        // --- ZONAS ---
        Zona dest1 = chBoxDestPnDestPrepViaje.getValue();
        Zona dest2 = chBoxDestPnDestPrepViaje2.getValue();
        Zona dest3 = chBoxDestPnDestPrepViaje3.getValue();

        if (dest1 == null && dest2 == null && dest3 == null) {
            mostrarAlerta("Error", "Debe seleccionar al menos un destino.");
            return;
        }

        Set<Integer> idsDests = new HashSet<>();
        if (dest1 != null) idsDests.add(dest1.getZCod());
        if (dest2 != null && !idsDests.add(dest2.getZCod())) {
            mostrarAlerta("Error", "Destino repetido en selección.");
            return;
        }
        if (dest3 != null && !idsDests.add(dest3.getZCod())) {
            mostrarAlerta("Error", "Destino repetido en selección.");
            return;
        }

        Tarea t1 = chBoxDest1Tr1.getValue();
        Tarea t2 = chBoxDest1Tr2.getValue();
        Tarea t3 = chBoxDest2Tr1.getValue();
        Tarea t4 = chBoxDest2Tr2.getValue();
        Tarea t5 = chBoxDest3Tr1.getValue();
        Tarea t6 = chBoxDest3Tr2.getValue();

        if(dest1 != null && t1 == null && t2 == null) {
            mostrarAlerta("Error","Debe seleccionar al menos una tarea para el destino 1.");
        }

        if(dest2 != null && t3 == null && t4 == null) {
            mostrarAlerta("Error","Debe seleccionar al menos una tarea para el destino 2.");
        }

        if(dest3 != null && t5 == null && t6 == null) {
            mostrarAlerta("Error","Debe seleccionar al menos una tarea para el destino 3.");
        }



        ObservableList<Camioneta> camionetas = tableCamionetas.getItems();
        ObservableList<Maquina> maquinas = tableMaquinas.getItems();
        ObservableList<Empleado> empleados = tableEmpleados.getItems();
        ObservableList<ProductoQuimico> productos = tableQuimicos.getItems();
        ObservableList<ElementoDeSeguridad> elementos = tableElementos.getItems();

        if (camionetas.isEmpty() || maquinas.isEmpty() || empleados.isEmpty() || productos.isEmpty() || elementos.isEmpty()) {
            mostrarAlerta("Error", "Una de las listas de recursos está vacía.");
            return;
        }

        // --- CREAR VIAJE ---
        Viaje nuevo = new Viaje();
        nuevo.setFechaInicio(fechainicio);
        nuevo.setTipo(tipo);
        nuevo.setEstado(Viaje.Estado.Pendiente);
        nuevo.setCamionetas(camionetas);
        nuevo.setMaquinas(maquinas);
        nuevo.setEmpleados(empleados);

        // Convertir zonas seleccionadas a lista de zonas
        List<Zona> zonas = new ArrayList<>();
        if (dest1 != null) zonas.add(dest1);
        if (dest2 != null) zonas.add(dest2);
        if (dest3 != null) zonas.add(dest3);
        nuevo.setZonas(zonas);

        // --- CREAR DETALLES CON CANTIDADES FIJAS ---
        List<Detalle> detalles = new ArrayList<>();
        for (ProductoQuimico p : productos) {
            Stock stock = p.getStock();
            int cantidadTomada = 0;

            switch (p.getPCod()) {
                case 1 -> cantidadTomada = 2; // producto P
                case 2 -> cantidadTomada = 3; // producto C
                case 3 -> cantidadTomada = 1; // producto I
                default -> cantidadTomada = 1; // por si hay otros
            }

            Detalle detalle = new Detalle();
            detalle.setViaje(nuevo);
            detalle.setStock(stock);
            detalle.setCantTomada(cantidadTomada);
            detalle.setCantUsada(0); // todavía no se uso nada
            detalles.add(detalle);
        }
        nuevo.setDetalles(detalles);

        List<Registro> registros = new ArrayList<>();
        if (dest1 != null) {
            if (t1 != null) registros.add(new Registro(nuevo, dest1, t1));
            if (t2 != null) registros.add(new Registro(nuevo, dest1, t2));
        }
        if (dest2 != null) {
            if (t3 != null) registros.add(new Registro(nuevo, dest2, t3));
            if (t4 != null) registros.add(new Registro(nuevo, dest2, t4));
        }
        if (dest3 != null) {
            if (t5 != null) registros.add(new Registro(nuevo, dest3, t5));
            if (t6 != null) registros.add(new Registro(nuevo, dest3, t6));
        }

        nuevo.setRegistros(registros);

        // --- GUARDAR VIAJE ---
        ViajeDAO dao = new ViajeDAO();
        dao.insertar(nuevo);

        clearZonas();
        //consultarController.recargarViajes();
        mostrarAlerta("Éxito", "Viaje ingresado correctamente.");
    }


    @FXML
    private void actualizarLista() {
        PlanificarViajeController objLista = new PlanificarViajeController();
        objLista.actualizarListaVisual(listaContainer,tableEmpleados.getItems(), tableQuimicos.getItems(), tableElementos != null ? tableElementos.getItems() : null, tableCamionetas.getItems(), tableMaquinas.getItems());
        lblMensajePDF.setText("Lista actualizada correctamente");
    }


    //##################### PDF #######################
    private void actualizarMapaObjetos() {
        objetos.clear(); // Limpiar el mapa existente

        //PRODUCTOS QUÍMICOS
        ObservableList<ProductoQuimico> todosLosProductos = tableQuimicos.getItems();
        List<ProductoQuimico> listaProductos = new ArrayList<>(todosLosProductos);

        for (ProductoQuimico producto : listaProductos) {
            String clave = "PRODUCTO: " + producto.getNombre();
            objetos.put(clave, objetos.getOrDefault(clave, 0) + 1);
        }

        //ELEMENTOS DE SEGURIDAD
        if (tableElementos != null) {
            ObservableList<ElementoDeSeguridad> todosElementosSeguridad = tableElementos.getItems();
            for (ElementoDeSeguridad elemento : todosElementosSeguridad) {
                String clave = "SEGURIDAD: " + elemento.getDescripcion();
                objetos.put(clave, objetos.getOrDefault(clave, 0) + 1);
            }
        }

        //AGREGAR CAMIONETAS
        if (tableCamionetas != null) {
            ObservableList<Camioneta> todasCamionetas = tableCamionetas.getItems();
            for (Camioneta camioneta : todasCamionetas) {
                String clave = "CAMIONETA: " + camioneta.getPatente();
                objetos.put(clave, objetos.getOrDefault(clave, 0) + 1);
            }
        }

        //AGREGAR MÁQUINAS
        if (tableMaquinas != null) {
            ObservableList<Maquina> todasMaquinas = tableMaquinas.getItems();
            for (Maquina maquina : todasMaquinas) {
                String clave = "MÁQUINA: " + maquina.getTipo() + " - " + maquina.getModelo();
                objetos.put(clave, objetos.getOrDefault(clave, 0) + 1);
            }
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
                // Asegurarse de que el mapa objetos esté actualizado
                actualizarMapaObjetos();
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

            try {
                float margin = 50;
                float yPosition = 750;
                float lineHeight = 20;

                // ====== Título ======
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("LISTA DE ELEMENTOS DEL VIAJE");
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

                // Línea bajo encabezados
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
                        contentStream.close();

                        // Crear nueva página
                        PDPage newPage = new PDPage();
                        document.addPage(newPage);
                        contentStream = new PDPageContentStream(document, newPage);
                        contentStream.setFont(PDType1Font.HELVETICA, 12);

                        yPosition = 750;

                        // Volver a poner encabezados en nueva página
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

                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                    }

                    // Número
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(contador + ".");
                    contentStream.endText();

                    // Nombre del elemento (recortar si es muy largo)
                    String elemento = entry.getKey();
                    if (elemento.length() > 40) {
                        elemento = elemento.substring(0, 40) + "...";
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 30, yPosition);
                    contentStream.showText(elemento);
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

            } finally {
                contentStream.close();
            }

            // Guardar documento
            document.save(file);
        }
    }


//    @FXML
//    private void mostrarSeleccion() {
//        Toggle seleccionado = tipoViajeOpcs.getSelectedToggle();
//
//
//        if (seleccionado == radbtnDiaPaneDestPrepViaje) {
//            System.out.println("Seleccion: Opción Diario");
//        } else if (seleccionado == radbtnBarrPaneDestPrepViaje) {
//            System.out.println("Seleccion: Opción Barrido");
//        } else {
//            System.out.println("No hay ninguna opción seleccionada");
//        }
//    }

    private String obtenerTipoViajeSeleccionado() {
        Toggle seleccionado = tipoViajeOpcs.getSelectedToggle();

        if (seleccionado == radbtnDiaPaneDestPrepViaje) {
            return "Diario";
        } else if (seleccionado == radbtnBarrPaneDestPrepViaje) {
            return "Barrido";
        } else {
            return null; // o "Ninguno"
        }
    }

    @FXML
    private void onSiguiente(ActionEvent event) {

    }


    // ========== EMPLEADOS ==========
    @FXML private TableView<Empleado> tableEmpleados;
    @FXML private TableColumn<Empleado, Integer> colEmpleadoDNI;
    @FXML private TableColumn<Empleado, String> colEmpleadoNombre;
    @FXML private TableColumn<Empleado, Integer> colEmpleadoCod;
            ;

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

    // ========== ELEMENTOS DE SEGURIDAD ==========

    @FXML
    private TableView<ElementoDeSeguridad> tableElementos;

    @FXML
    private TableColumn<ElementoDeSeguridad, String> colDescripcion;

    @FXML
    private TableColumn<ElementoDeSeguridad, String> colEstado;

    private void configurarColumnas() {
        // Camionetas

        colCamionetaCodigo.setCellValueFactory(new PropertyValueFactory<>("CCod"));
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

        //Elementos de seguridad

        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
//        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        //Empleados

        colEmpleadoDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colEmpleadoCod.setCellValueFactory(new PropertyValueFactory<>("ECod"));
        colEmpleadoNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));

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
                    },
                    false

            );

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.initOwner(btnAñadirCamioneta.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();


            Object seleccionadoObj = controller.getSeleccionado();
            if (seleccionadoObj != null) {
                Camioneta seleccionado = (Camioneta) seleccionadoObj;



                boolean yaExiste = tableCamionetas.getItems().stream()
                        .anyMatch(p -> p.getCCod() == seleccionado.getCCod());

                if (yaExiste) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Camioneta duplicada");
                    alerta.setHeaderText(null);
                    alerta.setContentText("La camioneta con patente \"" + seleccionado.getPatente() + "\" ya fue agregada.");
                    alerta.showAndWait();
                    return;
                }


               tableCamionetas.getItems().add(seleccionado);
                System.out.println("Camioneta seleccionada: " + seleccionado.getPatente());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private Button btnAñadirProducto;

    @FXML
    private void onAñadirProducto() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<ProductoQuimico> productos = dao.listarTodos();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Seleccionar1.fxml"));
            Parent root = loader.load();

            SeleccionarController controller = loader.getController();
            controller.configurar(
                    "Seleccionar Producto",
                    productos,
                    p -> {
                        ProductoQuimico prod = (ProductoQuimico)p;
                        return prod.getNombre() ;
                    },
                    false
            );

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.initOwner(btnAñadirProducto.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Object seleccionadoObj = controller.getSeleccionado();
            if (seleccionadoObj != null) {
                ProductoQuimico seleccionado = (ProductoQuimico) seleccionadoObj;

                boolean yaExiste = tableQuimicos.getItems().stream()
                        .anyMatch(p -> p.getPCod() == seleccionado.getPCod());

                if (yaExiste) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Producto duplicado");
                    alerta.setHeaderText(null);
                    alerta.setContentText("El producto \"" + seleccionado.getNombre() + "\" ya fue agregado.");
                    alerta.showAndWait();
                    return;
                }

                tableQuimicos.getItems().add(seleccionado);
                System.out.println("Producto seleccionado: " + seleccionado.getNombre());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button btnAñadirMaquina;

    @FXML
    private void onAñadirMaquina() {
        try {
            MaquinaDAO dao = new MaquinaDAO();
            List<Maquina> maquinas = dao.listarTodos();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Seleccionar1.fxml"));
            Parent root = loader.load();

            SeleccionarController controller = loader.getController();
            controller.configurar(
                    "Seleccionar Máquina",
                    maquinas,
                    m -> {
                        Maquina maq = (Maquina)m;
                        return maq.getMarca() + " | Modelo: " + maq.getModelo();
                    },
                    false
            );

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.initOwner(btnAñadirMaquina.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Object seleccionadoObj = controller.getSeleccionado();
            if (seleccionadoObj != null) {
                Maquina seleccionado = (Maquina) seleccionadoObj;

                boolean yaExiste = tableMaquinas.getItems().stream()
                        .anyMatch(p -> p.getMCod() == seleccionado.getMCod());

                if (yaExiste) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Máquina duplicada");
                    alerta.setHeaderText(null);
                    alerta.setContentText("La máquina \"" + seleccionado.getMarca() + " " + seleccionado.getModelo() + "\" ya fue agregada.");
                    alerta.showAndWait();
                    return;
                }

                tableMaquinas.getItems().add(seleccionado);
                System.out.println("Máquina seleccionada: " + seleccionado.getMarca() + " " + seleccionado.getModelo());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button btnAñadirEmpleado;

    @FXML
    private void onAñadirEmpleados() {
        try {
            EmpleadoDAO dao = new EmpleadoDAO();
            List<Empleado> empleados = dao.listarTodos();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Seleccionar1.fxml"));
            Parent root = loader.load();

            SeleccionarController controller = loader.getController();
            controller.configurar(
                    "Seleccionar Empleado",
                    empleados,
                    e -> {
                        Empleado emp = (Empleado) e;
                        return emp.getNombre() + " (DNI: " + emp.getDni() + ")";
                    },
                    true
            );

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.initOwner(btnAñadirEmpleado.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Object seleccionadoObj = controller.getSeleccionado();
            if (seleccionadoObj != null) {
                Empleado seleccionado = (Empleado) seleccionadoObj;

                boolean yaExiste = tableEmpleados.getItems().stream()
                        .anyMatch(p -> p.getECod() == seleccionado.getECod());

                if (yaExiste) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Empleado duplicado");
                    alerta.setHeaderText(null);
                    alerta.setContentText("El empleado \"" + seleccionado.getNombre() + "\" ya fue agregado.");
                    alerta.showAndWait();
                    return;
                }

                tableEmpleados.getItems().add(seleccionado);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button btnGuardarElementos;

    @FXML
    private void onGuardar() {
        List<Object> listaGeneral = new ArrayList<>();

        listaGeneral.addAll(tableCamionetas.getItems());
        listaGeneral.addAll(tableQuimicos.getItems());
        listaGeneral.addAll(tableMaquinas.getItems());
        listaGeneral.addAll(tableElementos.getItems());
        tabPanePrepViaje.getSelectionModel().select(tabDestPrepViaje);

    }

//Borrar elementos de la tabla

    @FXML
    private Button btnEditar;

    private boolean modoEdicion = false;

    @FXML private Button btnEditarCamionetas;
    @FXML private Button btnEditarQuimicos;
    @FXML private Button btnEditarMaquinas;
    @FXML private Button btnEditarEmpleados;

    private boolean editandoCamionetas = false;
    private boolean editandoQuimicos = false;
    private boolean editandoMaquinas = false;
    private boolean editandoEmpleados = false;

    @FXML
    private void onEditarCamionetas() {
        editandoCamionetas = !editandoCamionetas;
        if (editandoCamionetas) {
            btnEditarCamionetas.setText("Cancelar");
            btnEditarCamionetas.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo
            agregarColumnaBorrar(tableCamionetas);
        } else {
            btnEditarCamionetas.setText("Editar");
            btnEditarCamionetas.setStyle("");
            quitarColumnaBorrar(tableCamionetas);
        }
    }

    @FXML
    private void onEditarQuimicos() {
        editandoQuimicos = !editandoQuimicos;
        if (editandoQuimicos) {
            btnEditarQuimicos.setText("Cancelar");
            btnEditarQuimicos.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo
            agregarColumnaBorrar(tableQuimicos);
        } else {
            btnEditarQuimicos.setText("Editar");
            btnEditarQuimicos.setStyle("");
            quitarColumnaBorrar(tableQuimicos);
        }
    }

    @FXML
    private void onEditarMaquinas() {
        editandoMaquinas = !editandoMaquinas;
        if (editandoMaquinas) {
            btnEditarMaquinas.setText("Cancelar");
            btnEditarMaquinas.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo
            agregarColumnaBorrar(tableMaquinas);
        } else {
            btnEditarMaquinas.setText("Editar");
            btnEditarMaquinas.setStyle("");
            quitarColumnaBorrar(tableMaquinas);
        }
    }

    @FXML
    private void onEditarEmpleados() {
        editandoEmpleados = !editandoEmpleados;
        if (editandoEmpleados) {
            btnEditarEmpleados.setText("Cancelar");
            btnEditarEmpleados.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // rojo
            agregarColumnaBorrar(tableEmpleados);
        } else {
            btnEditarEmpleados.setText("Editar");
            btnEditarEmpleados.setStyle("");
            quitarColumnaBorrar(tableEmpleados);
        }
    }


    private <T> void agregarColumnaBorrar(TableView<T> tabla) {
        // Evitar duplicar la columna si ya existe
        for (TableColumn<T, ?> col : tabla.getColumns()) {
            if ("Acciones".equals(col.getText())) {
                return;
            }
        }

        TableColumn<T, Void> colAccion = new TableColumn<>("Acciones");

        colAccion.setCellFactory(tc -> new TableCell<>() {
            private final Button btnBorrar = new Button("✖");

            {
                btnBorrar.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Eliminar este elemento?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES) {
                        T item = getTableView().getItems().get(getIndex());
                        getTableView().getItems().remove(item);
                        System.out.println("Eliminado: " + item);
                    }
                });

                btnBorrar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBorrar);
                }
            }
        });

        tabla.getColumns().add(colAccion);
    }

    private <T> void quitarColumnaBorrar(TableView<T> tabla) {
        tabla.getColumns().removeIf(col -> "Acciones".equals(col.getText()));
    }

//    @FXML
//    private void onCargarElementos() {
//        String tipoViaje = obtenerTipoViajeSeleccionado();
//        if (tipoViaje != null) {
//            cargarElementosSeguridad(tipoViaje);
//            System.out.println("Cargando elementos para viaje: " + tipoViaje);
//        } else {
//            System.out.println("⚠ No se ha seleccionado ningún tipo de viaje");
//        }
//    }



    public void onSiguiente(javafx.event.ActionEvent actionEvent) {
        // Obtener el tipo de viaje
        String tipoViaje = obtenerTipoViajeSeleccionado();
        Zona dest1 = chBoxDestPnDestPrepViaje.getValue();
        Zona dest2 = chBoxDestPnDestPrepViaje2.getValue();
        Zona dest3 = chBoxDestPnDestPrepViaje3.getValue();

        if (dest1 == null && dest2 == null && dest3 == null) {
            mostrarAlerta("Error", "Debe seleccionar al menos un destino.");
            return;
        }

        Set<Integer> idsDests = new HashSet<>();
        if (dest1 != null) {
            idsDests.add(dest1.getZCod());
        }if (dest2 != null) {
            if (!idsDests.add(dest2.getZCod())){
                mostrarAlerta("Error", "Destino repetido en seleccion.");
                return;
            }
        }
        if (dest3 != null) {
            if (!idsDests.add(dest3.getZCod())) {
                mostrarAlerta("Error", "Destino repetido en seleccion.");
                return;
            }
        }

        if (tipoViaje != null) {
            try {
                Connection conn = ConexionBD.getConexion();
                ElementoSeguridadDAO dao = new ElementoSeguridadDAO(conn);
                List<ElementoDeSeguridad> lista = dao.listarPorTipoViaje(tipoViaje);
                tableElementos.getItems().setAll(lista);
                System.out.println("Se cargaron los elementos de seguridad para el viaje " + tipoViaje);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mostrarAlerta("Error","No se ha seleccionado tipo de viaje");
            return;
        }

        chBoxDestPnDestPrepViaje.setDisable(true);
        chBoxDestPnDestPrepViaje2.setDisable(true);
        chBoxDestPnDestPrepViaje3.setDisable(true);

        radbtnBarrPaneDestPrepViaje.setDisable(true);
        radbtnDiaPaneDestPrepViaje.setDisable(true);

        mostrarPanelElementos();
    }


    public void onCancelar(javafx.event.ActionEvent actionEvent) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar planificación");
        confirm.setHeaderText("¿Desea cancelar la planificación del viaje?");
        confirm.setContentText("Se descartarán los cambios no guardados.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    tabPanePrepViaje.getSelectionModel().select(tabDestPrepViaje);
                    mostrarPaneIncio();
                } catch (Exception ex) {
                    System.out.println("No entro al menu");
                }

                // Limpiar Destino
                if (datePnDestPrepViaje != null) datePnDestPrepViaje.setValue(null);
                if (radbtnDiaPaneDestPrepViaje.isSelected() || radbtnBarrPaneDestPrepViaje.isSelected()) tipoViajeOpcs.selectToggle(null);
                if (chBoxDestPnDestPrepViaje != null) chBoxDestPnDestPrepViaje.getSelectionModel().clearSelection();
                if (chBoxDestPnDestPrepViaje2 != null) chBoxDestPnDestPrepViaje2.getSelectionModel().clearSelection();
                if (chBoxDestPnDestPrepViaje3 != null) chBoxDestPnDestPrepViaje3.getSelectionModel().clearSelection();

                // Limpiar tablas recursos
                if (tableQuimicos != null) tableQuimicos.getItems().clear();
                if (tableElementos != null) tableElementos.getItems().clear();
                if (tableCamionetas != null) tableCamionetas.getItems().clear();
                if (tableMaquinas != null) tableMaquinas.getItems().clear();
                if (tableEmpleados != null) tableEmpleados.getItems().clear();

                chBoxDestPnDestPrepViaje.setDisable(false);
                chBoxDestPnDestPrepViaje2.setDisable(false);
                chBoxDestPnDestPrepViaje3.setDisable(false);

                radbtnBarrPaneDestPrepViaje.setDisable(false);
                radbtnDiaPaneDestPrepViaje.setDisable(false);

                // Mensaje
                actualizarLista();
                if (lblMensajePDF != null) {
                    lblMensajePDF.setText("");
                }
            }
        });
    }

    @FXML TableView<Detalle> tablaDets = new TableView<>();

    private void cargarDetalles(Viaje viaje) {
        ViajeDAO dao = new ViajeDAO();
        List<Detalle> detalles = dao.listarDetPorViaje(viaje.getVCod());
        tablaDets.setItems(FXCollections.observableArrayList(detalles));
        tablaDets.setEditable(true);

        TableColumn<Detalle, String> colProd = new TableColumn<>("Producto");
        colProd.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStock().getProducto().getNombre()));

        TableColumn<Detalle, Integer> colCantTomada = new TableColumn<>("Cantidad tomada");
        colCantTomada.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCantTomada()).asObject());

        TableColumn<Detalle, Integer> colCantUsada = new TableColumn<>("Cantidad usada");
        colCantUsada.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCantUsada()).asObject());

        colCantUsada.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colCantUsada.setOnEditCommit(event -> {
            Detalle detalle = event.getRowValue();
            int valor = event.getNewValue();
            if (valor >= 0 && valor <= detalle.getCantTomada()){
                detalle.setCantUsada(valor);
            } else {
                detalle.setCantUsada(event.getOldValue());
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Valor invalido");
                alerta.setHeaderText(null);
                alerta.setContentText("Cantidad negativa o mayor a tomada.");
                alerta.showAndWait();
                tablaDets.refresh();
            }
        });

        tablaDets.getColumns().setAll(colProd,colCantTomada,colCantUsada);
    }

            private Viaje viajeSeleccionado;
            private Label labelSeleccionado = null;

    private void ontabFinalizar() {
        FinalizarViajeController objFinalizar = new FinalizarViajeController();
        List<Viaje> lista = objFinalizar.listarViajes();

        listaFinalizar.getChildren().clear();

        if (lista == null || lista.isEmpty()) {
            Label vacio = new Label("No hay viajes para finalizar.");
            vacio.getStyleClass().add("label-vacio");
            listaFinalizar.getChildren().add(vacio);
            return;
        }

        for (Viaje v : lista) {
            Label label = new Label(
                    "Viaje #" + v.getVCod() +
                            " | Tipo: " + v.getTipo() +
                            " | Fecha: " + v.getFechaInicio() +
                            " | Estado: " + v.getEstado()
            );
            label.getStyleClass().add("label-viaje");

            //Funcion selecciona un viaje...
            label.setOnMouseClicked(e -> {
                System.out.println("Seleccionaste el viaje: " + v.getVCod());

                // Quitar estilo seleccionado
                if (labelSeleccionado != null)
                    labelSeleccionado.getStyleClass().remove("label-viaje-seleccionado");

                // Aplicar nuevo estilo ;) carita fachera facherita
                label.getStyleClass().add("label-viaje-seleccionado");

                labelSeleccionado = label;
                viajeSeleccionado = v;
                cargarDetalles(viajeSeleccionado);
            });

            listaFinalizar.getChildren().add(label);
        }
    }

    @FXML private void onFinalizar(){
        if (viajeSeleccionado == null) {
            mostrarAlerta("Advertencia","Sin seleccion: Debe seleccionar un viaje");
            return;
        }

        for (Detalle d : tablaDets.getItems()){
            if (d.getCantUsada() > d.getCantTomada()) {
                mostrarAlerta("Error","Las cantidades usadas no pueden ser mayores a las cantidades tomadas");
                return;
            }
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar finalizacion");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea finalizar el viaje #:" + viajeSeleccionado.getVCod());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK)
            return;

        ViajeDAO dao = new ViajeDAO();

        for (Detalle d : tablaDets.getItems()) {
            int dif = d.getCantTomada() - d.getCantUsada();
            Stock stock = d.getStock();
            stock.setTotal(stock.getTotal() + dif);
            dao.actualizarCantU(viajeSeleccionado,d);
            dao.actualizarStock(viajeSeleccionado,stock);
        }

        viajeSeleccionado.setEstado(Viaje.Estado.Finalizado);

        dao.actualizar(viajeSeleccionado);

        tablaDets.getItems().clear();

        mostrarAlerta("Exito","El viaje ha sido finalizado con exito.");

        ontabFinalizar();

    }

    @FXML
    private void onGenerarClick() {
        if (viajeSeleccionado == null) {
            mostrarAlerta("Error", "Por favor seleccione un viaje primero.");
            return;
        }
        ViajeDAO viajeDAO = new ViajeDAO();
        Viaje viaje = viajeDAO.obtenerViajeConRecursosYTareas(viajeSeleccionado.getVCod());
        if (viaje != null) {
            System.out.println("Tareas del viaje: " + viaje.getRegistrosTexto());
        }

        GenerarController generar = new GenerarController();
        generar.generarReporte(viajeSeleccionado);
    }
}