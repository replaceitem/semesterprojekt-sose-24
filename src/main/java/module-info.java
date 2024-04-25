module org.example.driveractivity {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.driveractivity to javafx.fxml;
    exports org.example.driveractivity;
}