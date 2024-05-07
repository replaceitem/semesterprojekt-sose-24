module org.driveractivity {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.xml.bind;
    requires java.desktop;


    opens org.driveractivity.gui to javafx.fxml;
    exports org.driveractivity;
    exports org.driveractivity.gui;
    exports org.driveractivity.entity;
}