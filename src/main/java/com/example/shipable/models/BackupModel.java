package com.example.shipable.models;


import com.example.shipable.helpers.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class BackupModel {
    private static final Connection connection = DbConnection.getConnection();
    private static final ObservableList<String> paths = FXCollections.observableArrayList();


    public void insertPath(String path) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO backup_table(location) VALUES('" + path + "')");
    }

    public ObservableList<String> backupPaths() throws SQLException {

        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM backup_table;");

        while (rs.next()) {
            paths.add(rs.getString("location"));
            paths.add(rs.getString("last_backup"));
        }
        rs.close();
        statement.close();
        return paths;
    }

    public void backUp(String path) throws SQLException {
        Statement statement = connection.createStatement();
        connection.setAutoCommit(false);
        try {
            String query = "BACKUP to " + path;
            String lastBackup = "UPDATE backup_table SET last_backup='" + LocalDateTime.now() +
                    "' WHERE location='" + path + "'";
            statement.executeUpdate(query);
            statement.executeUpdate(lastBackup);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        }

    }

    public void restore(String path) throws SQLException {
        String query = "RESTORE FROM " + path;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }
}
