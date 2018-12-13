package com.austinramsay.gui;

import com.austinramsay.controller.ReportListener;
import com.austinramsay.model.Filters;
import com.austinramsay.timekeeper.PayPeriod;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;

public class ReportViewer {


    private ReportListener reportListener;
    private Stage report_window = new Stage();
    private ListView<PayPeriod> payPeriodsList = new ListView<>();
    private ListView<String> actions_list;
    private ListView<String> hours_list;
    private Label selected_pp_label = new Label();
    private Label pp_hours_label = new Label();
    private Label daily_hours_label = new Label();
    private Label weekly_hours_label = new Label();


    /**
     * Set the request listener for the Report Viewer.
     * It is necessary to set a listener so the Viewer can fetch required information.
     * @param reportListener ReportListener with implemented functions to handle Viewer requests
     */
    public void setReportListener(ReportListener reportListener) {
        this.reportListener = reportListener;
    }


    /**
     * Build a report stage for a selected employee.
     * The report will display information about employee actions along with a corresponding date/time of the action. (Iteration of action log)
     * It will contain information regarding pay period information times for all pay periods of the selected time range. (Iteration of pay period list)
     * Weekly and daily information will be available as well. (Using search function to filter hours log)
     */
    public void displayReport() {

        // Check for request handler - necessary for requesting vital information
        if (reportListener == null) {
            Platform.runLater(() -> {
                Alert.display("Report Viewer missing request handler component.");
            });
            return;
        }


        /*
        // Create the pay periods list view
        // This will be our main filter for sorting information in the report viewer
        // The information displayed will be dependant upon the pay period selected
         */

        // Create cell factory for pay periods list view
        payPeriodsList.setCellFactory(new Callback<ListView<PayPeriod>, ListCell<PayPeriod>>() {
            @Override
            public ListCell<PayPeriod> call(ListView<PayPeriod> param)
            {
                ListCell<PayPeriod> cell = new ListCell<PayPeriod>() {
                    @Override
                    protected void updateItem(PayPeriod payperiod, boolean empty)
                    {
                        // Verify pay period not null
                        if (payperiod != null)
                        {
                            super.updateItem(payperiod, empty);

                            // Set text of this cell to the formatted date of the respective pay period
                            this.setText(payperiod.getFormattedDate());

                            // Set height of cell
                            this.setPrefHeight(30);
                        }
                        else // No pay period information to display -> set cell to empty
                        {
                            super.updateItem(null, empty);
                            this.setText(null);
                        }
                    }
                };
                return cell;
            }
        } ); // End pay periods list view cell factory


        // Define change listener for pay period selection model
        payPeriodsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PayPeriod>() {
            @Override
            public void changed(ObservableValue<? extends PayPeriod> observable, PayPeriod oldValue, PayPeriod newValue) {
                if (payPeriodsList.getSelectionModel().isEmpty()) {
                    reportListener.fireClearPayPeriodDependents();
                } else {
                    PayPeriod selectedPayPeriod = payPeriodsList.getSelectionModel().getSelectedItem();
                    reportListener.firePayPeriodSelected(selectedPayPeriod);
                }
            }
        });


        // Set pay period list view size
        payPeriodsList.setPrefHeight(350);
        payPeriodsList.setPrefWidth(225);


        // Create the action log list view
        actions_list = new ListView<>();
        actions_list.setPrefHeight(350);
        actions_list.setPrefWidth(225);


        // Create the hours log list view
        hours_list = new ListView<>();
        hours_list.setPrefHeight(350);
        hours_list.setPrefWidth(225);


        // Create a VBox with corresponding labels for each list view
        VBox pp_box = new VBox(10); // Pay period box
        VBox al_box = new VBox(10); // Action log box
        VBox hl_box = new VBox(10); // Hours log box


        pp_box.getChildren().addAll(
                new Label("Pay Periods:"), // Add a label on top
                payPeriodsList                // Add the pay periods list view
        );

        al_box.getChildren().addAll(
                new Label("Action Log:"), // Add a label on top
                actions_list                  // Add the actions list view
        );

