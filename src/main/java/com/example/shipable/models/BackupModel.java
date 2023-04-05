package com.example.shipable.models;


import com.example.shipable.helpers.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BackupModel {
    private static final Connection connection = DbConnection.getConnection();
    private static final ObservableList<String> paths = FXCollections.observableArrayList();


    public void insertPath(String path) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO backup_table(location) VALUES('" + path + "')");
    }

    public void deletePath(String path) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM backup_table WHERE location='" + path + "'");
    }

    public ObservableList<String> backupPaths() throws SQLException {

        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM backup_table;");

        while (rs.next()) {
            paths.add(rs.getString("location"));
        }
        rs.close();
        statement.close();
        return paths;
    }

    public void backUp(String path) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "BACKUP to " + path;
        statement.executeUpdate(query);
    }

    public void restore(String path) throws SQLException {
        String query = "RESTORE FROM " + path;
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }
}
