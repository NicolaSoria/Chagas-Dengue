package proyect.chagas_dengue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import proyect.chagas_dengue.conexion.ConexionBD;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Mostrar pantalla de login primero
        mostrarLogin();
    }

    /**
     * Muestra la pantalla de login
     */
    public void mostrarLogin() throws IOException {
        try {
            // Ruta CORREGIDA - incluye la carpeta "interfaz"
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Login-view.fxml"));

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 400, 500);
            primaryStage.setTitle("Inicio de Sesión - Chagas/Dengue");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error al cargar Login-view.fxml:");
            e.printStackTrace();
            // Si falla, intenta cargar la pantalla principal directamente
            cargarPantallaPrincipalComoRespaldo();
        }
    }

    /**
     * Método para cambiar a la pantalla principal después del login exitoso
     */
    public static void mostrarPantallaPrincipal() throws IOException {
        try {
            // También actualiza la ruta del Main-view.fxml si está en la misma carpeta
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main-view.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 800, 660);
            primaryStage.setTitle("Sistema Chagas/Dengue");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al cargar Main-view.fxml:");
            e.printStackTrace();
        }
    }

    /**
     * Método de respaldo si no se encuentra el login
     */
    private void cargarPantallaPrincipalComoRespaldo() throws IOException {
        try {
            // Intenta diferentes rutas para Main-view.fxml
            FXMLLoader fxmlLoader = null;

            // Opción 1: En la carpeta interfaz
            try {
                fxmlLoader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/interfaz/Main-view.fxml"));
            } catch (Exception e1) {
                // Opción 2: En la raíz (ruta anterior)
                try {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/proyect/chagas_dengue/Main-view.fxml"));
                } catch (Exception e2) {
                    throw new IOException("No se pudo encontrar Main-view.fxml en ninguna ubicación");
                }
            }

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 660);
            primaryStage.setTitle("Sistema Chagas/Dengue (Modo Respaldo)");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error crítico: No se pudo cargar ninguna pantalla:");
            e.printStackTrace();
            // Muestra un mensaje de error al usuario
            mostrarErrorCritico();
        }
    }

    private void mostrarErrorCritico() {
        // Crear una escena de error básica
        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error: No se pudieron cargar los archivos de la interfaz.\n\n" +
                        "Verifique que los archivos FXML existan en:\n" +
                        "• /proyect/chagas_dengue/interfaz/Login-view.fxml\n" +
                        "• /proyect/chagas_dengue/interfaz/Main-view.fxml"
        );
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 20px;");

        Scene errorScene = new Scene(new javafx.scene.layout.StackPane(errorLabel), 600, 200);
        primaryStage.setTitle("Error - Sistema Chagas/Dengue");
        primaryStage.setScene(errorScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // --- PRUEBA DE CONEXIÓN ---
        try {
            Connection conexion = ConexionBD.getInstancia().getConexion();
            if (conexion != null && !conexion.isClosed()) {
                System.out.println("Conexión exitosa con la base de datos!");
            } else {
                System.out.println("No se pudo conectar a la base de datos.");
            }
        } catch (Exception e) {
            System.out.println("Error al conectar con la base de datos:");
            e.printStackTrace();
        }

        // --- LANZAR APLICACIÓN FXML ---
        launch(args);
    }
}