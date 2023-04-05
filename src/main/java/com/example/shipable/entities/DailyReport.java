package com.example.shipable.entities;

public class DailyReport {
    private final int male;
    private final int female;

    public DailyReport(String day, int registrations, int male, int female, int vipBox) {
        this.male = male;
        this.female = female;
    }
    public int getMale() {
        return male;
    }

    public int getFemale() {
        return female;
    }
}
