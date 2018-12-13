package com.austinramsay.gui;

import com.austinramsay.events.EmployeeLogListener;
import com.austinramsay.events.NewActionListener;
import com.austinramsay.events.NewHoursEventListener;
import com.austinramsay.model.ActionsListRenderer;
import com.austinramsay.model.HoursListRenderer;
import com.austinramsay.model.PayPeriodsListRenderer;
import com.austinramsay.timekeeper.EmployeeAction;
import com.austinramsay.timekeeper.PayPeriod;
import com.austinramsay.types.ActionType;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

class EmployeeLogPanel extends JPanel {

    private EmployeeLogListener logListener;   // Buttons will push notify to listener to handle addition/removal of action events and hour events

    private JList payPeriodsList;
    private JList actionsList;
    private JList hoursList;

    private DefaultListModel<PayPeriod> ppModel;
    private DefaultListModel<Map.Entry<Calendar, EmployeeAction>> actionModel;  // Model will contain all data from map of employee's tracker information
    private DefaultListModel<Map.Entry<Calendar, Double>> hoursModel;           // Model will contain all data from map of employee's tracker information

    private PayPeriodsListRenderer ppRenderer;
    private ActionsListRenderer actionsRenderer;
    private HoursListRenderer hoursRenderer;

    private LinkedHashMap<Calendar, Double> hours;
    private LinkedHashMap<Calendar, EmployeeAction> actions;

    private JButton addActionEntry = new JButton("Add");
    private JButton removeActionEntry = new JButton("Remove");

    private JButton addHoursEntry = new JButton("Add");
    private JButton removeHoursEntry = new JButton("Remove");

    public EmployeeLogPanel(EmployeeLogListener logListener) {

        this.logListener = logListener;

        // Create 3 columns each in a line axis box layout
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        add(getPayPeriodsColumn());
        add(Box.createRigidArea(new Dimension(10,0)));
        add(getActionsColumn());
        add(Box.createRigidArea(new Dimension(10,0)));
        add(getHoursColumn());
    }


    /**
     * Build pay period JList panel and define list properties such as the cell renderer and data model
     * @return JPanel column containing pay period list
     */
    private JPanel getPayPeriodsColumn() {
        /*
        // Initialize pay periods data model and renderer for use when an employee is selected
         */
        ppModel = new DefaultListModel<>();
        ppRenderer = new PayPeriodsListRenderer();

        // Build pay periods list and define renderer
        payPeriodsList = new JList(ppModel);
        payPeriodsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payPeriodsList.setCellRenderer(ppRenderer);
        payPeriodsList.addListSelectionListener(e -> {

            // Pay period was selected, populate action entries and hour entries lists - enable/disable buttons depending on if a selection is chosen

            // Because hours entries functions are dependent upon whether a pay period is selected or not, set buttons to disabled unless a pay period is selected
            if (payPeriodsList.isSelectionEmpty()) {
                addHoursEntry.setEnabled(false);
            } else {
                addHoursEntry.setEnabled(true);
            }

            refreshModels();
        });

        // Set default values of hours entry buttons to disabled until a pay period is selected
        addHoursEntry.setEnabled(false);

        // Add list to scroll pane and limit size
        JScrollPane payPeriodsPane = new JScrollPane(payPeriodsList);
        payPeriodsPane.setPreferredSize(new Dimension(220,275));

        // Create borders and add all nodes to panel
        JPanel PPcolumn = new JPanel();
        PPcolumn.setLayout(new BoxLayout(PPcolumn, BoxLayout.PAGE_AXIS));
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        Border titled = BorderFactory.createTitledBorder("Pay Periods");
        Border compounded = BorderFactory.createCompoundBorder(padding, titled);
        PPcolumn.setBorder(compounded);

        PPcolumn.add(payPeriodsPane);

        return PPcolumn;
    }


