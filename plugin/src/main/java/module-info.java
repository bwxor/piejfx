module com.bwxor.plugin {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bwxor.plugin to javafx.fxml;
    exports com.bwxor.plugin;
    exports com.bwxor.plugin.sender;
    opens com.bwxor.plugin.sender to javafx.fxml;
    exports com.bwxor.plugin.service;
    opens com.bwxor.plugin.service to javafx.fxml;
}