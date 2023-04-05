package com.example.shipable.controllers.info;


import com.example.shipable.entities.DailyReport;
import com.example.shipable.helpers.CommonClass;
import com.example.shipable.models.DailyReportModel;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DailyReportController extends CommonClass implements Initializable {

    @FXML
    private TableColumn<DailyReport, String> dailyReportDay;
    @FXML
    private TableColumn<DailyReport, Integer> totalFemale;

    @FXML
    private TableColumn<DailyReport, Integer> totalMale;

    @FXML
    private TableColumn<DailyReport, Integer> totalRegister;

    @FXML
    private TableColumn<DailyReport, Integer> totalVipBox;

    @FXML
    private TableView<DailyReport> dailyTbView;
    //---------------------Generate report-------------–

    @FXML
    private TableView<DailyReport> reportTbView;

    @FXML
    private TableColumn<DailyReport, Integer> reportTotalFemale;

    @FXML
    private TableColumn<DailyReport, Integer> reportTotalMale;

    @FXML
    private TableColumn<DailyReport, Integer> reportTotalReg;

    @FXML
    private TableColumn<DailyReport, Integer> reportTotalVip;

    @FXML
    private TableColumn<DailyReport, String> reportTotalDay;

    @FXML
    private DatePicker endDate;

    @FXML
    private ImageView imgViewSearch;


    @FXML
    private JFXButton searchBtn;

    @FXML
    private DatePicker startDate;
    private ObservableList<DailyReport> reports;
    private final ObservableList<DailyReport> weeklyReport;
    private final URL url = getClass().getResource("/com/example/shipable/style/icons/icons8-search-50.png");

    public DailyReportController() throws SQLException {
        weeklyReport = DailyReportModel.getWeeklyPayments(LocalDate.now());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                initTable();
                if (reports == null) {
                    Label label = new Label("RAADI WAKHTIGA AAD U BAHANTAY");
                    reportTbView.setPlaceholder(label);
                }
                service.setOnSucceeded(e -> {
                    System.out.println(reports);
                    Image image = new Image(String.valueOf(url));

                    imgViewSearch.setImage(image);
                    searchBtn.setGraphic(imgViewSearch);

                    try {
                        generateTable();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initTable() throws SQLException {
        tableFields(dailyReportDay, totalRegister, totalMale, totalFemale, totalVipBox);
        getMandatoryFields().addAll(startDate, endDate);
        if (weeklyReport.isEmpty()) {
            Label label = new Label("MA JIRO WAX REPORT AH OO U WIIGAN ");
            dailyTbView.setPlaceholder(label);
        }
        dailyTbView.setItems(DailyReportModel.getWeeklyPayments(LocalDate.now()));
    }

    private void generateTable() throws SQLException {
        tableFields(reportTotalDay, reportTotalReg, reportTotalMale, reportTotalFemale, reportTotalVip);
        if (reports.isEmpty()) {
            Label label = new Label("MA JIRO WAX REPORT AH OO U DHAXAYA " + startDate.getValue() + " ILaa " + endDate.getValue());
            reportTbView.setPlaceholder(label);
            return;
        }
        reportTbView.setItems(reports);
        reportTbView.refresh();

    }

    private void tableFields(TableColumn<DailyReport, String> reportTotalDay,
                             TableColumn<DailyReport, Integer> reportTotalReg,
                             TableColumn<DailyReport, Integer> reportTotalMale,
                             TableColumn<DailyReport, Integer> reportTotalFemale,
                             TableColumn<DailyReport, Integer> reportTotalVip) {
        reportTotalDay.setCellValueFactory(new PropertyValueFactory<>("day"));
        reportTotalReg.setCellValueFactory(new PropertyValueFactory<>("registrations"));
        reportTotalMale.setCellValueFactory(new PropertyValueFactory<>("male"));
        reportTotalFemale.setCellValueFactory(new PropertyValueFactory<>("female"));
        reportTotalVip.setCellValueFactory(new PropertyValueFactory<>("vipBox"));
    }

    @FXML
    void searchHandler() {
        if (isValid(getMandatoryFields(), null)) {
            if (start) {
                service.restart();
                searchBtn.setGraphic(getLoadingImageView());
            } else {
                service.start();
                searchBtn.setGraphic(getLoadingImageView());
                start = true;
            }

        }
    }


    private final Service<Void> service = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    reports = DailyReportModel.getPaymentsBetween(startDate.getValue(), endDate.getValue());
                    return null;
                }
            };
        }
    };
}
