package com.example.shipable.controllers.services;

import com.example.shipable.HelloApplication;
import com.example.shipable.controllers.info.WarningController;
import com.example.shipable.dao.CustomerService;
import com.example.shipable.entities.Customers;
import com.example.shipable.entities.Payments;
import com.example.shipable.entities.Users;
import com.example.shipable.helpers.CommonClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SplashScreenController extends CommonClass implements Initializable {
    @FXML
    private ProgressBar progress;
    @FXML
    private Label waiting;
    @FXML
    private Label welcomeUserName;
    @FXML
    private ImageView loadingImage;

    private final ObservableList<Customers> warningList;
    // private Stage stage;

    public SplashScreenController() {
        this.warningList = FXCollections.observableArrayList();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FetchOnlineCustomersByGander.setOnSucceeded(e -> {
            System.out.println("Finished");
            System.out.println(warningList);
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/shipable/views/info/warning.fxml"));

            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            WarningController controller = fxmlLoader.getController();
            controller.setOutdatedCustomers(warningList);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
           // URL url = getClass().getResource("/com/example/gymproject/style/icons/app-icon.jpeg");
          //  stage.getIcons().add(new Image(String.valueOf(url)));
            stage.show();
//            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/gymproject/views/dashboard.fxml"));
//            Platform.runLater(() -> {
//                stage = (Stage) progress.getScene().getWindow();
//                closeSplash();
//            });
//            try {
//                Scene scene = new Scene(fxmlLoader.load());
//                DashboardController controller = fxmlLoader.getController();
//                controller.setWarningList(warningList);
//                controller.setActiveUser(activeUser);
//                Stage stage = new Stage(StageStyle.UNDECORATED);
//                stage.setScene(scene);
//                URL url = getClass().getResource("/com/example/gymproject/style/icons/app-icon.jpeg");
//                stage.getIcons().add(new Image(String.valueOf(url)));
//                stage.show();
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }
        });
    }

    public Task<Void> FetchOnlineCustomersByGander = new Task<>() {
        private final LocalDate now = LocalDate.now();

        @Override
        protected Void call() throws Exception {
            ObservableList<Customers> offlineCustomers = CustomerService.fetchOnlineCustomer(activeUser);
            int i = 0;
            int sleepTime = offlineCustomers.size() <= 10 ? 1000 : 100;

            for (Customers customer : offlineCustomers) {
                i++;
                updateMessage("Loading.. " + i + "%");
                updateProgress(i, offlineCustomers.size());
                for (Payments payment : customer.getPayments()) {
                    LocalDate expDate = payment.getExpDate();
                    if (now.plusDays(2).isEqual(expDate) || now.plusDays(1).isEqual(expDate) || now.isEqual(expDate)) {
                        warningList.add(customer);
                    } else {
                        // TODO: 05/04/2023 Make the payment of
                        System.out.println(customer.getFirstName() + " " + expDate + " Outdated");

                        ///PaymentModel.offPayment(payment);
                    }

                }
                Thread.sleep(sleepTime);
            }
            return null;
        }
    };

    @Override
    public void setActiveUser(Users activeUser) {
        super.setActiveUser(activeUser);
        Thread thread = new Thread(FetchOnlineCustomersByGander);
        thread.setDaemon(true);
        thread.start();
        progress.progressProperty().bind(FetchOnlineCustomersByGander.progressProperty());
        welcomeUserName.setText("Welcome " + activeUser.getUsername());
        waiting.textProperty().bind(FetchOnlineCustomersByGander.messageProperty());

        URL url = getClass().getResource(activeUser.getGender().equals("Male") ? images[1] : images[2]);
        Image image = new Image(String.valueOf(url));
        loadingImage.setImage(image);


    }


}