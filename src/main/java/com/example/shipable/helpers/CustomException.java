package com.example.shipable.helpers;

import java.sql.SQLException;

public class CustomException extends SQLException {

    public CustomException(String message) {
        super(message);
    }

}