    /**
     * Build action entry JList panel (w/ add and remove buttons) and define list properties such as the cell renderer and data model
     * @return JPanel column containing action entry list with buttons
     */
    private JPanel getActionsColumn() {
        /*
        // Initialize actions data model and renderer for use when an employee is selected
         */
        actionModel = new DefaultListModel<>();
        actionsRenderer = new ActionsListRenderer();

        // Build actions list and define renderer
        actionsList = new JList(actionModel);
        actionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionsList.setCellRenderer(actionsRenderer);
        actionsList.addListSelectionListener(e -> {

            // Action event was selected/deselected

            // If selection is empty, disable the removal button - else set enabled
            if (actionsList.isSelectionEmpty()) {
                removeActionEntry.setEnabled(false);
            } else {
                removeActionEntry.setEnabled(true);
            }

        });

        // Set default add action entry button to disabled until an employee is selected
        addActionEntry.setEnabled(false);

        // Set default remove action entry button to disabled until an entry is selected
        removeActionEntry.setEnabled(false);

        // Add list to scroll pane and limit size
        JScrollPane actionsListPane = new JScrollPane(actionsList);
        actionsListPane.setPreferredSize(new Dimension(220,275));


        // Build panel and add nodes
        JPanel actionsColumn = new JPanel();
        actionsColumn.setLayout(new BoxLayout(actionsColumn, BoxLayout.PAGE_AXIS));
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        Border titled = BorderFactory.createTitledBorder("Action Entries");
        Border compounded = BorderFactory.createCompoundBorder(padding, titled);
        actionsColumn.setBorder(compounded);

        actionsColumn.add(actionsListPane);
        actionsColumn.add(Box.createRigidArea(new Dimension(0,10)));
        actionsColumn.add(addActionEntry);
        actionsColumn.add(Box.createRigidArea(new Dimension(0,10)));
        actionsColumn.add(removeActionEntry);


        // Define button logic
        // Fire log listener event for controller to process upon being clicked (add/remove an action entry)
        addActionEntry.addActionListener(e -> {

            // Prepare the prompt with a listener to handle a new entry submission
            ActionPrompt prompt = new ActionPrompt(new NewActionListener() {
                @Override
                public void fireNewActionSubmission(Calendar date, EmployeeAction action) {
                    logListener.fireActionEntryAddition(date, action);
                }
            });

            // Display the new action entry prompt
            prompt.showNewActionPrompt();
        });

        removeActionEntry.addActionListener(e -> {

            // Get the selected action entry to remove
            Map.Entry<Calendar, EmployeeAction> selectedAction = (Map.Entry<Calendar, EmployeeAction>)actionsList.getSelectedValue();

            // If none is selected, return
            if (selectedAction == null) {
                return;
            }

            // Fire request to controller to remove this entry from the map
            logListener.fireActionEntryRemoval(selectedAction.getKey(), selectedAction.getValue());
        });


        // Return built panel
        return actionsColumn;
    }


