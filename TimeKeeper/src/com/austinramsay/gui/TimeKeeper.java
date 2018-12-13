package com.austinramsay.gui;

import com.austinramsay.controller.CorrectionRequestHandler;
import com.austinramsay.controller.ReportListener;
import com.austinramsay.networking.RequestWorker;
import com.austinramsay.model.Filters;
import com.austinramsay.timekeeper.*;
import java.util.*;
import java.util.function.Predicate;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * This class represents the main structure of the Time Keeper
 * Time Keeper class maintains all main frame
 * @author austinramsay
 */
public class TimeKeeper extends Application {
   

    private RequestWorker worker;


    @Override
    public void start(Stage primaryStage) 
    {
        /*
        // Create a request worker
        // Give request worker access to main TimeKeeper by passing in this class
         */
        this.worker = new RequestWorker();


        // Create the user interface
        buildInterface(primaryStage);
    }

    
    /*
    // Build the GUI nodes
    // status_display = 'clocked in' OR 'clocked out'
    // All time labels such as ***_display are to be updated real time
    // Example: hours_today_display should be set upon clicking employee name
    */


    private Stage primaryStage;
    private final Label search_label = new Label("Search:");
    private final Label info_label = new Label("Employee Information");
    private final Label status_label = new Label("Status:");
    private final Label status_display = new Label();
    private final Label hours_today_label = new Label("Hours Today:");
    private final Label hours_today_display = new Label();
    private final Label hours_week_label = new Label("Weekly Hours:");
    private final Label hours_week_display = new Label();
    private final Label hours_payperiod_label = new Label("Pay Period Hours:");
    private final Label hours_payperiod_display = new Label();
    private final Button clockout = new Button("Clock Out");
    private final Button clockin = new Button("Clock In");
    private final Button getReport = new Button("Show Report");
    private final Button refresh = new Button("Refresh");
    private final TextField searchinput = new TextField();
    private ListView<Employee> employees = new ListView<>();
    private FilteredList<Employee> filtered;
    private SortedList<Employee> sorted;


