package com.example.shipable.dao;


import com.example.shipable.entities.Users;
import com.example.shipable.helpers.CustomException;
import com.example.shipable.models.UserModel;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class UserService {
    private static final UserModel userModel;
    private static ObservableList<Users> users = null;

    static {
        userModel = new UserModel();
    }

    public static void insertUser(Users user) throws SQLException {
        try {
            if (user.getUsername().equalsIgnoreCase("ogleh")) {
                throw new CustomException("Username ka " + user.getUsername() + " horaa loo isticmalay");
            }
            userModel.insert(user);
        } catch (SQLException e) {
            if (e.getMessage().contains("(UNIQUE constraint failed: users.username")) {
                throw new CustomException("username ka " + user.getUsername() + " horaa lo isticmalay");
            } else if (e.getMessage().contains("(UNIQUE constraint failed: users.phone")) {
                throw new CustomException("lanbar ka " + user.getPhone() + " horaa lo isticmalay");
            } else {
                throw e;
            }
        }
    }

    public static void update(Users user) throws SQLException {
        try {
            userModel.update(user);
        } catch (SQLException e) {
            if (e.getMessage().contains("(UNIQUE constraint failed: users.username")) {
                throw new CustomException("username ka " + user.getUsername() + " horaa lo isticmalay");
            } else if (e.getMessage().contains("(UNIQUE constraint failed: users.phone")) {
                throw new CustomException("lanbar ka " + user.getPhone() + " horaa lo isticmalay");
            } else {
                throw e;
            }
        }
    }

    public static void delete(Users user) throws SQLException {
        try {
            userModel.delete(user);
        } catch (SQLException e) {
            throw new CustomException("Khalad ayaa ka dhacay " + e.getMessage());
        }
    }

    public static ObservableList<Users> users() throws SQLException {
        if (users == null) {
            users = userModel.fetch();
            users.add(new Users(0, "Mohamed", "Saeed", "4476619", "Male",
                    "Morning", "Ogleh", "4000", null, "super_admin"));
        }

        return users;
    }

    public static int predictNextId() throws SQLException {
        try {
            return (userModel.nextID() + 1);
        } catch (SQLException e) {
            throw new CustomException("Khalad caused predict nextId " + e.getMessage());
        }
    }
}
