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
    @Override


    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 660);
        stage.setTitle("MenuBar FXML");
        stage.setScene(scene);
        stage.show();
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
