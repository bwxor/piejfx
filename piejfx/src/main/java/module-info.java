module com.bwxor.piejfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires javafx.graphics;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires org.json;

    opens com.bwxor.piejfx to javafx.fxml;
    opens com.bwxor.piejfx.controller;
    opens com.bwxor.piejfx.state;
    exports com.bwxor.piejfx;
    exports com.bwxor.piejfx.controller;
    exports com.bwxor.piejfx.state;
}