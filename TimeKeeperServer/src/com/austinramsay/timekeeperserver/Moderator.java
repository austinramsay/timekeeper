package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.*;
import sun.awt.image.ImageWatched;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Moderator extends JFrame {

    private ModeratorForm form;
    private ModeratorList list;

    public Moderator() {

        super("Moderator");


        // Create the labels and buttons for employee specific information
        // The moderator form contains the Log Panel, which houses 3 JList's containing:
        //   1. Pay Periods
        //   2. Action event entries
        //   3. Hours clocked event entries
        // The moderator form should pass this listener to the log panel to handle button events when the user needs to add/remove either type of entries
        // This event will be handled here in the controller
        form = new ModeratorForm(new EmployeeLogListener() {

            @Override
            public void fireActionEntryRemoval(Calendar date, EmployeeAction action) {

                // Fetch the selected employee
                int employee_id = list.getSelectedEmployeeId();
                Employee selected = TimeKeeperServer.current_org.getEmployee(employee_id);

                // Get the action log map from the employee tracker
                LinkedHashMap<Calendar, EmployeeAction> actionsLog = selected.getTracker().getActionLog();

                // Attempt to remove selected action from the employee tracker
                boolean removed = actionsLog.remove(date, action);

                // Verify removed, if not warn the user
                if (!removed) {
                    JOptionPane.showMessageDialog(null, "Failed to remove the action entry from the tracker.");
                    return;
                }

                // Refresh the actions & hours list models to reflect changes
                updateFormLists();
            }

            @Override
            public void fireActionEntryAddition(Calendar date, EmployeeAction action) {

                // Fetch the selected employee
                int employee_id = list.getSelectedEmployeeId();
                Employee selected = TimeKeeperServer.current_org.getEmployee(employee_id);

                // Get the action log map from the employee tracker and add the event
                selected.getTracker().getActionLog().put(date, action);

                // Refresh the actions & hours list models to reflect changes
                updateFormLists();
            }

            @Override
            public void fireHourEntryRemoval(Calendar date, Double hours) {

                // Fetch the selected employee
                int employee_id = list.getSelectedEmployeeId();
                Employee selected = TimeKeeperServer.current_org.getEmployee(employee_id);

                // Get the hours log map from the employee tracker
                LinkedHashMap<Calendar, Double> hoursLog = selected.getTracker().getHoursLog();

                // Attempt to remove selected action from the employee tracker
                boolean removed = hoursLog.remove(date, hours);

                // Verify removed, if not warn the user
                if (!removed) {
                    JOptionPane.showMessageDialog(null, "Failed to remove the hours entry from the tracker.");
                    return;
                }

                // Refresh the actions & hours list models to reflect changes
                updateFormLists();
            }

            @Override
            public void fireHourEntryAddition(Calendar date, Double hours) {
                System.out.println("Add hour entry event fired.");
            }

            @Override
            public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions() {

                // Fetch the selected employee ID
                int employee_id = list.getSelectedEmployeeId();

                // Retrieve the employee's action log and return
                return TimeKeeperServer.current_org.getEmployee(employee_id).getTracker().getActionLog();
            }


        });


        // Create the JPanel containing the JList of the organization's employees
        list = new ModeratorList(new EmployeeChangeListener() {
            @Override
            public void fireEmployeeChangedEvent(EmployeeChangeEvent e) {
                updateForm(e.getSelected());
            }
        });


        // Create the button box to be housed on bottom of moderator to add and remove employees
        ModeratorButtonBox buttonBox = new ModeratorButtonBox(new EmployeeListChangeListener() {
            @Override
            public void fireNewEmployeeRequest(String name) {

                /*
                // Before we can create an employee, we need to get the next avaialble ID number for this organization
                 */
                int employee_id = TimeKeeperServer.current_org.getNextEmployeeID();
                if (employee_id == -1)  // The getNextEmployeeID() method returns -1 if it fails to find a next avaialable ID number.
                {
                    JOptionPane.showMessageDialog(null, "Failed to retrieve next available employee ID.");
                    return;
                }

                /*
                // Now that we have an ID number to use, create the employee's tracker and then the employee
                // Use the current organizations pay periods (the organizations getPayPeriods() method returns a copied array list!)
                // Use the argument 'name' for the employee's name
                 */
                Tracker new_tracker = new Tracker(employee_id);
                Employee new_employee = new Employee(employee_id, name, TimeKeeperServer.current_org.getPayPeriods(), new_tracker);

                /*
                // Add to the organization using organization manager
                 */
                if (TimeKeeperServer.current_org.addEmployee(new_employee)) {

                    // Refresh the employee list model data to reflect employee changes
                    updateEmpList();
                    FileManager.updateOrganizationManager();
                    JOptionPane.showMessageDialog(null, "New employee added. Assigned ID number: " + employee_id + ".");

                }
                else
                    JOptionPane.showMessageDialog(null, "Failed to add employee.");

            } // End new employee request event handling


            @Override
            public void fireEmployeeEditRequest() {
                // Edit employee button logic
                // From the current selection in the JList, get the employee's ID number
                int employee_id = list.getSelectedEmployeeId();

                // Verify an employee is selected. Will return -1 if null
                if (employee_id == -1) {
                    JOptionPane.showMessageDialog(null, "Verify you have selected an employee.");
                    return;
                }

                String current_name = TimeKeeperServer.current_org.getEmployee(employee_id).getName();

                String updated_name = JOptionPane.showInputDialog(null, "Edit Employee Name:", current_name);

                if (updated_name.equals(current_name)) {
                    // The name wasn't changed, cancel edit.
                    return;
                } else {
                    // The name was changed

                    // Get the selected employee and reset the name corresponding to user request
                    Employee selected = TimeKeeperServer.current_org.getEmployee(employee_id);
                    selected.setName(updated_name);

                    // Refresh the employee list model data to reflect employee changes
                    updateEmpList();

                    // Now that the name has been changed, the model needs to be refreshed to reflect changes
                    if (FileManager.updateOrganizationManager())
                        JOptionPane.showMessageDialog(null, "Employee name updated.");
                    else
                        JOptionPane.showMessageDialog(null, "Employee name updated, but the organization failed to update.");
                }
            } // End employee edit request event handling


            @Override
            public void fireRemoveEmployeeRequest() {
                // Remove employee button logic
                // From the current selection in the JList, get the employee's ID number
                int employee_id = list.getSelectedEmployeeId();

                // If employee ID returned -1, we failed to retrieve the employee (not selected?)
                if ( (employee_id != -1) && (TimeKeeperServer.current_org.removeEmployee(employee_id)) )
                {
                    // Refresh the employee list model data to reflect employee changes
                    updateEmpList();

                    // Save organization manager
                    if (FileManager.updateOrganizationManager())
                        JOptionPane.showMessageDialog(null, "Remove Succeeded.");
                    else
                        JOptionPane.showMessageDialog(null, "Remove succeeded, but the organization failed to update.");
                }
                else
                    JOptionPane.showMessageDialog(null, "Verify you have selected an employee.");
            } // End employee removal request event handling
        }); // End button box panel

        // Add panels to main Moderator frame
        add(list, BorderLayout.WEST);
        add(form, BorderLayout.CENTER);
        add(buttonBox, BorderLayout.SOUTH);

        // Define Moderator frame properties & display
        setSize(900, 720);

        setLocationRelativeTo(null);
        setVisible(true);
    }


    /**
     * Set form variables corresponding to newly selected employee.
     * @param selected employee selected to set 
     */
    private void updateForm(Employee selected) {
        form.update(selected);
    }


    /**
     * Calls refreshModel() method of the Moderator List panel.
     * Clears list model data and retrieves newest employee list.
     */
    private void updateEmpList() {
        list.refreshModel();
    }


    /**
     * Calls refreshModel() method of the Moderator Log panel
     * Clears list model data and retrieves newest action and hour entry lists
     */
    private void updateFormLists() {
        form.refreshModels();
    }
}





