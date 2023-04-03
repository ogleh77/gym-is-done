package com.example.shipable.dao;

import com.example.shipable.helpers.CustomException;
import com.example.shipable.models.BackupModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class BackupService {
    private static final BackupModel backupModel = new BackupModel();
    private static ObservableList<String> paths;

    public static void insertPath(String path) throws CustomException {
        try {
            if (path == null) {
                throw new CustomException("Fadlan dooro meel aamin ah oo aad digan rabto backup kaga adigoo u bixinya " +
                        "magac aad ku garan karto");
            }

            backupModel.insertPath(path);

        } catch (SQLException e) {
            throw new CustomException(e.getMessage());
        }
    }
    public static void deletePath(String path) throws CustomException {
        try {
            if (path == null) {
                throw new CustomException("Fadlan dooro path-ka aad delete garayn rabtid");
            }

            backupModel.deletePath(path);

        } catch (SQLException e) {
            throw new CustomException(e.getMessage());
        }
    }
    public static void backup(String path) throws CustomException {
        try {
            if (path == null) {
                throw new CustomException("Fadlan kasoo dooro listka sare path ka aad dhigayso backup kaga" +
                        " hadii location-ku ku jirin list ka sare taabo button ka PATH si aad u samayso");
            }
            backupModel.backUp(path);
        } catch (SQLException e) {
            throw new CustomException(e.getMessage());
        }
    }

    public static void restore(String path) throws CustomException {
        try {
            if (path == null) {
                throw new CustomException("Fadlan marka ka dooro list-ka sare location-ka backup kagu kugu kaydsan yahay" +
                        " Si aad uga soo restore garayso");
            }
            backupModel.restore(path);
        } catch (SQLException e) {
            throw new CustomException(e.getMessage());
        }
    }

    public static ObservableList<String> backupPaths() throws SQLException {
        if (paths == null) {
            paths = FXCollections.observableArrayList();
            paths = backupModel.backupPaths();
        }
        return paths;
    }

}
