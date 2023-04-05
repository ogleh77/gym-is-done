package com.example.shipable;

import com.example.shipable.controllers.info.CustomerInfoController;
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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/shipable/views/service/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
// TODO: 05/04/2023 Lastly make the outdated payment off insha Allah
        // TODO: 04/04/2023 controller MAin is done checking next services insha Allah 

    }

    public static void main(String[] args) {
        launch();
    }
}