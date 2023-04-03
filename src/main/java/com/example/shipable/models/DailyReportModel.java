package com.example.shipable.models;



import com.example.shipable.entities.DailyReport;
import com.example.shipable.helpers.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DailyReportModel {
    private static final LocalDate today = LocalDate.now();

    private static final Connection connection = DbConnection.getConnection();

    public static ObservableList<DailyReport> getPaymentsBetween(LocalDate start, LocalDate end) throws SQLException, InterruptedException {
        ObservableList<DailyReport> reports = FXCollections.observableArrayList();
        DailyReport dailyReport = null;
        String sql = "SELECT * FROM daily_report WHERE report_date between " +
                "'" + start + "' AND '" + end + "'";

        statement(reports, sql);
        Thread.sleep(2000);
        return reports;
    }

    private static void statement(ObservableList<DailyReport> reports, String sql) throws SQLException {
        DailyReport dailyReport;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            dailyReport = new DailyReport(
                    rs.getString(1), rs.getInt(2),
                    rs.getInt(3), rs.getInt(4), rs.getInt(5));
            reports.add(dailyReport);
        }
        statement.close();
        rs.close();
    }

    public static ObservableList<DailyReport> getWeeklyPayments(LocalDate today) throws SQLException {
        ObservableList<DailyReport> dailyReports = FXCollections.observableArrayList();
        DailyReport dailyReport = null;
        String sql = "SELECT * FROM daily_report WHERE report_date between " +
                "'" + today.minusDays(7) + "' AND '" + today + "'";
        statement(dailyReports, sql);
        return dailyReports;
    }

    public static void dailyReportMaleWithBox(Statement st) throws SQLException {

        if (st.executeUpdate("UPDATE daily_report SET registration=(registration+1),male=(male+1),vip_box=(vip_box+1) " + "WHERE report_date ='" + today + "'") > 0) {
            System.out.println("Updated male with box....");

        } else {
            String query = "INSERT INTO daily_report(report_date,registration,male,vip_box)VALUES ('" + today + "',1,1,1)";
            st.executeUpdate(query);
            System.out.println("Saved male with box....");
        }
    }


    public static void dailyReportFemaleWithBox(Statement st) throws SQLException {

        if (st.executeUpdate("UPDATE daily_report SET registration=(registration+1),female=(female+1),vip_box=(vip_box+1) " + "WHERE report_date ='" + today + "'") > 0) {
            System.out.println("Updated Female with box....");

        } else {
            String query = "INSERT INTO daily_report(registration,female,vip_box)VALUES (1,1,1)";
            st.executeUpdate(query);
            System.out.println("Saved Female with box....");

        }
    }


    public static void dailyReportMaleWithOutBox(Statement st) throws SQLException {

        if (st.executeUpdate("UPDATE daily_report SET registration=(registration+1),male=(male+1) WHERE report_date='" + today + "'") > 0) {
            System.out.println("Updated male with out box....");

        } else {
            String query = "INSERT INTO daily_report(registration,male)VALUES (1,1)";

            st.executeUpdate(query);
            System.out.println("Saved male with out box....");

        }
    }


    public static void dailyReportFemaleWithOutBox(Statement st) throws SQLException {
        if (st.executeUpdate("UPDATE daily_report SET registration=(registration+1),female=(female+1)" + "WHERE report_date ='" + today + "'") > 0) {
            System.out.println("Updated female with out box....");
        } else {
            String query = "INSERT INTO daily_report(registration,female)VALUES (1,1)";
            st.executeUpdate(query);

            System.out.println("Saved female with out box....");

        }
    }
}
