package com.example.shipable.entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Gym {
    private int gymId;
    private final SimpleStringProperty gymName = new SimpleStringProperty();
    private final SimpleIntegerProperty zaad = new SimpleIntegerProperty();
    private final SimpleIntegerProperty eDahab = new SimpleIntegerProperty();
    private final SimpleDoubleProperty fitnessCost = new SimpleDoubleProperty();
    private final SimpleDoubleProperty poxingCost = new SimpleDoubleProperty();
    private final SimpleDoubleProperty boxCost = new SimpleDoubleProperty();
    private final SimpleIntegerProperty pendingDate = new SimpleIntegerProperty();
    private final SimpleDoubleProperty maxDiscount = new SimpleDoubleProperty();
    private final ObservableList<Box> vipBoxes;

    public Gym(int gymId, String gymName, int zaad, int eDahab, double fitnessCost, double poxingCost, double boxCost,
               int pendingDate, double maxDiscount) {
        this.gymId = gymId;
        this.setGymName(gymName);
        this.setZaad(zaad);
        this.seteDahab(eDahab);
        this.setFitnessCost(fitnessCost);
        this.setPoxingCost(poxingCost);
        this.setBoxCost(boxCost);
        this.setPendingDate(pendingDate);
        this.setMaxDiscount(maxDiscount);
        this.vipBoxes = FXCollections.observableArrayList();
    }

    public int getGymId() {
        return gymId;
    }

    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    public String getGymName() {
        return gymName.get();
    }

    public SimpleStringProperty gymNameProperty() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName.set(gymName);
    }

    public int getZaad() {
        return zaad.get();
    }

    public SimpleIntegerProperty zaadProperty() {
        return zaad;
    }

    public void setZaad(int zaad) {
        this.zaad.set(zaad);
    }

    public int geteDahab() {
        return eDahab.get();
    }

    public SimpleIntegerProperty eDahabProperty() {
        return eDahab;
    }

    public void seteDahab(int eDahab) {
        this.eDahab.set(eDahab);
    }

    public double getFitnessCost() {
        return fitnessCost.get();
    }

    public SimpleDoubleProperty fitnessCostProperty() {
        return fitnessCost;
    }

    public void setFitnessCost(double fitnessCost) {
        this.fitnessCost.set(fitnessCost);
    }

    public double getPoxingCost() {
        return poxingCost.get();
    }

    public SimpleDoubleProperty poxingCostProperty() {
        return poxingCost;
    }

    public void setPoxingCost(double poxingCost) {
        this.poxingCost.set(poxingCost);
    }

    public double getBoxCost() {
        return boxCost.get();
    }

    public SimpleDoubleProperty boxCostProperty() {
        return boxCost;
    }

    public void setBoxCost(double boxCost) {
        this.boxCost.set(boxCost);
    }

    public int getPendingDate() {
        return pendingDate.get();
    }

    public SimpleIntegerProperty pendingDateProperty() {
        return pendingDate;
    }

    public void setPendingDate(int pendingDate) {
        this.pendingDate.set(pendingDate);
    }

    public double getMaxDiscount() {
        return maxDiscount.get();
    }

    public SimpleDoubleProperty maxDiscountProperty() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount.set(maxDiscount);
    }

    public ObservableList<Box> getVipBoxes() {
        return vipBoxes;
    }

    @Override
    public String toString() {
        return "Gym{" +
                "gymId=" + gymId +
                ", gymName='" + gymName + '\'' +
                ", zaad='" + zaad + '\'' +
                ", eDahab='" + eDahab + '\'' +
                ", fitnessCost=" + fitnessCost +
                ", poxingCost=" + poxingCost +
                ", boxCost=" + boxCost +
                ", pendingDate=" + pendingDate +
                ", maxDiscount=" + maxDiscount +
                ", vipBoxes=" + vipBoxes +
                '}';
    }
}
