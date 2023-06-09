package com.example.shipable.controllers.services;

import com.example.shipable.dao.BackupService;
import com.example.shipable.entities.Users;
import com.example.shipable.helpers.CommonClass;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class BackupController extends CommonClass implements Initializable {
    @FXML
    private JFXButton backupBtn;
    @FXML
    private ListView<String> listView;

    @FXML
    private TextField name;
    @FXML
    private Label lastBackup;

    @FXML
    private JFXButton pathBtn;

    @FXML
    private JFXButton restoreBtn;

    private Stage stage;
    private String restorePath;
    private boolean restored = false;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = (Stage) listView.getScene().getWindow();
            getMandatoryFields().add(name);
            enterKeyFire(backupBtn, stage);

            try {
                if (!BackupService.backupPaths().isEmpty()) {
                    listView.getItems().add(BackupService.backupPaths().get(0));
                    String s = (BackupService.backupPaths().get(1));
                    lastBackup.setText("Last backup  " + s);
                }
            } catch (SQLException e) {
                errorMessage(e.getMessage());
            }
        });

        backupService.setOnSucceeded(e -> {
            backupBtn.setGraphic(getFirstImage("/com/example/shipable/style/icons/backup.png"));
            backupBtn.setText("Backup");
        });

        restoreService.setOnSucceeded(e -> {
            restoreBtn.setGraphic(getFirstImage("/com/example/shipable/style/icons/icons8-reset-48.png"));
            restoreBtn.setText("Restore");
            if (restored) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Restored successfully restart the system",
                        new ButtonType("Restart", ButtonBar.ButtonData.YES));

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get().getButtonData().isDefaultButton()) {
                    openLoginWindow();
                } else {
                    alert.close();
                }
            }
        });
    }

    @FXML
    void cancelHandler() {
        closeStage(stage, listView.getParent());
    }

    @FXML
    void backupHandler() {
        if (start) {
            backupService.restart();
            backupBtn.setGraphic(getLoadingImageView());
            backupBtn.setText("backuping");
        } else {
            backupService.start();
            backupBtn.setGraphic(getLoadingImageView());
            backupBtn.setText("backuping");
            start = true;
        }
    }

    @FXML
    void pathHandler() {
        if (listView.getItems().isEmpty()) {
            try {
                if (isValid(getMandatoryFields(), null)) {
                    pathSelector();
                }
            } catch (Exception e) {
                errorMessage(e.getMessage());
            }
        }
    }

    @FXML
    void restoreHandler() {
        restorePath = listView.getSelectionModel().getSelectedItem();
        if (restorePath == null) {
            restorePathConfirm();
        }
        if (restorePath != null)
            if (start) {
                restoreService.restart();
                restoreBtn.setGraphic(getLoadingImageView());
                restoreBtn.setText("Restoring");
            } else {
                restoreService.start();
                restoreBtn.setGraphic(getLoadingImageView());
                restoreBtn.setText("Restoring");
                start = true;
            }
    }

    private final Service<Void> backupService = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() {
                    try {
                        Thread.sleep(1000);
                        BackupService.backup(listView.getSelectionModel().getSelectedItem());
                        Platform.runLater(() -> informationAlert("Backup successfully."));
                    } catch (Exception e) {
                        Platform.runLater(() -> infoAlert(e.getMessage()));
                    }
                    return null;
                }
            };
        }
    };

    private final Service<Void> restoreService = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() {
                    try {
                        Thread.sleep(1000);
                        BackupService.restore(restorePath);
                        restored = true;
                    } catch (SQLException e) {
                        Platform.runLater(() -> infoAlert(e.getMessage()));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            };
        }
    };

    //------------------------helpers------------------
    private void pathSelector() throws SQLException {
        DirectoryChooser chooser = new DirectoryChooser();
        File selectedPath = chooser.showDialog(null);
        BackupService.insertPath(selectedPath.getAbsolutePath() + "/" + name.getText());
        listView.getItems().add(selectedPath.getAbsolutePath() + "/" + name.getText());
        pathBtn.setDisable(true);
    }

    private void restorePathConfirm() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*");
        fileChooser.getExtensionFilters().add(extFilter);


        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            restorePath = selectedFile.getAbsolutePath();
        }
    }

    @Override
    public void setActiveUser(Users activeUser) {
        super.setActiveUser(activeUser);
        if (!activeUser.getRole().equals("super_admin")) {
            restoreBtn.setDisable(true);
        }
    }

    private void openLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shipable/views/service/backup.fxml"));
        Stage stage = new Stage(StageStyle.UNDECORATED);
        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            errorMessage(ex.getMessage());
        }
    }

}