    /**
     * Build the user interface
     * @param primaryStage JavaFX main stage
     */
    private void buildInterface(Stage primaryStage)
    {
        // Set the primaryStage of the class to be used in other methods
        this.primaryStage = primaryStage;
        
        
        /*
        // Set styling for static labels
        */
        info_label.getStyleClass().add("main-label");
        status_label.getStyleClass().add("main-label");
        hours_today_label.getStyleClass().add("main-label");
        hours_week_label.getStyleClass().add("main-label");
        hours_payperiod_label.getStyleClass().add("main-label");
        
        
        /*
        // Disable buttons until an employee is selected
        */
        getReport.setDisable(true);
        clockin.setDisable(true);
        clockout.setDisable(true);
        
        
        /*
        // Set the buttons logic
        */

        // Clock In
        clockin.setOnAction(e -> {
            // Get the selected employee to get necessary identifying information
            int employee_id = employees.getSelectionModel().getSelectedItem().getEmployeeID();

            // Create and send a request to the server
            if (worker.requestAction(employee_id, EmployeeAction.CLOCKIN)) {
                // Request succeeded, update employees
                updateEmployeeList(requestEmployees());
            } else {
                // Request failed to complete
                Alert.display("The server failed to process the request.");
            }
        });


        // Clock out
        clockout.setOnAction(e -> {
            // Get the selected employee to get necessary identifying information
            int employee_id = employees.getSelectionModel().getSelectedItem().getEmployeeID();

            // Create and send a request to the server
            if (worker.requestAction(employee_id, EmployeeAction.CLOCKOUT)) {
                // Request succeeded, update employees
                updateEmployeeList(requestEmployees());
            } else {
                // Request failed to complete
                Alert.display("The server failed to process the request.");
            }
        });


        refresh.setOnAction(e -> {

            // Get a fresh employee list
            updateEmployeeList(requestEmployees());

        });


        // Show Report button -> displayReport();
        // Display a list of actions done by employee along with information of pay period hours and weekly information, etc.
        getReport.setOnAction(e -> {

            Employee employee = employees.getSelectionModel().getSelectedItem();

            ReportViewer reportView = new ReportViewer();

            // Create listener to perform Report Viewer actions
            ReportListener reportListener = new ReportListener() {

                @Override
                public Employee getSelectedEmployee() {
                    return employee;
                }


                @Override
                public void firePayPeriodSelected(PayPeriod payPeriod) {
                    ObservableList<String> actionEntries = Filters.getActionEntries(payPeriod.getStartDate(), payPeriod.getEndDate(), getSelectedEmployee().getTracker().getActionLog());
                    ObservableList<String> hoursEntries = Filters.getHoursEntries(payPeriod.getStartDate(), payPeriod.getEndDate(), getSelectedEmployee().getTracker().getHoursLog());

                    reportView.updatePayPeriodDependents(actionEntries, hoursEntries);
                    reportView.updatePayPeriodHoursLabel(payPeriod.getFormattedDate(), payPeriod.getTotalHours());
                }


                @Override
                public void fireClearPayPeriodDependents() {
                    ObservableList<String> actionEntries = Filters.getActionEntries(null, null, employee.getTracker().getActionLog());
                    ObservableList<String> hoursEntries = Filters.getHoursEntries(null, null, employee.getTracker().getHoursLog());

                    reportView.updatePayPeriodDependents(actionEntries, hoursEntries);
                    reportView.updatePayPeriodHoursLabel("None Selected", 0);
                }


                @Override
                public void displayCorrectionBox(int employeeId) {
                    buildCorrectionBox(employeeId);
                }
            };

            // Define listener before attempting to open display
            reportView.setReportListener(reportListener);

            // Set Report Viewer title
            reportView.setTitle(employee.getName());

            // Populate pay periods list
            reportView.setPayPeriodsList(FXCollections.observableArrayList(employee.getPayPeriods()));

            //Set Report Viewer labels
            double daily_hours = employee.getTracker().getHours(TimeInterval.TODAY);
            double weekly_hours = employee.getTracker().getHours(TimeInterval.WEEKLY);

            reportView.updatePayPeriodHoursLabel("None Selected", 0);
            reportView.updateHoursLabels(daily_hours, weekly_hours);

            // Display the Report Viewer
            reportView.displayReport();

        });


        /*
        // At the top of the application, there should be a search box with a label
        // Use HBox with center alignment
        // Search box: searchinput
        // Search label: search_label
        */
        HBox search_field = new HBox(13);
        search_field.setAlignment(Pos.CENTER);
        search_field.getChildren().addAll(search_label, searchinput);


        /*
        // Apply a text property listener to the search input field
        // We will apply a predicate to the filtered list of employees after each key stroke
         */
        searchinput.textProperty().addListener(obs -> {

            // Get the name of the user to be searched from our input field
            String filter = searchinput.getText();

            filtered.setPredicate(testForUser(filter));

        });


        // Create the employees list view
        employees.setCellFactory(new Callback<ListView<Employee>, ListCell<Employee>>() {
            @Override
            public ListCell<Employee> call(ListView<Employee> param) 
            {  
                ListCell<Employee> cell = new ListCell<Employee>() {
                    @Override
                    protected void updateItem(Employee employee, boolean empty)
                    {
                        
                        if (employee != null)
                        {
                            super.updateItem(employee, empty);
                            this.setText(employee.getName());
                            this.setPrefHeight(30);
                        }
                        else
                        {
                            super.updateItem(null, empty);
                            this.setText(null);
                        }
                    } 
                };
                return cell;
            }
        } );
        employees.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                if (newValue == null) {
                    setEmployee(null);
                    employees.getSelectionModel().clearSelection();
                } else {
                    setEmployee(newValue);
                }
            }
        });
        employees.setPrefHeight(270);
        employees.setPrefWidth(300);


        /*
        // Populate the list view by pulling a full list of employees from the server
        // updateEmployeeList(employees) will set the item list
         */
        updateEmployeeList(requestEmployees());
        
        
        /*
        // Create a grid for employee information
        // Constraints - col, row
        */
        GridPane infogrid = new GridPane();
        GridPane.setConstraints(status_label, 0, 0);
        GridPane.setConstraints(status_display, 1, 0);
        GridPane.setConstraints(hours_today_label, 0, 1);
        GridPane.setConstraints(hours_today_display, 1, 1);
        GridPane.setConstraints(hours_week_label, 0, 2);
        GridPane.setConstraints(hours_week_display, 1, 2);
        GridPane.setConstraints(hours_payperiod_label, 0, 3);
        GridPane.setConstraints(hours_payperiod_display, 1, 3);
        infogrid.setHgap(14);
        infogrid.setVgap(14);
        infogrid.getChildren().addAll(
                status_label,
                status_display,
                hours_today_label,
                hours_today_display,
                hours_week_label,
                hours_week_display,
                hours_payperiod_label,
                hours_payperiod_display
        );
        
        
        /*
        // Create lower button box to house 'Clock In' and 'Clock Out' 
        */
        HBox buttonbox = new HBox(14);
        buttonbox.setAlignment(Pos.CENTER);
        buttonbox.getChildren().addAll(getReport, clockin, clockout);


        HBox refresh_box = new HBox();
        refresh_box.setAlignment(Pos.CENTER);
        refresh_box.getChildren().addAll(refresh);


        /*
        // Create root layout (VBox center alignment)
        */
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(search_field, employees, refresh_box, info_label, infogrid, buttonbox);
        root.setPadding(new Insets(10));
        
        
        /*
        // Create scene
        */
        Scene main = new Scene(root);
        main.getStylesheets().add(this.getClass().getResource("timekeeper.css").toExternalForm());

        
        /*
        // Pack the main stage together and display
        */
        primaryStage.setScene(main);
        primaryStage.setTitle("Time Keeper");
        primaryStage.show();
    }  // End buildInterface() method


    /**
     * Create stage for user to submit a correction request.
     * On submit, packs a request and sends to the server for processing.
     * @param employeeId
     */
    private void buildCorrectionBox(int employeeId) {
        CorrectionBox correctionBox = new CorrectionBox(employeeId);
        correctionBox.setRequestHandler(new CorrectionRequestHandler() {
            @Override
            public void sendCorrection(CorrectionRequest correction) {

                // Send correction through networker
                worker.sendCorrection(correction);

            }
        });
        correctionBox.show();
    }


    /**
     * Update the GUI values according to newly selected employee information
     * @param employee The newly selected employee
     */
    private void setEmployee(Employee employee)
    {
        /*
        // Verify employee is not null
        // If it is, set all fields to blank and disable buttons
        */
        if (employee == null)
        {
            getReport.setDisable(true);
            clockin.setDisable(true);
            clockout.setDisable(true);
            status_display.setText(null);
            hours_today_display.setText(null);
            hours_week_display.setText(null);
            hours_payperiod_display.setText(null);
            return;
        }
        
        
        // An employee is selected, allow them to view their report
        getReport.setDisable(false);
        
        
        /*
        // Check if the employee is clocked in
        // Set button state according to employee status
        */
        if (employee.isClockedIn())
        {
            // Disable the clock in button
            // Enable the clock out button
            clockin.setDisable(true);
            clockout.setDisable(false);
            status_display.setText("Clocked In");
        }
        else
        {
            // Disable the clock out button
            // Enable the clock in button
            clockin.setDisable(false);
            clockout.setDisable(true);
            status_display.setText("Clocked Out");
        }
        
        
        /*
        // Get the employee's hours information 
        */

        // We need a calendar to compare dates against today
        Calendar today = Calendar.getInstance();

        // We need the employees tracker to get daily and weekly hours
        Tracker employee_tracker = employee.getTracker();
        double daily_hours = employee_tracker.getHours(TimeInterval.TODAY);
        double weekly_hours = employee_tracker.getHours(TimeInterval.WEEKLY);

        // We can get the pay period total hours from this current pay period stored in the employee
        PayPeriod current_pay_period = employee.getPayPeriod(today);
        double pay_period_hours = current_pay_period.getTotalHours();

        // Round all the hours to two decimals before displaying
        daily_hours = Filters.round(daily_hours);
        weekly_hours = Filters.round(weekly_hours);
        pay_period_hours = Filters.round(pay_period_hours);

        /*
        // Using information of hours we just got, set our fields to our values
         */
        hours_today_display.setText(Double.toString(daily_hours));
        hours_week_display.setText(Double.toString(weekly_hours));
        hours_payperiod_display.setText(Double.toString(pay_period_hours));
    }


    /**
     * Pull fresh employee list from the server.
     * @return populated array list with employees requested from server. Null if failed or empty.
     */
    private ArrayList<Employee> requestEmployees() {

        ArrayList<Employee> employee_list = worker.getEmployees();
        if (employee_list == null)
            return null;

        return employee_list;
    }


    /**
     * Updates filtered and sorted list of employees using argument list of employees
     * Checks the currently selected employee, to be reselected after repopulating.
     * Reselects employee after updating to show updates.
     * @param employee_list latest list of employees requested from server (use requestEmployees())
     */
    private void updateEmployeeList(ArrayList<Employee> employee_list) {

        if (employee_list == null) {
            return;
        }

        /*
        // Remember the currently selected employee
         */
        Employee selected = employees.getSelectionModel().getSelectedItem();
        for (Employee employee : employee_list) {
            if (employee.equals(selected)) {
                setEmployee(employee);
            }
        }


        /*
        // Setup the filtered / sorted lists to prepare to populate list view
         */
        ObservableList<Employee> obs_employee_list = FXCollections.observableArrayList(employee_list);


        // Create a filtered list to apply predicate to
        filtered = new FilteredList<>(obs_employee_list);


        // Setup our sorted list with a comparator to sort employee names by alphabetical order
        sorted = new SortedList<>(filtered, new Comparator<Employee>() {

            @Override
            public int compare(Employee emp1, Employee emp2) {

                // Get both the employee names
                String emp1_name = emp1.getName();
                String emp2_name = emp2.getName();

                // Compare the two employee names independent of capitalization
                return (emp1_name.compareToIgnoreCase(emp2_name));

            }

        });


        // Set the new content in the list view
        employees.setItems(sorted);
        employees.refresh();  // Refresh is necessary


        // Apply filter if exists (if the search input field is not empty)
        if (!searchinput.getText().isEmpty())
        {
            // Set the list predicate to filter users
            filtered.setPredicate(testForUser(searchinput.getText()));

            // Ensure the same employee that was initially manipulated is reselected
            employees.getSelectionModel().select(selected);
        }
    }


    /**
     * To be used for searching of employee name's.
     * @return predicate testing for matching names of argument
     */
    private Predicate<Employee> testForUser(String name) {

        Predicate<Employee> predicate = new Predicate<Employee>() {

            @Override
            public boolean test(Employee input) {

                // If there's no name to test, match all employees
                if (name.isEmpty())
                    return true;

                String test_name = name.toLowerCase();
                String emp_name = input.getName().toLowerCase();

                // Test if the input contains the name the user is searching for, return true if so, false if not
                if (emp_name.contains(test_name))
                    return true;

                return false;
            }

        };

        return predicate;

    }
}
