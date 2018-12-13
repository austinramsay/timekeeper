package com.austinramsay.gui;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.exceptions.PayPeriodOutOfBoundsException;
import com.austinramsay.types.ActionType;
import com.austinramsay.events.EmployeeLogListener;
import com.austinramsay.timekeeper.Employee;
import com.austinramsay.timekeeper.EmployeeAction;
import com.austinramsay.timekeeper.PayPeriod;
import com.austinramsay.timekeeper.TimeInterval;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.LinkedHashMap;


/**
 * Moderator form contains information about employee specific information.
 * Contains dynamic labels for employee name, ID number, hours the employee has worked for the pay period, week, and day.
 */
public class ModeratorForm extends JPanel {

    private EmployeeLogListener logListener;

    private EmployeeLogPanel logPanel;

    private JLabel formNameLabel = new JLabel("Name:");
    private JLabel employeeNameLabel = new JLabel();

    private JLabel formIdLabel = new JLabel("Employee ID:");
    private JLabel employeeIdLabel = new JLabel();

    private JLabel formPPHoursLabel = new JLabel("Current Pay Period:");
    private JLabel employeePPHoursLabel = new JLabel();

    private JLabel formWeekHoursLabel = new JLabel("This Week:");
    private JLabel employeeWeekHoursLabel = new JLabel();

    private JLabel formTodayHoursLabel = new JLabel("Today:");
    private JLabel employeeTodayHoursLabel = new JLabel();

