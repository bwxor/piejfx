module com.bwxor.plugin {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bwxor.plugin to javafx.fxml;
    exports com.bwxor.plugin;
    exports com.bwxor.plugin.service;
    exports com.bwxor.plugin.dto;
    exports com.bwxor.plugin.type;
    opens com.bwxor.plugin.service to javafx.fxml;
    exports com.bwxor.plugin.input;
    opens com.bwxor.plugin.input to javafx.fxml;
}