module com.bwxor.piejfxsdk {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.bwxor.plugin;

    opens com.bwxor.piejfxsdk to javafx.fxml;
    exports com.bwxor.piejfxsdk;
}