package com.example.shipable.entities;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.Period;

public class Payments {
    private final int paymentID;
    private final SimpleStringProperty paymentDate = new SimpleStringProperty();
    private LocalDate expDate;
    private final SimpleStringProperty month = new SimpleStringProperty();
    private final SimpleStringProperty year = new SimpleStringProperty();
    private final SimpleDoubleProperty amountPaid = new SimpleDoubleProperty();
    private final SimpleStringProperty paidBy = new SimpleStringProperty();
    private final SimpleDoubleProperty discount = new SimpleDoubleProperty();
    private final SimpleBooleanProperty poxing = new SimpleBooleanProperty();
    private Box box;
    private final SimpleBooleanProperty online = new SimpleBooleanProperty();
    private final SimpleBooleanProperty pending = new SimpleBooleanProperty();
    private final JFXButton pendingBtn;
    private int daysRemind=0;

    public Payments(int paymentID, String paymentDate, LocalDate expDate, String month, String year, double amountPaid, String paidBy, double discount, boolean poxing, String customerFK, boolean online, boolean pending) {
        this.paymentID = paymentID;
        this.setPaymentDate(paymentDate);
        this.expDate = expDate;
        this.setMonth(month);
        this.setYear(year);
        this.setAmountPaid(amountPaid);
        this.setPaidBy(paidBy);
        this.setDiscount(discount);
        this.setPoxing(poxing);
        this.setOnline(online);
        this.setPending(pending);
        this.pendingBtn = new JFXButton("Haki");
        String unPendStyle = "-fx-background-color: red;-fx-text-fill: white;-fx-font-family:Verdana;-fx-pref-width: 100;-fx-font-size: 15";
        String pendStyle = "-fx-background-color: #afd6e3;-fx-text-fill: black;-fx-font-family:Verdana;-fx-pref-width: 100;-fx-font-size: 15";
        pendingBtn.setStyle(pendStyle);
        if (!this.online.get() && !this.pending.get()) {
            this.pendingBtn.setText("----");
            this.pendingBtn.setDisable(true);
            this.pendingBtn.setStyle(unPendStyle);
        } else if (this.isPending() && !this.isOnline()) {
            this.getPendingBtn().setStyle(unPendStyle);
            this.getPendingBtn().setText("Fur");
        }
    }

    public int getPaymentID() {
        return paymentID;
    }

    public String getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public LocalDate getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDate expDate) {
        this.expDate = expDate;
    }

    public String getMonth() {
        return month.get();
    }

    public void setMonth(String month) {
        this.month.set(month);
    }
    public void setYear(String year) {
        this.year.set(year);
    }

    public double getAmountPaid() {
        return amountPaid.get();
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid.set(amountPaid);
    }

    public String getPaidBy() {
        return paidBy.get();
    }

    public void setPaidBy(String paidBy) {
        this.paidBy.set(paidBy);
    }

    public double getDiscount() {
        return discount.get();
    }

    public void setDiscount(double discount) {
        this.discount.set(discount);
    }

    public boolean isPoxing() {
        return poxing.get();
    }
    public void setPoxing(boolean poxing) {
        this.poxing.set(poxing);
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }
    public boolean isOnline() {
        return online.get();
    }

    public void setOnline(boolean online) {
        this.online.set(online);
    }

    public boolean isPending() {
        return pending.get();
    }

    public void setPending(boolean pending) {
        this.pending.set(pending);
    }

    public JFXButton getPendingBtn() {
        return pendingBtn;
    }

    public int getDaysRemind() {
        if (expDate.isAfter(LocalDate.now()))
            daysRemind = Period.between(LocalDate.now(), expDate).getDays();
        return daysRemind;
    }

}
