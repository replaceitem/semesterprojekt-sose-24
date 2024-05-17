module org.driveractivity {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires java.xml.bind;


    opens org.driveractivity.gui to javafx.fxml;
    opens org.driveractivity.DTO to java.xml.bind;
    exports org.driveractivity;
    exports org.driveractivity.gui;
    exports org.driveractivity.entity;
    exports org.driveractivity.service;
}