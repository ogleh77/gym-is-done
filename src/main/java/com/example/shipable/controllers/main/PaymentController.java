package com.example.shipable.controllers.main;

import animatefx.animation.FadeIn;
import com.example.shipable.dao.GymService;
import com.example.shipable.dao.PaymentService;
import com.example.shipable.entities.Box;
import com.example.shipable.entities.Customers;
import com.example.shipable.entities.Gym;
import com.example.shipable.entities.Payments;
import com.example.shipable.helpers.CommonClass;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentController extends CommonClass implements Initializable {
    @FXML
    private TextField amountPaid;

    @FXML
    private ComboBox<Box> boxChooser;

    @FXML
    private JFXButton createBtn;

    @FXML
    private TextField discount;

    @FXML
    private Label discountValidation;

    @FXML
    private DatePicker expDate;

    @FXML
    private JFXRadioButton female;

    @FXML
    private TextField firstName;

    @FXML
    private ImageView imgView;

    @FXML
    private Label infoMin;

    @FXML
    private TextField lastName;

    @FXML
    private JFXRadioButton male;

    @FXML
    private TextField middleName;

    @FXML
    private ComboBox<String> paidBy;

    @FXML
    private Label paymentInfo;

    @FXML
    private TextField phone;
    @FXML
    private JFXCheckBox poxing;
    private final Gym currentGym;
    private final double fitnessCost;
    private final double poxingCost;
    private final double vipBoxCost;

    private double currentCost = 0;
    private final ButtonType ok;
    private boolean done = false;
    private ObservableList<Payments> paymentsList;
    private Payments payment;

    public PaymentController() throws SQLException {
        this.currentGym = GymService.getGym();
        this.fitnessCost = currentGym.getFitnessCost();
        this.poxingCost = currentGym.getPoxingCost();
        this.vipBoxCost = currentGym.getBoxCost();
        this.ok = new ButtonType("Back to home", ButtonBar.ButtonData.OK_DONE);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            if (payment == null) {
                initFields();
                currentCost = fitnessCost;
                amountPaid.setText(String.valueOf(currentCost));
                paymentValidation();
                validateDiscount();
            }

        });

        service.setOnSucceeded(e -> {
            createBtn.setGraphic(null);
            createBtn.setText("Created");
            if (done) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, customer.getFirstName() + " Waxad u samaysay payment cusub", ok);
                Optional<ButtonType> result = alert.showAndWait();
                borderPane.getLeft().setDisable(false);
                if (result.isPresent() && result.get() == ok) {
                    try {
                        backGoToHome();
                    } catch (IOException ex) {
                        errorMessage(ex.getMessage());
                    }
                } else {
                    createBtn.setDisable(true);
                }
            }
        });
    }

    @FXML
    void createPaymentHandler() {
        if (isValid(getMandatoryFields(), null)) {
            if (start) {
                service.restart();
                createBtn.setGraphic(getLoadingImageView());
                createBtn.setText("Creating");
            } else {
                service.start();
                createBtn.setGraphic(getLoadingImageView());
                createBtn.setText("Creating");
                start = true;
            }

        }
    }

    @Override
    public void setCustomer(Customers customer) {
        super.setCustomer(customer);
        if (customer != null) {
            firstName.setText(customer.getFirstName());
            middleName.setText(customer.getFirstName());
            lastName.setText(customer.getFirstName());
            middleName.setText(customer.getMiddleName());
            lastName.setText(customer.getLastName());
            phone.setText(customer.getPhone());
            male.setSelected(customer.getGander().equals("Male"));
            female.setSelected(customer.getGander().equals("Female"));

            if (customer.getImage() != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(customer.getImage());
                Image image = new Image(bis);
                imageUploaded = true;
                imgView.setImage(image);
            }
            try {
                paymentsList = PaymentService.fetchAllCustomersPayments(customer.getPhone());
            } catch (SQLException e) {
                errorMessage(e.getMessage());
            }
            if (!paymentsList.isEmpty()) {
                for (Payments payment : paymentsList) {
                    checkOnlinePayment(payment);
                }
            }
        }
    }

    private void checkOnlinePayment(Payments payment) {
        if ((payment.isOnline() || payment.isPending()) && (payment.getExpDate().isAfter(LocalDate.now())) || payment.getExpDate().equals(LocalDate.now())) {
            this.payment = payment;
            blockFields(payment);
        }
    }

    private void initFields() {
        expDate.setValue(LocalDate.now().plusDays(30));
        expDate.setStyle("-fx-opacity: 1");
        paidBy.setItems(getPaidBy());
        currentCost = fitnessCost;
        amountPaid.setText(String.valueOf(currentCost));
        getMandatoryFields().addAll(amountPaid, paidBy);
        if (boxChooser.getItems().isEmpty()) {
            for (Box box : currentGym.getVipBoxes()) {
                if (box.isReady()) boxChooser.getItems().add(box);
            }
        }
        boxChooser.getItems().add(new Box(0, "remove box", false));
    }

    private void blockFields(Payments payment) {
        amountPaid.setEditable(false);
        amountPaid.setText(String.valueOf(payment.getAmountPaid()));
        poxing.setSelected(payment.isPoxing());
        poxing.setDisable(true);
        poxing.setStyle("-fx-opacity: 1");
        paidBy.setValue(payment.getPaidBy());
        paidBy.setEditable(false);
        boxChooser.setValue(payment.getBox() != null ? payment.getBox() : null);
        boxChooser.setEditable(false);
        discount.setText(payment.getDiscount() + "");
        discount.setEditable(false);
        expDate.setValue(payment.getExpDate());
        createBtn.setDisable(true);
        tellInfo(payment.getExpDate(), payment.isPending());
    }

    private void tellInfo(LocalDate expDate, boolean isPending) {
        paymentInfo.setText(isPending ? "Macmillku payment ayaa u xidhan" : "Macmiilkan wakhtigu kama dhicin ");
        infoMin.setText(isPending ? "Macmiilka waxa u xidhay payment saaso ay tahay looma samayn karo " + "payment cusub" : "wuxuse ka dhaacyaa [" + expDate.toString() + "] Insha Allah");
        paymentInfo.setStyle("-fx-text-fill: #d20e0e; -fx-font-family: Verdana;-fx-font-size: 15");
        FadeIn fadeIn = new FadeIn(paymentInfo);
        fadeIn.setCycleCount(50);
        fadeIn.setDelay(Duration.millis(100));
        fadeIn.play();
    }

    private void validateDiscount() {
        discount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                discount.setText(newValue.replaceAll("[^\\d\\.?}]", ""));
            }
            if (!discount.getText().isBlank()) {
                double _discount = Double.parseDouble(discount.getText());
                if (_discount > currentGym.getMaxDiscount()) {
                    discountValidation.setText("Qimo dhimista u badani waa $" + currentGym.getMaxDiscount());
                    discountValidation.setVisible(true);
                } else {
                    discountValidation.setVisible(false);
                }
            }
        });

    }

    private void paymentValidation() {
        boxChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((oldValue == null || oldValue.getBoxName().matches("re.*")) && !newValue.getBoxName().matches("re.*")) {
                currentCost += vipBoxCost;
            } else if (oldValue != null && boxChooser.getValue().getBoxName().matches("re.*")) {
                currentCost -= vipBoxCost;
            }
            amountPaid.setText(String.valueOf(currentCost));
        });

        poxing.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (poxing.isSelected()) {
                currentCost += poxingCost;
            } else {
                currentCost -= poxingCost;
            }
            amountPaid.setText(String.valueOf((currentCost)));

        });
    }

    private void backGoToHome() throws IOException {
        FXMLLoader loader = openNormalWindow("/com/example/shipable/views/main/home.fxml", borderPane);
        HomeController controller = loader.getController();
        controller.setActiveUser(activeUser);
        controller.setBorderPane(borderPane);
    }

    private final Service<Void> service = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() {
                    try {
                        double _discount = (!discount.getText().isEmpty() || !discount.getText().isBlank() ? Double.parseDouble(discount.getText()) : 0);

                        currentCost -= _discount;
                        Payments payment = new Payments(0, LocalDate.now().toString(), expDate.getValue(), String.valueOf(LocalDate.now().getMonth()), String.valueOf(LocalDate.now().getYear()), currentCost, paidBy.getValue(), _discount, poxing.isSelected(), customer.getPhone(), true, false);


                        if (boxChooser.getValue() != null && !boxChooser.getValue().getBoxName().matches("remove box")) {
                            payment.setBox(boxChooser.getValue());
                        }
                        customer.getPayments().add(0, payment);
                        PaymentService.insertPayment(customer);
                        Thread.sleep(1000);
                        done = true;
                    } catch (Exception e) {
                        Platform.runLater(() -> errorMessage(e.getMessage()));
                    }
                    return null;
                }
            };
        }
    };

}