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
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javafx.stage.Modality;
import javafx.stage.Stage;
import proyect.chagas_dengue.conexion.ConexionBD;
import proyect.chagas_dengue.dao.*;
import proyect.chagas_dengue.interfaz.PlanificarViajeController;
import proyect.chagas_dengue.interfaz.SeleccionarController;
import proyect.chagas_dengue.modelos.*;

import java.awt.event.ActionEvent;

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
    @FXML private DatePicker datePnDestPrepViaje;
    @FXML private TabPane tabPanePrepViaje;
    @FXML private VBox listaContainer;
    @FXML private Label lblMensajePDF;
    @FXML private Tab tabConsultar;


   // private ObservableList<Camioneta> listaCamionetas = FXCollections.observableArrayList();

    private Map<String, Integer> objetos = new HashMap<>();

    @FXML public void initialize(){
        actualizarLista();
        mostrarPaneIncio();
        configurarColumnas();
//        cargarDatos();
        cargarZonas();
        fechasCont();
        // Asignar la lista observable a la tabla
        //tableCamionetas.setItems(listaCamionetas);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Consultar.fxml"));
            Parent contenidoConsultar = loader.load();

            // Agregar el contenido al tab
            tabConsultar.setContent(contenidoConsultar);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar Consultar.fxml");
        }


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

    @FXML private void clearZonas(){
        chBoxDestPnDestPrepViaje.setValue(null);
        chBoxDestPnDestPrepViaje2.setValue(null);
        chBoxDestPnDestPrepViaje3.setValue(null);
        tipoViajeOpcs.selectToggle(null);
        datePnDestPrepViaje.setValue(null);

        if (tableQuimicos != null) tableQuimicos.getItems().clear();
        if (tableElementos != null) tableElementos.getItems().clear();
        if (tableCamionetas != null) tableCamionetas.getItems().clear();
        if (tableMaquinas != null) tableMaquinas.getItems().clear();

        chBoxDestPnDestPrepViaje.setDisable(false);
        chBoxDestPnDestPrepViaje2.setDisable(false);
        chBoxDestPnDestPrepViaje3.setDisable(false);

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

    @FXML private void ingresarViaje(){
        LocalDate fechainicio = datePnDestPrepViaje.getValue();

        if (fechainicio == null) {
            mostrarAlerta("Error","Fecha no seleccionada.");
            return;
        }

        Viaje.Tipo tipo;
        if (radbtnDiaPaneDestPrepViaje.isSelected()){
            tipo = Viaje.Tipo.Diario;
        }else if (radbtnBarrPaneDestPrepViaje.isSelected()){
            tipo = Viaje.Tipo.Barrido;
        }else {
            mostrarAlerta("Error", "Debe seleccionar un tipo de viaje.");
            return;
        }

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

        ObservableList<Camioneta> camionetas = tableCamionetas.getItems();
        ObservableList<Maquina> maquinas = tableMaquinas.getItems();
        ObservableList<ProductoQuimico> productos = tableQuimicos.getItems();
        ObservableList<ElementoDeSeguridad> elementos = tableElementos.getItems();

        if (camionetas.isEmpty() || maquinas.isEmpty() || productos.isEmpty() || elementos.isEmpty()) {
            mostrarAlerta("Error","Una de las listas de recursos esta vacia.");
            return;
        }

        Viaje nuevo = new Viaje();
        nuevo.setFechaInicio(fechainicio);
        nuevo.setTipo(tipo);

        ViajeDAO dao = new ViajeDAO();
        dao.insertar(nuevo);

        DestinoDAO dao2 = new DestinoDAO();
        for (Integer ZCod : idsDests) {
            Destino destino = new Destino(nuevo.getVCod(),ZCod);
            dao2.insertar(destino);
        }

        UsanDAO dao3 = new UsanDAO();
        for (Camioneta c : camionetas) {
            dao3.insertar(new Usan(nuevo.getVCod(), c.getCCod()));
        }

        clearZonas();
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


//    private Connection conn;

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
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

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

            // Obtener el objeto real
            Object seleccionadoObj = controller.getSeleccionado();
            if (seleccionadoObj != null) {
                Camioneta seleccionado = (Camioneta) seleccionadoObj;

          //      listaCamionetas.add(seleccionado); // agregar a la lista observable

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
                        return prod.getNombre() + " | Marca: " + prod.getMarca();
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
        // Crear una lista general
        List<Object> listaGeneral = new ArrayList<>();

        // Agregar todos los elementos de cada tabla
        listaGeneral.addAll(tableCamionetas.getItems());
        listaGeneral.addAll(tableQuimicos.getItems());
        listaGeneral.addAll(tableMaquinas.getItems());
        listaGeneral.addAll(tableElementos.getItems());


        // Mostrar en consola (para probar)
        System.out.println("Elementos guardados:");
        for (Object obj : listaGeneral) {
            System.out.println(" - " + obj);
        }

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

//    @FXML
//    private void onEditar() {
//        modoEdicion = !modoEdicion; // alternar entre editar / no editar
//
//        if (modoEdicion) {
//            btnEditar.setText("Cancelar");
//            agregarColumnaBorrar(tableCamionetas);
//            agregarColumnaBorrar(tableQuimicos);
//            agregarColumnaBorrar(tableMaquinas);
//        } else {
//            btnEditar.setText("Editar");
//            quitarColumnaBorrar(tableCamionetas);
//            quitarColumnaBorrar(tableQuimicos);
//            quitarColumnaBorrar(tableMaquinas);
//        }
//    }

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
//                    T item = getTableView().getItems().get(getIndex());
//                    getTableView().getItems().remove(item);
//                    System.out.println("Eliminado: " + item);
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



//    public void cargarElementosSeguridad(String tipoViaje) {
//        try {
//            ElementoSeguridadDAO dao = new ElementoSeguridadDAO(conn);
//            tableElementos.getItems().setAll(dao.listarPorTipoViaje(tipoViaje));
//        } catch (SQLException e) {
//            e.printStackTrace();
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
                //Crear la conexión

                Connection conn = ConexionBD.getConexion();

                //Crear el DAO con esa conexión
                ElementoSeguridadDAO dao = new ElementoSeguridadDAO(conn);

                //Obtener la lista de elementos según el tipo de viaje
                List<ElementoDeSeguridad> lista = dao.listarPorTipoViaje(tipoViaje);

                // 🔎 Filtramos los elementos que no estén deteriorados
                List<ElementoDeSeguridad> filtrados = lista.stream()
                        .filter(e -> e.getEstado().equalsIgnoreCase("Nuevo")
                                || e.getEstado().equalsIgnoreCase("Usado"))
                        .toList();

                //Cargar la lista en la tabla
                tableElementos.getItems().setAll(filtrados);

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
        // Cambiar al panel de elementos
        mostrarPanelElementos();
    }


    public void onCancelar(javafx.event.ActionEvent actionEvent) {
        // Advertencia
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
}

