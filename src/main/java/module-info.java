module proyect.chagas_dengue {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires proyect.chagas_dengue;


    opens proyect.chagas_dengue to javafx.fxml;
    opens proyect.chagas_dengue.interfaz to javafx.fxml;
    exports proyect.chagas_dengue;
}