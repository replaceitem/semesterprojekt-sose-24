module org.driveractivity {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens org.driveractivity to javafx.fxml;
    exports org.driveractivity;
}