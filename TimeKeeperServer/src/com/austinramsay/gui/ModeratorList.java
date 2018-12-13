package com.austinramsay.gui;

import com.austinramsay.events.EmployeeChangeEvent;
import com.austinramsay.events.EmployeeChangeListener;
import com.austinramsay.model.EmployeeListRenderer;
import com.austinramsay.timekeeper.Employee;
import com.austinramsay.controller.TimeKeeperServer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * The moderator list pane creates a JPanel to house and maintain a list of employees in the organization
 * Automatically refreshes/populates the list model upon initialization by fetching static current organization employee list
 * There is no reason to manually populate the list upon first initialization.
 * Upon an employee selection in the list, fires an event to the controller to fetch newly selected employee and update GUI labels and information accordingly
 * Provides a method to return the list's selected employee's ID number
 */
public class ModeratorList extends JPanel {

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

                if (selected != null) {
                    // Check if employee is clocked in
                    // Display warning if they are - as calculations for hours are based off the last tracker event, adding/removing entries may cause problems
                    if (selected.isClockedIn()) {
                        JOptionPane.showMessageDialog(null, "Warning: This employee is clocked in. Making changes now may cause calculation issues when employee clocks out.");
                    }
                }
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





