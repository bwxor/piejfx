module com.bwxor.piejfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires javafx.graphics;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires org.json;
    requires net.harawata.appdirs;
    requires com.techsenger.jeditermfx.ui;
    requires com.techsenger.jeditermfx.core;
    requires pty4j;
    requires com.helger.css;
    requires com.helger.collection;
    requires org.jetbrains.annotations;
    requires java.management;
    requires javafx.base;
    requires com.helger.base;
    requires com.bwxor.plugin;

    opens com.bwxor.piejfx to javafx.fxml;
    opens com.bwxor.piejfx.controller;
    opens com.bwxor.piejfx.state;
    exports com.bwxor.piejfx;
    exports com.bwxor.piejfx.controller;
    exports com.bwxor.piejfx.state;
}