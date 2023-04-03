package com.example.shipable.dao;


import com.example.shipable.entities.Box;
import com.example.shipable.helpers.CustomException;
import com.example.shipable.models.BoxModel;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class BoxService {
    private static ObservableList<Box> boxes;
    private static final BoxModel boxModel = new BoxModel();

    public static void insertBox(Box box) throws SQLException {
        try {
            boxModel.insert(box);
        } catch (Exception e) {
            throw new CustomException("Lama diwan gelin karo khanadan fadlan ku celi " + e.getMessage());
        }

    }

    public static void updateBox(Box box) throws SQLException {
        boxModel.update(box);
        box.setReady(!box.isReady());
//        int index = findBoxIndex(fetchBoxes(), box.getBoxId());
//        fetchBoxes().set(index, box);
    }

    public static void deleteBox(Box box) throws SQLException {
        try {
            if (!box.isReady()) {
                throw new CustomException("Khanadan macmiil ayaa isticmalaya hada \n" +
                        "Saaso tahay ma masaxi kartid");
            }
            boxModel.deleteBox(box);

        } catch (SQLException e) {
           throw  new CustomException(e.getMessage());
        }
    }

    public static ObservableList<Box> fetchBoxes() throws SQLException {
        System.out.println("Called fetch box");
        if (boxes == null) {
            System.out.println("init boxes");
            boxes = boxModel.fetchBoxes();
        }

        return boxes;
    }

    public static int nextBoxID() throws CustomException {
        try {
            return (1 + boxModel.predictNextId());
        } catch (SQLException e) {
            throw new CustomException("Khalad aya ka dhacay sahaminta ID ga xiga " + e.getMessage());
        }
    }

    public static int findBoxIndex(ObservableList<Box> boxes, int box_id) {
        int index = 0;
        for (int i = 0; i < boxes.size(); i++) {
            for (int j = 0; j < boxes.size(); j++) {
                if (box_id == boxes.get(i).getBoxId()) {
                    index = i;
                    break;
                }
            }
        }
        return index;

    }
}