    /**
     * Build hour entry JList panel (w/ add and remove buttons) and define list properties such as the cell renderer and data model
     * @return JPanel column containing hour entry list with buttons
     */
    private JPanel getHoursColumn() {
        /*
        // Initialize hours entry data model and renderer for use when an employee is selected
         */
        hoursModel = new DefaultListModel<>();
        hoursRenderer = new HoursListRenderer();

        // Build hours list and define renderer
        hoursList = new JList(hoursModel);
        hoursList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hoursList.setCellRenderer(hoursRenderer);
        hoursList.addListSelectionListener(e -> {

            // Action event was selected/deselected

            // If selection is empty, disable the removal button - else set enabled
            if (hoursList.isSelectionEmpty()) {
                removeHoursEntry.setEnabled(false);
            } else {
                removeHoursEntry.setEnabled(true);
            }

        });

        // Set default remove hours entry button to disabled until an entry is selected
        removeHoursEntry.setEnabled(false);

        // Add to scroll pane and limit size
        JScrollPane hoursListPane = new JScrollPane(hoursList);
        hoursListPane.setPreferredSize(new Dimension(220, 275));


        // Build panel and add nodes
        JPanel hoursColumn = new JPanel();
        hoursColumn.setLayout(new BoxLayout(hoursColumn, BoxLayout.PAGE_AXIS));
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        Border titled = BorderFactory.createTitledBorder("Hours Clocked");
        Border compounded = BorderFactory.createCompoundBorder(padding, titled);
        hoursColumn.setBorder(compounded);

        hoursColumn.add(hoursListPane);
        hoursColumn.add(Box.createRigidArea(new Dimension(0,10)));
        hoursColumn.add(addHoursEntry);
        hoursColumn.add(Box.createRigidArea(new Dimension(0,10)));
        hoursColumn.add(removeHoursEntry);


        // Define button logic

        // Fire log listener event for controller to process upon being clicked (add/remove an hours entry)
        addHoursEntry.addActionListener(e -> {

            if (getSelectedPayPeriod() == null) {
                JOptionPane.showMessageDialog(null, "Please select a pay period.");
                return;
            }

            HoursPrompt prompt = new HoursPrompt(new NewHoursEventListener() {
                @Override
                public void fireNewHoursEventSubmission(Double hoursClocked, Calendar clockOutTime) {
                    logListener.fireActionEntryAddition(clockOutTime, EmployeeAction.CLOCKOUT);
                    logListener.fireHourEntryAddition(clockOutTime, hoursClocked);
                }

                @Override
                public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions() {
                    return logListener.retrieveEmployeeActions();
                }
            });

            // Display the new hours entry prompt
            prompt.showNewHoursPrompt();

        });

        removeHoursEntry.addActionListener(e -> {
            Map.Entry<Calendar, Double> selectedHoursEntry = (Map.Entry<Calendar, Double>)hoursList.getSelectedValue();

            if (selectedHoursEntry == null) {
                return;
            }

            // Fire request to controller to remove this entry from the map
            logListener.fireHourEntryRemoval(selectedHoursEntry.getKey(), selectedHoursEntry.getValue());
        });


        // Return built panel
        return hoursColumn;
    }


    /**
     * Enables/disables 'Add' button under 'Action Events' column dependant upon if an employee is selected or not
     */
    public void setAddActionButton(boolean enabled) {
        addActionEntry.setEnabled(enabled);
    }


    /**
     * Set each time a new employee selected event is fired.
     * @param actions the selected employee's action entry map
     */
    public void setActions(LinkedHashMap<Calendar, EmployeeAction> actions) {
        this.actions = actions;
    }


    /**
     * Set each time a new employee selected event is fired.
     * @param hours the selected employee's hours entry map
     */
    public void setHours(LinkedHashMap<Calendar, Double> hours) {
        this.hours = hours;
    }


    /**
     * @return the selected pay period in the log panel
     */
    public PayPeriod getSelectedPayPeriod() {
        return (PayPeriod)payPeriodsList.getSelectedValue();
    }


    /**
     * When selecting a new employee, the pay periods model must be set to correspond with that employee
     * @param payperiods the selected employee's pay period list
     */
    public void setPayPeriods(ArrayList<PayPeriod> payperiods) {
        // Clear current model data
        ppModel.clear();

        // If no employee is selected, and pay periods is null..there is no need to continue.
        if (payperiods == null) {
            return;
        }

        // Iterate set pay period list and add each element to model
        for (PayPeriod payperiod : payperiods) {
            ppModel.addElement(payperiod);
        }
    }


    /**
     * When selecting a new pay period, the hours list model must be set to correspond with the pay period start & end dates
     * @param hours the filtered hours entry map
     */
    private void updateHoursModel(LinkedHashMap<Calendar, Double> hours) {
        // Clear current model data
        hoursModel.clear();

        // If hours map is null..there is no need to continue
        if (hours == null) {
            return;
        }

        // Iterate hours entries and add each element to model
        for (Map.Entry<Calendar, Double> entry : hours.entrySet()) {
            hoursModel.addElement(entry);
        }
    }


    /**
     * When selecting a new pay period, the action list model must be set to correspond with the pay period start & end dates
     * @param actions the filtered action entry map
     */
    private void updateActionModel(LinkedHashMap<Calendar, EmployeeAction> actions) {
        // Clear current model data
        actionModel.clear();

        // If actions map is null..there is no need to continue
        if (actions == null) {
            return;
        }

        // Iterate action entries and add each element to model
        for (Map.Entry<Calendar, EmployeeAction> entry : actions.entrySet()) {
            actionModel.addElement(entry);
        }
    }


