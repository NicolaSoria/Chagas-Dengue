module pryect.chagas_dengue {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires org.apache.pdfbox;


    opens proyect.chagas_dengue to javafx.fxml;
    exports proyect.chagas_dengue;
}