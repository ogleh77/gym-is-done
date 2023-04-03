package com.example.shipable;

import com.example.shipable.controllers.RegistrationController;
import com.example.shipable.dao.CustomerService;
import com.example.shipable.dao.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/shipable/views/registrations.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        RegistrationController controller = fxmlLoader.getController();
        controller.setActiveUser(UserService.users().get(0));
        controller.setCustomer(CustomerService.fetchAllCustomer(UserService.users().get(0)).get(0));

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}