    public ModeratorForm(EmployeeLogListener logListener) {

        this.logListener = logListener;

        // Define size of form panel
        Dimension size = getPreferredSize();
        size.width = 400;
        setPreferredSize(size);

        /*
        // Create labeled border to define employee information around this panel
         */
        Border padding = BorderFactory.createEmptyBorder(5,5,0,5);
        Border titled = BorderFactory.createTitledBorder("Employee Information");
        setBorder(BorderFactory.createCompoundBorder(padding, titled));

        /*
        // Left column: Houses static labels as identifiers (Ex. "Name:", "Employee ID:")
         */
        JPanel leftColumn = new JPanel();
        leftColumn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftColumn.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));  // Create 10 pixel padding between left & right columns
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.PAGE_AXIS));
        leftColumn.add(Box.createRigidArea(new Dimension(0,15)));
        leftColumn.add(formNameLabel);
        leftColumn.add(Box.createRigidArea(new Dimension(0,15)));
        leftColumn.add(formIdLabel);
        leftColumn.add(Box.createRigidArea(new Dimension(0,15)));
        leftColumn.add(formPPHoursLabel);
        leftColumn.add(Box.createRigidArea(new Dimension(0,15)));
        leftColumn.add(formWeekHoursLabel);
        leftColumn.add(Box.createRigidArea(new Dimension(0,15)));
        leftColumn.add(formTodayHoursLabel);
        leftColumn.add(Box.createRigidArea(new Dimension(0,20)));

        /*
        // Right column: Houses dynamic labels. These labels change dependant upon the employee that is selected (Ex. amount of hours)
         */
        JPanel rightColumn = new JPanel();
        rightColumn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.PAGE_AXIS));
        rightColumn.add(Box.createRigidArea(new Dimension(0,15)));
        rightColumn.add(employeeNameLabel);
        rightColumn.add(Box.createRigidArea(new Dimension(0,15)));
        rightColumn.add(employeeIdLabel);
        rightColumn.add(Box.createRigidArea(new Dimension(0,15)));
        rightColumn.add(employeePPHoursLabel);
        rightColumn.add(Box.createRigidArea(new Dimension(0,15)));
        rightColumn.add(employeeWeekHoursLabel);
        rightColumn.add(Box.createRigidArea(new Dimension(0,15)));
        rightColumn.add(employeeTodayHoursLabel);
        rightColumn.add(Box.createRigidArea(new Dimension(0,20)));

        /*
        // Log panel: Contains 3 COLUMNS IN THE LOG PANEL... each with corresponding buttons to add/remove hours/actions.
        // Column 1: List of pay periods
        // Column 2 & 3 JLISTS ARE DEPENDANT UPON CHOICE OF PAY PERIOD IN COLUMN 1
        // Column 2: List of employee actions dependant on selected pay period
        // Column 3: List of employee hours clocked dependant on selected pay period
         */
        logPanel = new EmployeeLogPanel(logListener);

        /*
        // Wrap all panels together
         */
        JPanel empInfoPanel = new JPanel();
        empInfoPanel.setLayout(new BoxLayout(empInfoPanel, BoxLayout.LINE_AXIS));
        empInfoPanel.add(Box.createHorizontalGlue());
        empInfoPanel.add(leftColumn);
        empInfoPanel.add(rightColumn);
        empInfoPanel.add(Box.createHorizontalGlue());

        JPanel compounded = new JPanel();
        compounded.setLayout(new BoxLayout(compounded, BoxLayout.PAGE_AXIS));
        compounded.add(Box.createVerticalGlue());
        compounded.add(empInfoPanel);
        compounded.add(Box.createVerticalGlue());
        compounded.add(logPanel);
        compounded.add(Box.createVerticalGlue());

        // Add compounded panel to this form panel -- to be added into the main moderator frame
        add(compounded);
    }


    /**
     * Update dynamic labels contained in the employee information panel of the Moderator.
     * These labels are updated dependant upon information obtained from the argument Employee.
     * Displays warning that modifying hours may not be ideal when an employee is clocked-in.
     * @param selected the selected employee to obtain information from
     * @param refreshPayPeriods to update pay periods in the log panel or not
     */
    public void update(Employee selected, boolean refreshPayPeriods) {
        if (selected != null) {
            // Set labels corresponding to selected employee
            employeeNameLabel.setText(selected.getName());
            employeeIdLabel.setText(Integer.toString(selected.getEmployeeID()));
            employeeWeekHoursLabel.setText(Double.toString(round(selected.getTracker().getHours(TimeInterval.WEEKLY))) + " Hours");           // Get rounded weekly hours
            employeeTodayHoursLabel.setText(Double.toString(round(selected.getTracker().getHours(TimeInterval.TODAY))) + " Hours");           // Get rounded daily hours
            try {
                employeePPHoursLabel.setText(Double.toString(round(selected.getPayPeriod(Calendar.getInstance()).getTotalHours())) + " Hours");   // Get rounded pay period hours
            } catch (PayPeriodOutOfBoundsException ppe) {
                int repair = JOptionPane.showConfirmDialog(null, "Failed to locate pay period (out of bounds). Attempt to repair?", "Repair?", JOptionPane.YES_NO_OPTION);
                if (repair == JOptionPane.YES_OPTION) {
                    // Failed to find pay period to match today's date
                    TimeKeeperServer.broadcast("Message: Pay periods out of bounds. Attempting to repair...");

                    // Let's attempt to build more pay periods to fill the gap
                    TimeKeeperServer.current_org.addNewPayPeriods();

                    // Re-attempt the update
                    update(selected, refreshPayPeriods);
                } else {
                    employeePPHoursLabel.setText("Failed");
                }
            }

            // Set model data in log panel to correspond with selected employee

            // Sometimes may not be necessary to refresh pay periods list.
            // Ex. after adding a new hours entry, we need to update the employee labels to reflect new hours calculations
            // But pay periods would have not changed from this..
            // By avoiding the refresh, the user can keep their selected pay period instead of resetting the list model
            if (refreshPayPeriods) {
                logPanel.setPayPeriods(selected.getPayPeriods());
            }
            logPanel.setActions(selected.getTracker().getActionLog());
            logPanel.setHours(selected.getTracker().getHoursLog());
        } else {
            // No employee selected - set values to empty
            employeeNameLabel.setText("");
            employeeIdLabel.setText("");
            employeePPHoursLabel.setText("");
            employeeWeekHoursLabel.setText("");
            employeeTodayHoursLabel.setText("");

            logPanel.setPayPeriods(null);
            logPanel.setActions(null);
            logPanel.setHours(null);
        }
    }


    /**
     * This is a connection method between the form and the log panel
     * @return the selected pay period in the log panel
     */
    public PayPeriod getSelectedPayPeriod() {
        return logPanel.getSelectedPayPeriod();
    }


    /**
     * This is a connection method between the form and the log panel
     * Given a pay period start date, end date, and the full actions log of an employee.. return a filtered list of action events where the employee clocked in.
     * This is useful for the new hours submission prompt, where a user is given a list of clock-in events to correspond with the hours they are submitting.
     * @param payPeriodStart the pay period start date
     * @param payPeriodEnd the pay period end date
     * @param actions the full action log of the employee to be filtered
     * @return clock-in events falling between given pay period dates
     */
    public LinkedHashMap<Calendar, EmployeeAction> getClockInEvents(Calendar payPeriodStart, Calendar payPeriodEnd, LinkedHashMap<Calendar, EmployeeAction> actions) {
        // Reuse the log panel's filtered action entries method
        // Note argument: ActionType.CLOCKIN  --  ONLY CLOCK IN EVENTS RETURNED
        return logPanel.getFilteredActionEntries(payPeriodStart, payPeriodEnd, ActionType.CLOCKIN, actions);
    }


    /**
     * This is a connection method between the form and the log panel
     * Calls refreshModels() in the Moderator log panel
     * Dependant upon selected pay period, updates the actions and hours lists.
     */
    public void refreshModels() {
        logPanel.refreshModels();
    }


    /**
     * This is a connecting method to access the log panel's add action button.
     * Enables/disables 'Add' button under 'Action Events' column dependant upon if an employee is selected or not
     */
    public void setAddActionButton(boolean enabled) {
        logPanel.setAddActionButton(enabled);
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
// End ModeratorForm JPanel class
