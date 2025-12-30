module com.bwxor.plugin {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bwxor.plugin to javafx.fxml;
    exports com.bwxor.plugin;
}