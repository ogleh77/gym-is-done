module com.example.shipable {
    requires javafx.controls;
    requires javafx.fxml;
    requires AnimateFX;
    requires com.jfoenix;
    requires junit;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.shipable to javafx.fxml;
    opens com.example.shipable.controllers to javafx.fxml;
    exports com.example.shipable;
    opens com.example.shipable.controllers.users to javafx.fxml;
    opens com.example.shipable.controllers.services to javafx.fxml;
    opens com.example.shipable.controllers.info to javafx.fxml;
    opens com.example.shipable.controllers.main to javafx.fxml;
    exports com.example.shipable.controllers;
    exports com.example.shipable.controllers.main;
    exports com.example.shipable.entities;
    exports com.example.shipable.controllers.services;

}