    /**
     * Dependant upon selected pay period, update the actions and hours models.
     * If no pay period is selected, the lists are set to null
     */
    public void refreshModels() {
        // Pay period was selected, populate action entries and hour entries lists
        PayPeriod selected = (PayPeriod)payPeriodsList.getSelectedValue();

        // When the pay period model is cleared upon changing employees, it will fire this listener
        // Because the user has only just selected an employee, they haven't selected an actual pay period yet
        // We can set all models to null
        if (selected == null) {
            updateHoursModel(null);
            updateActionModel(null);
            return;
        }

        // Filter hours entries that fit between the selected pay period start & end dates
        LinkedHashMap<Calendar, Double> filteredHoursMap = getFilteredHoursEntries(selected.getStartDate(), selected.getEndDate(), hours);

        // Filter action entries that fit between the selected pay period start & end dates
        LinkedHashMap<Calendar, EmployeeAction> filteredActionsMap = getFilteredActionEntries(selected.getStartDate(), selected.getEndDate(), ActionType.ALL, actions);

        // Update list model information with filtered results
        updateHoursModel(filteredHoursMap);
        updateActionModel(filteredActionsMap);
    }


    /**
     * @param payPeriodStart the start day of the pay period to filter
     * @param payPeriodEnd the end day of the pay period to filter
     * @param hours the hours entry map of the employee
     * @return an filtered map of corresponding action events contained between specified pay period dates
     */
    private LinkedHashMap<Calendar, Double> getFilteredHoursEntries(Calendar payPeriodStart, Calendar payPeriodEnd, LinkedHashMap<Calendar, Double> hours) {

        // Initialize a map to add entries that fit between specified dates to
        LinkedHashMap<Calendar, Double> filteredEntries = new LinkedHashMap<>();

        // Iterate all action entries, add the entry if fits between specified pay period date
        for (Map.Entry<Calendar, Double> entry : hours.entrySet()) {

            // Get the entry date for the action
            Calendar entry_date = entry.getKey();

            // If the entry date is between the requested start/end dates, include the entry
            if (betweenDate(payPeriodStart, payPeriodEnd, entry_date)) {
                filteredEntries.put(entry.getKey(), entry.getValue());
            }

        } // End action entry search 'for' loop

        return filteredEntries;

    }


    /**
     * @param payPeriodStart the start day of the pay period to filter
     * @param payPeriodEnd the end day of the pay period to filter
     * @param actions the action entry map of the employee
     * @return an filtered map of corresponding action events contained between specified pay period dates
     */
    public LinkedHashMap<Calendar, EmployeeAction> getFilteredActionEntries(Calendar payPeriodStart, Calendar payPeriodEnd, ActionType type, LinkedHashMap<Calendar, EmployeeAction> actions) {

        // Initialize a map to add entries that fit between specified dates to
        LinkedHashMap<Calendar, EmployeeAction> filteredEntries = new LinkedHashMap<>();

        // Iterate all action entries, add the entry if fits between specified pay period date
        for (Map.Entry<Calendar, EmployeeAction> entry : actions.entrySet()) {

            // Get the entry date for the action
            Calendar entry_date = entry.getKey();

            // If the entry date is between the requested start/end dates, include the entry
            if (betweenDate(payPeriodStart, payPeriodEnd, entry_date)) {

                // The entries are between the requested date, compare against the type requested now
                if (type == ActionType.ALL) {
                    filteredEntries.put(entry.getKey(), entry.getValue());

                } else if (type == ActionType.CLOCKIN) {
                    // Type requested is clocking in events, compare against and add if correct
                    if (entry.getValue() == EmployeeAction.CLOCKIN)
                        filteredEntries.put(entry.getKey(), entry.getValue());

                } else if (type == ActionType.CLOCKOUT) {
                    // Type requested is clocking out events, compare against and add if correct
                    if (entry.getValue() == EmployeeAction.CLOCKOUT)
                        filteredEntries.put(entry.getKey(), entry.getValue());
                }

            }

        } // End action entry search 'for' loop

        return filteredEntries;

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
}
// End EmployeeLogPanel JPanel class