class ModeratorList extends JPanel {

    private JList employee_list;
    private DefaultListModel<Employee> empModel;
    private EmployeeChangeListener selectionListener;

    public ModeratorList(EmployeeChangeListener selectionListener) {

        this.selectionListener = selectionListener;

        empModel = new DefaultListModel<>();
        refreshModel();  // Populate model data

        employee_list = new JList(empModel);
        employee_list.setCellRenderer(new EmployeeListRenderer());
        employee_list.addListSelectionListener(e -> {

            // Verify the event isn't part of a series, we only need to run code block once not multiple times
            if (!e.getValueIsAdjusting()) {

                // New employee was selected, let's update the employee moderator label/information values with the newly selected employee's information

                // ---- //
                /* Fire employee selected event */
                Employee selected = (Employee)employee_list.getSelectedValue();

                // When removing an employee, the list selection may become null if that was the only employee. Verify not null before trying to fire an update
                EmployeeChangeEvent update = new EmployeeChangeEvent(this, selected);
                selectionListener.fireEmployeeChangedEvent(update);
            }

        });

        JScrollPane listPane = new JScrollPane(employee_list);

        // Define preferred width of scroll pane, the goal is to widen it out for readability of names
        int prefHeight = listPane.getPreferredSize().height;
        int prefWidth = listPane.getPreferredSize().width + 10;

        // Cap width at 150
        if (prefWidth > 150) {
            prefWidth = 150;
        }
        listPane.setPreferredSize(new Dimension(prefWidth, prefHeight));  // BorderLayout doesn't respect preferred height but that's fine, just want to widen it out

        // Add to ModeratorList pane
        setLayout(new BorderLayout());
        add(listPane, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(5,5,0,0));

        // Select first employee in the list
        employee_list.clearSelection();
        if (employee_list.getModel().getSize() >= 0)
            employee_list.setSelectedIndex(0);

    } // End constructor


