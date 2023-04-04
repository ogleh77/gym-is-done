package com.example.shipable.controllers;

import animatefx.animation.FadeInDownBig;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutLeft;
import com.example.shipable.controllers.info.OutDatedController;
import com.example.shipable.controllers.main.DashboardMenuController;
import com.example.shipable.controllers.main.HomeController;
import com.example.shipable.controllers.main.RegistrationController;
import com.example.shipable.controllers.users.UpdateUserController;
import com.example.shipable.controllers.users.UserChooserController;
import com.example.shipable.dao.GymService;
import com.example.shipable.entities.Customers;
import com.example.shipable.entities.Gym;
import com.example.shipable.entities.Users;
import com.example.shipable.helpers.CommonClass;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController extends CommonClass implements Initializable {
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label gymName;
    @FXML
    private Label warningLabel;
    @FXML
    private HBox warningParent;
    @FXML
    private VBox sidePane;
    @FXML
    private StackPane warningStack;
    @FXML
    private HBox menuHBox;

    @FXML
    private Circle activeProfile;
    @FXML
    private Label activeUserName;
    @FXML
    private MenuItem addUserBtn;
    @FXML
    private MenuItem updateUserBtn;
    @FXML
    private MenuItem gymBtn;

    private final Gym currentGym;
    private ObservableList<Customers> warningList;
    private boolean visible = false;
    private Stage dashboardStage;

    public DashboardController() throws SQLException {
        this.currentGym = GymService.getGym();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: 03/04/2023 Confirm the closing maain window insh llah
        Platform.runLater(() -> {
            gymName.textProperty().bind(currentGym.gymNameProperty());
            activeUserName.textProperty().bind(activeUser.usernameProperty());
            dashboardStage = (Stage) activeProfile.getScene().getWindow();
            borderPane.setLeft(null);
        });
        try {
            dashboard();
        } catch (IOException e) {
            errorMessage(e.getMessage());
        }
    }

    @FXML
    void menuClicked() {
        if (visible) {
            SlideOutLeft slideOutLeft = new SlideOutLeft();
            slideOutLeft.setNode(sidePane);
            slideOutLeft.play();
            slideOutLeft.setOnFinished(e -> borderPane.setLeft(null));
        } else {
            new SlideInLeft(sidePane).play();
            borderPane.setLeft(sidePane);
        }
        visible = !visible;
    }

    @FXML
    void profileHandler() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/users/user-update.fxml"));
        Scene scene = new Scene(loader.load());
        UpdateUserController controller = loader.getController();
        controller.setActiveUser(activeUser);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void warningHandler() {
    }

    @FXML
    void minimizeHandler() {
        dashboardStage.setIconified(true);
    }

    @FXML
    void closeHandler() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Ma hubtaa inad ka baxayso system ka", no, ok);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ok) {
            closeStage(dashboardStage, activeProfile.getParent());
        } else {
            alert.close();
        }
    }

    @FXML
    void dashboardHandler() throws IOException {
        dashboard();
        borderPane.setLeft(null);
        menuHBox.setVisible(!menuHBox.isVisible());
    }

    @FXML
    void homeHandler() throws IOException {
        FXMLLoader loader = openWindow("/com/example/shipable/views/main/home.fxml", borderPane,
                sidePane, null, warningStack);
        HomeController controller = loader.getController();
        controller.setActiveUser(activeUser);
        controller.setBorderPane(borderPane);
    }

    @FXML
    void registrationHandler() throws IOException {
        FXMLLoader loader = openWindow("/com/example/shipable/views/main/registrations.fxml", borderPane, sidePane, null, warningStack);
        RegistrationController controller = loader.getController();
        controller.setActiveUser(activeUser);
        controller.setBorderPane(borderPane);
        //controller.setCurrentGym(currentGym);
    }

    @FXML
    void outdatedHandler() throws IOException {
        FXMLLoader loader = openWindow("/com/example/shipable/views/info/outdated.fxml", borderPane,
                sidePane, null, warningStack);
        OutDatedController controller = loader.getController();
        controller.setActiveUser(activeUser);
    }


    @FXML
    void reportHandler() throws IOException {
        openWindow("/com/example/shipable/views/info/dailyReports.fxml", borderPane,
                sidePane, null, warningStack);
    }

    @FXML
    void backupHandler() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/service/backup.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    void userCreationHandler() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/users/user-creation.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(activeProfile.getScene().getWindow());
        stage.show();

        // TODO: 07/04/2023 Ste stage darag and drop insha Allah
    }

    @FXML
    void updateUserHandler() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/users/user-chooser.fxml"));
        Scene scene = new Scene(loader.load());
        UserChooserController controller = loader.getController();
        controller.tempActiveUser(activeUser);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void gymHandler() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/service/gym.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initOwner(activeProfile.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
        // TODO: 07/04/2023 Make drag and drop stage insha Allah
    }

    @FXML
    void logOutHandler() {
        openLogin();
    }


    @Override
    public void setActiveUser(Users activeUser) {
        super.setActiveUser(activeUser);
        activeUserName.setText(activeUser.getUsername() + " [" + activeUser.getRole() + "]");
        URL url;
        final String[] profileImages = {
                "/com/example/gymproject/style/icons/man-profile.jpeg",
                "/com/example/gymproject/style/icons/woman-hijap.jpeg"
        };

        if (activeUser.getGender().equals("Male")) {
            if (activeUser.getImage() == null) {
                url = getClass().getResource(profileImages[0]);
                activeProfile.setFill(new ImagePattern(new Image(String.valueOf(url))));
            } else {
                ByteArrayInputStream bis = new ByteArrayInputStream(activeUser.getImage());
                Image image = new Image(bis);
                activeProfile.setFill(new ImagePattern(image));
            }

        } else if (activeUser.getGender().equals("Female")) {
            if (activeUser.getImage() == null) {
                url = getClass().getResource(profileImages[1]);
                activeProfile.setFill(new ImagePattern(new Image(String.valueOf(url))));
            } else {
                ByteArrayInputStream bis = new ByteArrayInputStream(activeUser.getImage());
                Image image = new Image(bis);
                activeProfile.setFill(new ImagePattern(image));
            }
        }

        if (!activeUser.getRole().equals("super_admin")) {
            updateUserBtn.setDisable(true);
            addUserBtn.setDisable(true);
            gymBtn.setDisable(true);
        }
    }

//---------------------------Helpers---------------------------

    private void dashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/main/dashboard-menu.fxml"));
        AnchorPane anchorPane = loader.load();
        FadeInDownBig fadeIn = new FadeInDownBig(anchorPane);
        fadeIn.setOnFinished(e -> {
            DashboardMenuController controller = loader.getController();
            controller.setMenus(borderPane, sidePane, menuHBox, warningStack);
            controller.setActiveUser(activeUser);
            borderPane.setCenter(anchorPane);
        });
        fadeIn.setSpeed(1.5);
        fadeIn.play();
    }

    private void openLogin() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ma hubtaa inaad ka baxdo user ka " + activeUser.getUsername(), no, ok);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ok) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/service/login.fxml"));
            Stage stage = new Stage(StageStyle.UNDECORATED);
            Scene scene;
            closeStage(dashboardStage, activeProfile.getParent());
            try {
                scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        } else alert.close();
    }
}