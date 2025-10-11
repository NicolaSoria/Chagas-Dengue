module pryect.chagas_dengue {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens proyect.chagas_dengue to javafx.fxml;
    exports proyect.chagas_dengue;
}