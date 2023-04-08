package com.example.shipable.controllers.services;

import com.example.shipable.dao.BoxService;
import com.example.shipable.dao.GymService;
import com.example.shipable.entities.Box;
import com.example.shipable.entities.Gym;
import com.example.shipable.helpers.CommonClass;
import com.example.shipable.helpers.CustomException;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class GymController extends CommonClass implements Initializable {
    @FXML
    private TextField addBox;

    @FXML
    private TextField boxCost;

    @FXML
    private TextField eDahab;

    @FXML
    private TextField fitnessCost;

    @FXML
    private TextField gymName;

    @FXML
    private ListView<Box> listView;

    @FXML
    private TextField maxDiscount;

    @FXML
    private TextField pendDate;

    @FXML
    private TextField poxingCost;
    @FXML
    private JFXButton updateBtn;
    @FXML
    private TextField zaad;
    private final Gym currentGym;
    private Stage thisStage;
    int nextId;


    public GymController() throws SQLException {
        currentGym = GymService.getGym();
        nextId = BoxService.nextBoxID();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            initData();
            thisStage = (Stage) poxingCost.getScene().getWindow();
            enterKeyFire(updateBtn, thisStage);
        });
        fitnessValidation();
        poxingValidation();
        discountValidation();
        boxValidation();
        pendValidation();
        zaadValidation();
        eDahabValidation();

    }

    @FXML
    void cancelHandler() {
        closeStage(thisStage, listView.getParent());
    }

    @FXML
    void createBoxHandler() throws SQLException {
        if (!addBox.getText().isBlank() && !addBox.getText().isEmpty()) {
            Box box = new Box(nextId, addBox.getText(), true);
            System.out.println(box.getBoxId());
            try {
                BoxService.insertBox(box);
                listView.getItems().add(box);
                BoxService.fetchBoxes().add(box);
                informationAlert("New box added successfully");

            } catch (CustomException e) {
                errorMessage(e.getMessage());
            }
            nextId++;
        }
    }

    @FXML
    void deleteBoxHandler() {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            Box box = listView.getSelectionModel().getSelectedItem();
            try {
                BoxService.deleteBox(box);
                listView.getItems().remove(box);
                informationAlert("Box deleted successfully");
            } catch (SQLException e) {
                errorMessage(e.getMessage());
            }
        }
    }

    @FXML
    void updateHandler() {
        try {
            double fitness_Cost = Double.parseDouble(fitnessCost.getText());
            double pox_cost = Double.parseDouble(poxingCost.getText());
            double max_discount = Double.parseDouble(maxDiscount.getText());
            int zaad_number = Integer.parseInt(zaad.getText());
            int eDahab_number = Integer.parseInt(eDahab.getText());
            int pend_date = Integer.parseInt(pendDate.getText());
            double box_cost = Double.parseDouble(boxCost.getText());


            currentGym.setGymName(gymName.getText());
            currentGym.seteDahab(zaad_number);
            currentGym.setMaxDiscount(max_discount);
            currentGym.seteDahab(eDahab_number);
            currentGym.setZaad(zaad_number);
            currentGym.setPoxingCost(pox_cost);
            currentGym.setPendingDate(pend_date);
            currentGym.setBoxCost(box_cost);
            currentGym.setMaxDiscount(max_discount);
            currentGym.setFitnessCost(fitness_Cost);

            GymService.updateGym(currentGym);
            Platform.runLater(() -> {
                Alert alert = infoAlert("Gym updated successfully");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().getButtonData().isDefaultButton()) {
                    closeStage(thisStage, fitnessCost.getParent());
                } else {
                    alert.close();
                }
            });

        } catch (Exception e) {
            if (e.getClass().isInstance(SQLException.class)) {
                errorMessage(e.getMessage());
            } else {
                errorMessage("Fadlan hubi inad si saxan u gelisay qimayasha " + "Tusaale 12 AMA 12.0 error caused " + e.getMessage());
            }
        }
    }

    //----------------------Helper methods-------------------
    private void initData() {
        gymName.setText(currentGym.getGymName());
        fitnessCost.setText(String.valueOf(currentGym.getFitnessCost()));
        boxCost.setText(String.valueOf(currentGym.getBoxCost()));
        poxingCost.setText(String.valueOf(currentGym.getPoxingCost()));
        maxDiscount.setText(String.valueOf(currentGym.getMaxDiscount()));
        eDahab.setText((String.valueOf(currentGym.geteDahab())));
        zaad.setText((String.valueOf(currentGym.getZaad())));
        listView.setItems(currentGym.getVipBoxes());
        pendDate.setText(String.valueOf(currentGym.getPendingDate()));
    }

    private void poxingValidation() {
        poxingCost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d*)")) {
                poxingCost.setText(newValue.replaceAll("[^\\d.?}]", ""));
            }
        });
    }

    private void boxValidation() {
        boxCost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d*)")) {
                boxCost.setText(newValue.replaceAll("[^\\d.?}]", ""));
            }
        });
    }

    private void pendValidation() {
        pendDate.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pendDate.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }

    private void discountValidation() {
        maxDiscount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxDiscount.setText(newValue.replaceAll("[^\\d.?}]", ""));
            }
        });
    }

    private void fitnessValidation() {
        fitnessCost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                fitnessCost.setText(newValue.replaceAll("[^\\d.?}]", ""));
            }
        });
    }

    private void zaadValidation() {
        zaad.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                zaad.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }

    private void eDahabValidation() {
        eDahab.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                eDahab.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }
}
