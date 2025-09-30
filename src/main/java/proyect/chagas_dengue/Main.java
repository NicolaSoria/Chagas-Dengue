package proyect.chagas_dengue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 600);
        stage.setTitle("MenuBar FXML");
        stage.setScene(scene);
        stage.show();
    }
}