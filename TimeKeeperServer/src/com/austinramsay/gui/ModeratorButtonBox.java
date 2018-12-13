package com.austinramsay.gui;

import com.austinramsay.events.EmployeeListChangeListener;
import javax.swing.*;
import java.awt.*;

/**
 * Provides user with buttons in the Moderator to 'add', 'remove', or 'edit' a selected employee.
 * Fires an EmployeeListChangeListener event upon button action.
 * This class does get a 'name' parameter through a JOptionPane upon adding an employee, to pass to the controller to create a new employee.
 * This class does confirm user action before firing an employee removal request.
 */
public class ModeratorButtonBox extends JPanel {

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


    /**
     * Disables the 'Edit Employee' and 'Remove Employee' buttons.
     * Perform when no employee is selected.
     */
    public void disableButtons() {
        edit_employee.setEnabled(false);
        remove_employee.setEnabled(false);
    }


    /**
     * Enables the 'Edit Employee' and 'Remove Employee' buttons.
     * Perform when an employee is selected.
     */
    public void enableButtons() {
        edit_employee.setEnabled(true);
        remove_employee.setEnabled(true);
    }
}
// End ModeratorButtonBox JPanel class


