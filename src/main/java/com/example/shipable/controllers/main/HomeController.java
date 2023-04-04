package com.example.shipable.controllers.main;


import com.example.shipable.controllers.info.CustomerInfoController;
import com.example.shipable.dao.CustomerService;
import com.example.shipable.dao.UserService;
import com.example.shipable.entities.Customers;
import com.example.shipable.entities.Users;
import com.example.shipable.helpers.CommonClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController extends CommonClass implements Initializable {
    @FXML
    private TableColumn<Customers, Integer> customerId;

    @FXML
    private TableColumn<Customers, String> fullName;

    @FXML
    private TableColumn<Customers, String> gander;
    @FXML
    private TableColumn<Customers, String> phone;

    @FXML
    private TableColumn<Customers, String> shift;
    @FXML
    private TableView<Customers> tableView;

    @FXML
    private TableColumn<Customers, String> address;

    @FXML
    private TableColumn<Customers, String> imagePath;
    @FXML
    private TextField search;
    @FXML
    private TableColumn<Customers, String> weight;

    private ObservableList<Customers> customersList;
    private FilteredList<Customers> filteredList;
    @FXML
    private Label usersCount;
    @FXML
    private Label customersCount;

    private final int nextUserId;
    private final int nextCustomerId;


    public HomeController() throws SQLException {
        nextUserId = (UserService.predictNextId() - 1);
        nextCustomerId = (CustomerService.predictNextId() - 1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println();
        Platform.runLater(() -> {
            initTable();
            searchFilter();
            usersCount.setText(nextUserId == 1 ? nextUserId + " user" : nextUserId + " users");
            customersCount.setText(nextCustomerId == 1 ? nextCustomerId + " member" : nextCustomerId + " members");
        });
    }

    private void initTable() {
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        fullName.setCellValueFactory(customers -> new SimpleStringProperty(customers.getValue().firstNameProperty().get() + "   " + customers.getValue().getMiddleName() + "   " + customers.getValue().getLastName()));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        gander.setCellValueFactory(new PropertyValueFactory<>("gander"));
        shift.setCellValueFactory(new PropertyValueFactory<>("shift"));
        weight.setCellValueFactory(customers -> new SimpleStringProperty(customers.getValue().getWeight() + "Kg"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        imagePath.setCellValueFactory(customers ->
                new SimpleStringProperty(customers.getValue().getImage() == null ? "x"
                        : "√"));

        if (customersList.isEmpty()) {
            tableView.setPlaceholder(new Label("MACAAMIIL KUUMA DIWAAN GASHANA."));
        } else {
            tableView.setItems(customersList);
        }
    }

    @FXML
    void paymentHandler() throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            FXMLLoader loader = openNormalWindow("/com/example/shipable/views/main/payments.fxml", borderPane);
            PaymentController controller = loader.getController();
            controller.setCustomer(tableView.getSelectionModel().getSelectedItem());
            controller.setBorderPane(borderPane);
            controller.setActiveUser(activeUser);
        }

    }

    @FXML
    void fullInfoHandler() throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            FXMLLoader loader = openNormalWindow("/com/example/shipable/views/info/customer-info.fxml", borderPane);
            CustomerInfoController controller = loader.getController();
            controller.setCustomer(tableView.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    void deleteHandler() {
        try {
            Customers selectedItem = tableView.getSelectionModel().getSelectedItem();
            deleteConfirm(selectedItem);
        } catch (SQLException e) {
            infoAlert(e.getMessage());
        }
    }

    @FXML
    void updateHandler() throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            FXMLLoader loader = openNormalWindow("/com/example/shipable/views/main/registrations.fxml", borderPane);
            RegistrationController controller = loader.getController();
            controller.setCustomer(tableView.getSelectionModel().getSelectedItem());
            controller.setActiveUser(activeUser);
            controller.setBorderPane(borderPane);

        }
    }

    @Override
    public void setActiveUser(Users activeUser) {
        super.setActiveUser(activeUser);
        try {
            customersList = CustomerService.fetchAllCustomer(activeUser);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBorderPane(BorderPane borderPane) {
        super.setBorderPane(borderPane);
    }


    private void searchFilter() {
        filteredList = new FilteredList<>(customersList, b -> true);
        SortedList<Customers> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        search.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(customer -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            if (customer.getFirstName().contains(newValue.toLowerCase()) || customer.getFirstName().contains(newValue.toUpperCase())) {
                return true;
            } else if (customer.getPhone().contains(newValue)) {
                return true;
            } else if (customer.getLastName().contains(newValue.toLowerCase()) || customer.getLastName().contains(newValue.toUpperCase())) {
                return true;
            } else
                return customer.getMiddleName().contains(newValue.toLowerCase()) || customer.getMiddleName().contains(newValue.toUpperCase());
        }));

    }


    private void deleteConfirm(Customers customer) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ma hubtaa inaad ka baxdo user ka " + activeUser.getUsername(), no, ok);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ok) {
            CustomerService.deleteCustomer(customer);
            informationAlert("Deleted successfully");
        } else {
            alert.close();
        }
    }
}
