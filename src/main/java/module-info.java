module org.driveractivity {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.xml.bind;
    requires java.desktop;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;


    opens org.driveractivity.gui to javafx.fxml;
    exports org.driveractivity;
    exports org.driveractivity.gui;
    exports org.driveractivity.entity;
}