    /**
     * Iterates through all employees stored in the currently selected organization.
     * If an employee is found in the organization, that is not contained in the model, it is then added.
     */
    public void refreshModel() {
        // Verify model is initialized
        if (empModel == null) {
            return;
        }

        // Prepare model for updating
        empModel.clear();

        // Fetch all current employees and iterate all entries
        // Add each entry to the model
        ArrayList<Employee> employees = TimeKeeperServer.current_org.getEmployees();
        for (Employee employee : employees) {
            empModel.addElement(employee);
        }

        // Clear the employee list after completion
        employees = null;
    }


    /**
     * To be called by the Moderator controller.
     * @return the list's selected employee's ID number, -1 if no employee selected
     */
    public int getSelectedEmployeeId() {
        if (employee_list.getSelectedValue() != null) {
            Employee selected = (Employee)employee_list.getSelectedValue();
            int id = selected.getEmployeeID();
            selected = null;
            return id;
        } else {
            return -1;
        }
    }
}
// End ModeratorList JPanel class





class ModeratorForm extends JPanel {

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

    private JButton modifyHours = new JButton("Modify Hours");

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
        // Action button box: Houses buttons for 'Adding', 'Editing', and 'Removing' employees
         */
        JPanel employeeActionButtonBox = new JPanel();
        employeeActionButtonBox.setLayout(new BoxLayout(employeeActionButtonBox, BoxLayout.PAGE_AXIS));
        employeeActionButtonBox.add(modifyHours);

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
     * @param selected the selected employee to obtain information from
     */
    public void update(Employee selected) {
        if (selected != null) {
            // Set labels corresponding to selected employee
            employeeNameLabel.setText(selected.getName());
            employeeIdLabel.setText(Integer.toString(selected.getEmployeeID()));
            employeePPHoursLabel.setText(Double.toString(round(selected.getPayPeriod(Calendar.getInstance()).getTotalHours())) + " Hours");   // Get rounded pay period hours
            employeeWeekHoursLabel.setText(Double.toString(round(selected.getTracker().getHours(TimeInterval.WEEKLY))) + " Hours");           // Get rounded weekly hours
            employeeTodayHoursLabel.setText(Double.toString(round(selected.getTracker().getHours(TimeInterval.TODAY))) + " Hours");           // Get rounded daily hours

            // Set model data in log panel to correspond with selected employee
            logPanel.setPayPeriods(selected.getPayPeriods());
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
     * Calls refreshModels() in the Moderator log panel
     * Dependant upon selected pay period, updates the actions and hours lists.
     */
    public void refreshModels() {
        logPanel.refreshModels();
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





class ModeratorButtonBox extends JPanel {

    private JButton add_employee;
    private JButton remove_employee;
    private JButton edit_employee;

    public ModeratorButtonBox(EmployeeListChangeListener requestListener) {

        add_employee = new JButton("Add New Employee");
        add_employee.addActionListener(e -> {

            String name = JOptionPane.showInputDialog(null, "Employee Name:");

            // If a name was entered, fire a new employee addition request
            if (name != null && !name.isEmpty())
                requestListener.fireNewEmployeeRequest(name);
        });

        edit_employee = new JButton("Edit Employee");
        edit_employee.addActionListener(e -> {

            requestListener.fireEmployeeEditRequest();

        });

        remove_employee = new JButton("Remove Employee");
        remove_employee.addActionListener(e -> {

            // Verify user action
            int confirm = JOptionPane.showConfirmDialog(null, "Warning: An employee cannot be undeleted. Continue?", "Confirm", JOptionPane.YES_NO_OPTION);

            // If continue, fire remove request
            if (confirm == JOptionPane.YES_OPTION)
                requestListener.fireRemoveEmployeeRequest();
        });

        setLayout(new FlowLayout());

        add(add_employee);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(edit_employee);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(remove_employee);
    }

}
// End ModeratorButtonBox JPanel class





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
        payPeriodsList.setCellRenderer(ppRenderer);
        payPeriodsList.addListSelectionListener(e -> {
            // Pay period was selected, populate action entries and hour entries lists
            refreshModels();
        });

        // Add list to scroll pane and limit size
        JScrollPane payPeriodsPane = new JScrollPane(payPeriodsList);
        payPeriodsPane.setPreferredSize(new Dimension(200,275));

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
        actionsList.setCellRenderer(actionsRenderer);

        // Add list to scroll pane and limit size
        JScrollPane actionsListPane = new JScrollPane(actionsList);
        actionsListPane.setPreferredSize(new Dimension(200,275));


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

            // Display the new action prompt
            ActionPrompt prompt = new ActionPrompt(new NewActionListener() {
                @Override
                public void fireNewActionSubmission(Calendar date, EmployeeAction action) {
                    logListener.fireActionEntryAddition(date, action);
                }
            });

            prompt.showNewActionPrompt();
        });

        removeActionEntry.addActionListener(e -> {
            Map.Entry<Calendar, EmployeeAction> selectedAction = (Map.Entry<Calendar, EmployeeAction>)actionsList.getSelectedValue();

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
        hoursList.setCellRenderer(hoursRenderer);

        // Add to scroll pane and limit size
        JScrollPane hoursListPane = new JScrollPane(hoursList);
        hoursListPane.setPreferredSize(new Dimension(200, 275));


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

            HoursPrompt prompt = new HoursPrompt(new NewHoursEventListener() {
                @Override
                public void fireNewHoursEventSubmission() {
                    System.out.println("Debug: Submitted!");
                }

                @Override
                public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions() {
                    return logListener.retrieveEmployeeActions();
                }


            });
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
        LinkedHashMap<Calendar, EmployeeAction> filteredActionsMap = getFilteredActionEntries(selected.getStartDate(), selected.getEndDate(), actions);

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
    private LinkedHashMap<Calendar, EmployeeAction> getFilteredActionEntries(Calendar payPeriodStart, Calendar payPeriodEnd, LinkedHashMap<Calendar, EmployeeAction> actions) {

        // Initialize a map to add entries that fit between specified dates to
        LinkedHashMap<Calendar, EmployeeAction> filteredEntries = new LinkedHashMap<>();

        // Iterate all action entries, add the entry if fits between specified pay period date
        for (Map.Entry<Calendar, EmployeeAction> entry : actions.entrySet()) {

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








/*
// EVENT AND CHANGE LISTENER DEFINITIONS
 */

// Event Objects

class EmployeeChangeEvent extends EventObject {

    private Employee selected;

    public EmployeeChangeEvent(Object source, Employee selected) {
        super(source);
        this.selected = selected;
    }

    public Employee getSelected() {
        return selected;
    }

}

class EmployeeListChangeEvent extends EventObject {

    public EmployeeListChangeEvent(Object source) {
        super(source);
    }

}


// Listener interface definitions

interface EmployeeChangeListener {
    public void fireEmployeeChangedEvent(EmployeeChangeEvent e);
}

interface EmployeeListChangeListener {
    public void fireNewEmployeeRequest(String name);
    public void fireEmployeeEditRequest();
    public void fireRemoveEmployeeRequest();
}

interface EmployeeLogListener {
    public void fireActionEntryRemoval(Calendar date, EmployeeAction action);
    public void fireActionEntryAddition(Calendar date, EmployeeAction action);
    public void fireHourEntryRemoval(Calendar date, Double hours);
    public void fireHourEntryAddition(Calendar date, Double hours);
    public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions();
}

interface NewActionListener {
    public void fireNewActionSubmission(Calendar date, EmployeeAction action);
}

interface NewHoursEventListener {
    public void fireNewHoursEventSubmission();
    public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions();
}