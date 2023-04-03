package com.example.shipable.entities;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Box {
    private int boxId;
    private final SimpleStringProperty boxName = new SimpleStringProperty();
    private final SimpleBooleanProperty ready = new SimpleBooleanProperty();

    public Box(int boxId, String boxName, boolean ready) {
        this.boxId = boxId;
        this.setBoxName(boxName);
        this.setReady(ready);
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public String getBoxName() {
        return boxName.get();
    }

    public SimpleStringProperty boxNameProperty() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName.set(boxName);
    }

    public boolean isReady() {
        return ready.get();
    }

    public SimpleBooleanProperty readyProperty() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready.set(ready);
    }

    @Override
    public String toString() {
        return boxName.get();
    }
}
