package proyect.chagas_dengue.interfaz;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import proyect.chagas_dengue.modelos.*;

import java.util.HashMap;
import java.util.Map;

public class PlanificarViajeController {

    public void actualizarListaVisual(
            VBox listaContainer,
            ObservableList<Empleado> empleados,
            ObservableList<ProductoQuimico> productos,
            ObservableList<ElementoDeSeguridad> elementos,
            ObservableList<Camioneta> camionetas,
            ObservableList<Maquina> maquinas
    ) {
        listaContainer.getChildren().clear();

        // --- Contadores ---
        Map<Integer, Integer> contEmpleados = new HashMap<>();
        Map<Integer, Integer> contProductos = new HashMap<>();
        Map<String, Integer> contSeguridad = new HashMap<>();
        Map<String, Integer> contCamionetas = new HashMap<>();
        Map<String, Integer> contMaquinas = new HashMap<>();

        // --- EMPLEADOS ---
        if (empleados != null) {
            for (Empleado e : empleados) {
                contEmpleados.put(e.getECod(), contEmpleados.getOrDefault(e.getECod(), 0) + 1);
            }
        }

        // --- PRODUCTOS QUÍMICOS ---
        if (productos != null) {
            for (ProductoQuimico p : productos) {
                contProductos.put(p.getPCod(), contProductos.getOrDefault(p.getPCod(), 0) + 1);
            }
        }

        // --- ELEMENTOS DE SEGURIDAD ---
        if (elementos != null) {
            for (ElementoDeSeguridad e : elementos) {
                contSeguridad.put(e.getDescripcion(), contSeguridad.getOrDefault(e.getDescripcion(), 0) + 1);
            }
        }

        // --- CAMIONETAS ---
        if (camionetas != null) {
            for (Camioneta c : camionetas) {
                contCamionetas.put(c.getPatente(), contCamionetas.getOrDefault(c.getPatente(), 0) + 1);
            }
        }

        // --- MÁQUINAS ---
        if (maquinas != null) {
            for (Maquina m : maquinas) {
                String nombre = m.getTipo() + " - " + m.getModelo();
                contMaquinas.put(nombre, contMaquinas.getOrDefault(nombre, 0) + 1);
            }
        }

        // --- Mostrar resultados ---
        int contador = 1;
        contador = agregarSeccionEmpleados(listaContainer, "EMPLEADOS", contEmpleados, empleados, contador);
        contador = agregarSeccionProductos(listaContainer, "PRODUCTOS QUÍMICOS", contProductos, productos, contador);
        contador = agregarSeccionTexto(listaContainer, "ELEMENTOS DE SEGURIDAD", contSeguridad, contador);
        contador = agregarSeccionTexto(listaContainer, "CAMIONETAS", contCamionetas, contador);
        agregarSeccionTexto(listaContainer, "MÁQUINAS", contMaquinas, contador);

        // --- Caso vacío ---
        if (listaContainer.getChildren().isEmpty()) {
            Label vacio = new Label("No hay elementos en ninguna tabla");
            vacio.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
            listaContainer.getChildren().add(vacio);
        }
    }

    // === SECCIÓN EMPLEADOS ===
    private int agregarSeccionEmpleados(VBox listaContainer, String titulo, Map<Integer, Integer> contEmpleados, ObservableList<Empleado> empleados, int contador) {
        if (contEmpleados.isEmpty()) return contador;

        Label labelTitulo = new Label(titulo + ":");
        labelTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 5 5 5;");
        listaContainer.getChildren().add(labelTitulo);

        for (Map.Entry<Integer, Integer> entry : contEmpleados.entrySet()) {
            int codigo = entry.getKey();
            String nombre = empleados.stream().filter(e -> e.getECod() == codigo).map(Empleado::getNombre).findFirst().orElse("Empleado código " + codigo);

            Label label = new Label(String.format("   %d. %s x%d", contador++, nombre, entry.getValue()));
            label.setStyle("-fx-font-size: 14px; -fx-padding: 2px 5px;");
            listaContainer.getChildren().add(label);
        }
        return contador;
    }

    // === SECCIÓN PRODUCTOS QUÍMICOS ===
    private int agregarSeccionProductos(VBox listaContainer,String titulo,Map<Integer, Integer> contProductos,ObservableList<ProductoQuimico> productos,int contador) {
        if (contProductos.isEmpty()) return contador;

        Label labelTitulo = new Label(titulo + ":");
        labelTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 5 5 5;");
        listaContainer.getChildren().add(labelTitulo);

        for (Map.Entry<Integer, Integer> entry : contProductos.entrySet()) {
            int codigo = entry.getKey();
            String nombre = productos.stream().filter(p -> p.getPCod() == codigo).map(ProductoQuimico::getNombre).findFirst().orElse("Código " + codigo);

            Label label = new Label(String.format("   %d. %s x%d", contador++, nombre, entry.getValue()));
            label.setStyle("-fx-font-size: 14px; -fx-padding: 2px 5px;");
            listaContainer.getChildren().add(label);
        }
        return contador;
    }

    // === SECCIÓN DE TEXTO GENÉRICA ===
    private int agregarSeccionTexto(
            VBox listaContainer,
            String titulo,
            Map<String, Integer> mapa,
            int contador
    ) {
        if (mapa.isEmpty()) return contador;

        Label labelTitulo = new Label(titulo + ":");
        labelTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 5 5 5;");
        listaContainer.getChildren().add(labelTitulo);

        for (Map.Entry<String, Integer> entry : mapa.entrySet()) {
            Label label = new Label(String.format("   %d. %s x%d", contador++, entry.getKey(), entry.getValue()));
            label.setStyle("-fx-font-size: 14px; -fx-padding: 2px 5px;");
            listaContainer.getChildren().add(label);
        }
        return contador;
    }
}
