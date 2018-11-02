package com.austinramsay.timekeeper;

import com.austinramsay.timekeeperobjects.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;

import javafx.application.Application;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;

/**
 *
 * @author austinramsay
 */
public class TimeKeeper extends Application {
   

    private RequestWorker worker;


    
    /**
     * Main
     */
    public static void main(String[] args) 
    {
        launch(args);
    }
    



    
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

            /*
            // Get the selected employee to get necessary identifying information
             */
            int employee_id = employees.getSelectionModel().getSelectedItem().getEmployeeID();

            /*
            // Create and send a request to the server
             */
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

            /*
            // Get the selected employee to get necessary identifying information
             */
            int employee_id = employees.getSelectionModel().getSelectedItem().getEmployeeID();

            /*
            // Create and send a request to the server
             */
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

            Employee selected = employees.getSelectionModel().getSelectedItem();
            displayReport(selected);

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
                            this.setOnMouseClicked(e -> { 
                                setEmployee(employee);
                            });
                            
                        }
                        else
                        {
                            super.updateItem(null, empty);

                            this.setText(null);
                            this.setOnMouseClicked(e -> {
                                setEmployee(null);
                                employees.getSelectionModel().clearSelection();
                            });
                        }
                    } 
                };
                return cell;
            }
        } );
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
    }





    /**
     * Build a report stage for a selected employee.
     * The report will display information about employee actions along with a corresponding date/time of the action. (Iteration of action log)
     * It will contain information regarding pay period information times for all pay periods of the selected time range. (Iteration of pay period list)
     * Weekly and daily information will be available as well. (Using search function to filter hours log)
     * @param employee The employee to analyze.
     */
    private ArrayList<PayPeriod> employee_payperiods;  private ObservableList<PayPeriod> obs_payperiods;
    private ListView<String> actions_list;             private ObservableList<String> obs_actions;
    private ListView<String> hours_list;               private ObservableList<String> obs_hours;
    private Label selected_pp_label;
    private void displayReport(Employee employee) {

        // Create a stage to build the report window
        Stage report_window = new Stage();


        /*
        // Create the pay periods list view
        // This will be our main filter for sorting information in the report display
        // The information displayed will be dependant upon the pay period selected
         */
        ListView<PayPeriod> payperiods_list = new ListView<>();

        // Create cell factory for pay periods list view
        payperiods_list.setCellFactory(new Callback<ListView<PayPeriod>, ListCell<PayPeriod>>() {
            @Override
            public ListCell<PayPeriod> call(ListView<PayPeriod> param)
            {
                ListCell<PayPeriod> cell = new ListCell<PayPeriod>() {
                    @Override
                    protected void updateItem(PayPeriod payperiod, boolean empty)
                    {

                        if (payperiod != null)
                        {
                            super.updateItem(payperiod, empty);

                            Calendar start = payperiod.getStartDate();
                            Calendar end = payperiod.getEndDate();
                            String payperiod_date = String.format("%d/%d/%d - %d/%d/%d",
                                    (start.get(Calendar.MONTH) + 1),
                                    start.get(Calendar.DAY_OF_MONTH),
                                    start.get(Calendar.YEAR),
                                    (end.get(Calendar.MONTH) + 1),
                                    end.get(Calendar.DAY_OF_MONTH),
                                    end.get(Calendar.YEAR)
                            );

                            this.setText(payperiod_date);
                            this.setPrefHeight(30);
                            this.setOnMouseClicked(e -> {

                                // Pay Period was selected
                                // Filter the actions and hours lists to fit under pay period dates

                                // Filter Actions
                                obs_actions = getActionEntries(payperiod.getStartDate(), payperiod.getEndDate(), employee.getTracker().getActionLog());
                                actions_list.setItems(obs_actions);
                                actions_list.refresh();

                                // Filter Hours
                                obs_hours = getHoursEntries(payperiod.getStartDate(), payperiod.getEndDate(), employee.getTracker().getHoursLog());
                                hours_list.setItems(obs_hours);
                                hours_list.refresh();

                                // Resolve the amount of hours clocked for this pay period and update labels
                                selected_pp_label.setText("Selected Pay Period Hours: " + round(payperiod.getTotalHours()));

                            });

                        }
                        else
                        {
                            super.updateItem(null, empty);

                            this.setText(null);
                            this.setOnMouseClicked(e -> {
                                setEmployee(null);
                            });
                        }
                    }
                };
                return cell;
            }
        } ); // End pay periods list view cell factory

        // Set list view size
        payperiods_list.setPrefHeight(350);
        payperiods_list.setPrefWidth(225);

        // To populate the pay period list view, fetch the employee's pay periods and convert to an observable array list
        employee_payperiods = employee.getPayPeriods();
        obs_payperiods = FXCollections.observableArrayList(employee_payperiods);

        // Populate the pay period list
        payperiods_list.setItems(obs_payperiods);


        /*
        // Create the action log list view
         */
        actions_list = new ListView<>();
        actions_list.setPrefHeight(350);
        actions_list.setPrefWidth(225);

        obs_actions = getActionEntries(null, null, employee.getTracker().getActionLog());

        actions_list.setItems(obs_actions);


        /*
        // Create the hours log list view
         */
        hours_list = new ListView<>();
        hours_list.setPrefHeight(350);
        hours_list.setPrefWidth(225);

        obs_hours = getHoursEntries(null, null, employee.getTracker().getHoursLog());

        hours_list.setItems(obs_hours);


        /*
        // Create a VBox with corresponding labels for each list view
         */
        VBox pp_box = new VBox(10); // Pay period box
        VBox al_box = new VBox(10); // Action log box
        VBox hl_box = new VBox(10); // Hours log box

        pp_box.getChildren().addAll(
                new Label("Pay Periods:"), // Add a label on top
                payperiods_list                // Add the pay periods list view
        );

        al_box.getChildren().addAll(
                new Label("Action Log:"), // Add a label on top
                actions_list                  // Add the actions list view
        );

        hl_box.getChildren().addAll(
                new Label("Hours Clocked Log:"), // Add a label on top
                hours_list                           // Add the hours list view
        );


        /*
        // Fetch latest employee hour values (This pay period hours, daily hours, weekly hours)
         */
        PayPeriod current_pay_period = employee.getPayPeriod(Calendar.getInstance());
        double pay_period_hours = current_pay_period.getTotalHours();
        double daily_hours = employee.getTracker().getHours(TimeInterval.TODAY);
        double weekly_hours = employee.getTracker().getHours(TimeInterval.WEEKLY);


        // Create labels using information from above
        selected_pp_label = new Label("Selected Pay Period Hours: (None Selected)");
        Label daily_hours_label = new Label("Daily Hours Clocked: " + round(daily_hours));
        Label weekly_hours_label = new Label("Weekly Hours Clocked: " + round(weekly_hours));
        Label pp_hours_label = new Label("Current Pay Period Hours: " + round(pay_period_hours));


        /*
        // Create VBox to house labels
         */
        VBox labels_container = new VBox(12);
        labels_container.getChildren().addAll(
                selected_pp_label,
                daily_hours_label,
                weekly_hours_label,
                pp_hours_label
        );


        /*
        // Create buttons to close report, clear filters
         */
        Button clear = new Button("Clear Selection");
        Button close = new Button("Close Report");

        close.setOnAction(e -> {
            report_window.close();
        });

        clear.setOnAction(e -> {

            // List all actions and hours clocked not dependent on pay period selected
            // Unselect pay period if one is selected

            if (!payperiods_list.getSelectionModel().isEmpty())
                payperiods_list.getSelectionModel().clearSelection();

            // Filter Actions
            obs_actions = getActionEntries(null, null, employee.getTracker().getActionLog());
            actions_list.setItems(obs_actions);
            actions_list.refresh();

            // Filter Hours
            obs_hours = getHoursEntries(null, null, employee.getTracker().getHoursLog());
            hours_list.setItems(obs_hours);
            hours_list.refresh();

            // Update the selected pay period label
            selected_pp_label.setText("Selected Pay Period Hours: (None Selected)");
        });


        /*
        // Create HBox for buttons
         */
        HBox button_box = new HBox(12);
        button_box.setAlignment(Pos.CENTER);
        HBox.setHgrow(button_box, Priority.ALWAYS);
        button_box.getChildren().addAll(clear, close);


        /*
        // Create an HBox to house labels, and button box
         */
        HBox lower_root = new HBox();
        lower_root.getChildren().addAll(labels_container, button_box);


        /*
        // Create an HBox to house list views
         */
        HBox listview_container = new HBox(15);
        listview_container.getChildren().addAll(
                pp_box,
                al_box,
                hl_box
        );


        /*
        // Create root container
         */
        VBox root = new VBox(12);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
                listview_container,
                lower_root
        );


        /*
        // Build scene and stage window
         */
        Scene report_scene = new Scene(root);
        report_window.setScene(report_scene);
        report_window.setTitle(String.format("%s's Employee Report", employee.getName()));
        report_window.show();
    }





    /**
     * @param start the start day to dependent on end date
     * @param end the end day to check dependent on start date
     * @param actions the action log of the employee
     * @return an observable list of formatted string action entries (Date: Action)
     */
    private ObservableList<String> getActionEntries(Calendar start, Calendar end, LinkedHashMap<Calendar, EmployeeAction> actions) {

        // Create observable list to add to while searching for entries
        ObservableList<String> obs_actions = FXCollections.observableArrayList();

        for (Map.Entry<Calendar, EmployeeAction> entry : actions.entrySet()) {

            // Get the entry date for the action
            Calendar entry_date = entry.getKey();

            // If the start or end date argumented is null, include all entries
            // If the entry date is between the requested start/end dates, include the entry
            if ( ((start == null) && (end == null))  ||  (betweenDate(start, end, entry_date))) {
                String entry_date_str = String.format("%d/%d/%d %d:%d",
                        (entry_date.get(Calendar.MONTH) + 1),
                        entry_date.get(Calendar.DAY_OF_MONTH),
                        entry_date.get(Calendar.YEAR),
                        entry_date.get(Calendar.HOUR_OF_DAY),
                        entry_date.get(Calendar.MINUTE));

                EmployeeAction entry_action = entry.getValue();
                String entry_action_str;

                if (entry_action == EmployeeAction.CLOCKIN) {
                    entry_action_str = "Clocked In";
                } else {
                    entry_action_str = "Clocked Out";
                }

                String full_entry = String.format("%s: %s", entry_date_str, entry_action_str);

                obs_actions.add(full_entry);
            }

        } // End action entry search 'for' loop

        return obs_actions;

    }





    /**
     * @param start the start day to dependent on end date
     * @param end the end day to check dependent on start date
     * @param hours the hours log of the employee
     * @return an observable list of formatted string hours entries (Date: Hours clocked)
     */
    private ObservableList<String> getHoursEntries(Calendar start, Calendar end, LinkedHashMap<Calendar, Double> hours) {

        // Create observable list to add to while searching for entries
        ObservableList<String> obs_hours = FXCollections.observableArrayList();

        for (Map.Entry<Calendar, Double> entry : hours.entrySet()) {

            Calendar entry_date = entry.getKey();

            // If the start or end date argumented is null, include all entries
            // If the entry date is between the requested start/end dates, include the entry
            if (((start == null) && (end == null)) || (betweenDate(start, end, entry_date))) {
                String entry_date_str = String.format("%d/%d/%d %d:%d",
                        (entry_date.get(Calendar.MONTH) + 1),
                        entry_date.get(Calendar.DAY_OF_MONTH),
                        entry_date.get(Calendar.YEAR),
                        entry_date.get(Calendar.HOUR_OF_DAY),
                        entry_date.get(Calendar.MINUTE));

                Double entry_hours = round(entry.getValue());

                String full_entry = String.format("%s: %.2f Hours", entry_date_str, entry_hours);

                obs_hours.add(full_entry);
            }

        } // End hours entry search 'for' loop

        return obs_hours;

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
        daily_hours = round(daily_hours);
        weekly_hours = round(weekly_hours);
        pay_period_hours = round(pay_period_hours);

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





    /**
     * Tests two Calendar dates MM/dd/YYYY for equality
     * @return true if MM/dd/YYYY match, false if not
     */
    private boolean betweenDate(Calendar start, Calendar end, Calendar compare) {

        // First just check if the date fits between the start and end dates
        if (compare.after(start) && compare.before(end))
            return true;

        // Check if the date matches exactly the start date
        if ((start.get(Calendar.YEAR) == compare.get(Calendar.YEAR)) && (start.get(Calendar.MONTH) == compare.get(Calendar.MONTH)) && (start.get(Calendar.DAY_OF_MONTH) == compare.get(Calendar.DAY_OF_MONTH)))
            return true;

        // Check if the date matches exactly the end date
        if ((end.get(Calendar.YEAR) == compare.get(Calendar.YEAR)) && (end.get(Calendar.MONTH) == compare.get(Calendar.MONTH)) && (end.get(Calendar.DAY_OF_MONTH) == compare.get(Calendar.DAY_OF_MONTH)))
            return true;

        return false;
    }





    /**
     * @return a 2 decimal rounded double
     */
    private double round(double number) {
        BigDecimal to_round = BigDecimal.valueOf(number);
        to_round = to_round.setScale(2, RoundingMode.HALF_UP);
        return to_round.doubleValue();
    }




}