        hl_box.getChildren().addAll(
                new Label("Hours Clocked Log:"), // Add a label on top
                hours_list                           // Add the hours list view
        );


        // Create VBox to house labels
        VBox labels_container = new VBox(12);
        labels_container.getChildren().addAll(
                selected_pp_label,
                pp_hours_label,
                weekly_hours_label,
                daily_hours_label
        );


        // Create buttons to close report, clear filters
        Button clear = new Button("Clear Selection");
        Button correction = new Button("Submit Correction");
        Button close = new Button("Close Report");


        // Close report viewer on 'Close' button
        close.setOnAction(e -> {
            report_window.close();
        });


        // Create correction request
        correction.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get selected employee ID number
                int employeeId = reportListener.getSelectedEmployee().getEmployeeID();

                // Request correction box with corresponding employee ID
                reportListener.displayCorrectionBox(employeeId);
            }
        });


        // Clear pay period selection on 'Clear' button
        clear.setOnAction(e -> {
            payPeriodsList.getSelectionModel().clearSelection();
            reportListener.fireClearPayPeriodDependents();
        });


        // Create HBox for buttons
        HBox button_box = new HBox(12);
        button_box.setAlignment(Pos.CENTER);
        HBox.setHgrow(button_box, Priority.ALWAYS);
        button_box.getChildren().addAll(correction, clear, close);


        // Create an HBox to house labels, and button box
        HBox lower_root = new HBox();
        lower_root.getChildren().addAll(labels_container, button_box);


        // Create an HBox to house list views
        HBox listview_container = new HBox(15);
        listview_container.getChildren().addAll(
                pp_box,
                al_box,
                hl_box
        );


        // Create root container
        VBox root = new VBox(12);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
                listview_container,
                lower_root
        );


        // Populate pay period dependents with ALL entries
        reportListener.fireClearPayPeriodDependents();


        // Build scene and stage window
        Scene report_scene = new Scene(root);
        report_window.setScene(report_scene);
        report_window.show();
    }


    /**
     * Update the report viewer title to correspond with selected employee.
     * @param employeeName name of employee corresponding with report
     */
    public void setTitle(String employeeName) {
        report_window.setTitle(String.format("%s's Employee Report", employeeName));
    }


    /**
     * Set the pay periods referenced from the respective employee.
     * Argument should be referenced from employee.getPayPeriods();
     * @param payPeriods observable pay periods list
     */
    public void setPayPeriodsList(ObservableList<PayPeriod> payPeriods) {
        payPeriodsList.setItems(payPeriods);
    }


    /**
     * Update GUI labels with employee specific hours clocked information.
     * Arguments should be referenced from employee time tracker.
     * No reason to update these labels other than initially opening the report viewer.
     * @param daily_hours hours clocked for today
     * @param weekly_hours hours clocked for this week
     */
    public void updateHoursLabels(double daily_hours, double weekly_hours) {
        daily_hours_label.setText("Daily Hours Clocked: " + Filters.round(daily_hours));
        weekly_hours_label.setText("Weekly Hours Clocked: " + Filters.round(weekly_hours));
    }


    /**
     * Update GUI selected pay period hours label.
     * Argument should be referenced from pay period selected in the Report Viewer's pay period list view.
     * This should be called each time a user selects a different pay period in that list view.
     * @param date pay period start and end date formatted such as xx/xx/xxxx - xx/xx/xxxx
     * @param pay_period_hours selected pay period hours (referenced from payPeriod.getTotalHours())
     */
    public void updatePayPeriodHoursLabel(String date, double pay_period_hours) {
        selected_pp_label.setText("Selected Pay Period: " + date);
        pp_hours_label.setText("Selected Pay Period Hours: " + Filters.round(pay_period_hours));
    }



    public void updatePayPeriodDependents(ObservableList<String> actionEntries, ObservableList<String> hourEntries) {
        // Filter Actions
        actions_list.setItems(actionEntries);
        actions_list.refresh();

        // Filter Hours
        hours_list.setItems(hourEntries);
        hours_list.refresh();
    }
}
