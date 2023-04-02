module com.example.shipable {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.shipable to javafx.fxml;
    exports com.example.shipable;
}