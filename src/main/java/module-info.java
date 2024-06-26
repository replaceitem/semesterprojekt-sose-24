module org.driveractivity {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires jakarta.xml.bind;
    requires org.jetbrains.annotations;


    opens org.driveractivity.gui to javafx.fxml;
    opens org.driveractivity.DTO to jakarta.xml.bind;
    exports org.driveractivity.gui;
    exports org.driveractivity.entity;
    exports org.driveractivity.service;
    exports org.driveractivity.exception;
}