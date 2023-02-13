module com.example.gcex {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gcex to javafx.fxml;
    exports com.example.gcex;
}