module it.unipi.barplanner.clientapp {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires com.google.gson;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires static lombok;
    requires java.desktop;
    requires com.github.librepdf.openpdf;
    
    opens it.unipi.barplanner.clientapp to javafx.fxml, com.google.gson;
    exports it.unipi.barplanner.clientapp;
}
