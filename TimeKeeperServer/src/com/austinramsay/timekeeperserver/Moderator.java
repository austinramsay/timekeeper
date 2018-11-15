package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.Employee;
import com.austinramsay.timekeeperobjects.TimeInterval;
import com.austinramsay.timekeeperobjects.Tracker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventObject;
import java.util.Vector;

public class Moderator extends JFrame {

    private ModeratorForm form;

    public Moderator() {

        super("Moderator");


        // Create the input field and buttons for employee specific information
        form = new ModeratorForm();


        // Create the JPanel containing the JList of the organization's employees
        ModeratorList listPanel = new ModeratorList(new EmployeeChangeListener() {
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

                    FileManager.updateOrganizationManager();
                    JOptionPane.showMessageDialog(null, "New employee added. Assigned ID number: " + employee_id + ".");

                }
                else
                    JOptionPane.showMessageDialog(null, "Failed to add employee.");

            }


            @Override
            public void fireEmployeeEditRequest() {
                // Edit employee button logic
                // From the current selection in the JList, get the employee's ID number
                int employee_id = listPanel.getSelectedEmployeeId();

                // Verify an employee is selected. Will return -1 if null
                if (employee_id == -1) {
                    JOptionPane.showMessageDialog(null, "Verify you have selected an employee.");
                    return;
                }

                String current_name = TimeKeeperServer.current_org.getEmployeeByIndex(employee_id).getName();

                String updated_name = JOptionPane.showInputDialog(null, "Edit Employee Name:", current_name);

                if (updated_name.equals(current_name)) {
                    // The name wasn't changed, cancel edit.
                    return;
                } else {
                    // The name was changed
                    Employee selected = TimeKeeperServer.current_org.getEmployeeByIndex(employee_id);
                    selected.setName(updated_name);
                    if (FileManager.updateOrganizationManager())
                        JOptionPane.showMessageDialog(null, "Employee name updated.");
                    else
                        JOptionPane.showMessageDialog(null, "Employee name updated, but the organization failed to update.");
                }
            }


            @Override
            public void fireRemoveEmployeeRequest() {
                // Remove employee button logic
                // From the current selection in the JList, get the employee's ID number
                int employee_id = listPanel.getSelectedEmployeeId();

                // If employee ID returned -1, we failed to retrieve the employee (not selected?)
                if ( (employee_id != -1) && (TimeKeeperServer.current_org.removeEmployee(employee_id)) )
                {
                    if (FileManager.updateOrganizationManager())
                        JOptionPane.showMessageDialog(null, "Remove Succeeded.");
                    else
                        JOptionPane.showMessageDialog(null, "Remove succeeded, but the organization failed to update.");
                }
                else
                    JOptionPane.showMessageDialog(null, "Verify you have selected an employee.");
            }
        });


        add(listPanel, BorderLayout.WEST);
        add(form, BorderLayout.CENTER);
        add(buttonBox, BorderLayout.SOUTH);


        setSize(600, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void updateForm(Employee selected) {
        form.update(selected);
    }

}



class ModeratorList extends JPanel {

    private JList employee_list;
    private EmployeeChangeListener selectionListener;

    public ModeratorList(EmployeeChangeListener selectionListener) {

        this.selectionListener = selectionListener;

        setLayout(new BorderLayout());

        // Create an array containing all employees
        ArrayList<Employee> employees = TimeKeeperServer.current_org.getEmployees();

        employee_list = new JList(new Vector<Employee>(employees));
        employee_list.setCellRenderer(new EmployeeListRenderer());
        employee_list.addListSelectionListener(e -> {

            // Verify the event isn't part of a series, we only need to run code block once not multiple times
            if (!e.getValueIsAdjusting()) {

                // New employee was selected, let's update the employee moderator label/information values with the newly selected employee's information

                // ---- //
                /* Fire employee selected event */
                EmployeeChangeEvent update = new EmployeeChangeEvent(this, (Employee)employee_list.getSelectedValue());
                selectionListener.fireEmployeeChangedEvent(update);
            }

        });

        add(new JScrollPane(employee_list), BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(5,5,0,0));

        // Select first employee in the list
        employee_list.clearSelection();
        if (employee_list.getModel().getSize() >= 0)
            employee_list.setSelectedIndex(0);

    }

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



class ModeratorForm extends JPanel {

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

    public ModeratorForm() {

        Dimension size = getPreferredSize();
        size.width = 250;
        setPreferredSize(size);

        Border padding = BorderFactory.createEmptyBorder(5,5,0,5);
        Border titled = BorderFactory.createTitledBorder("Employee Information");
        setBorder(BorderFactory.createCompoundBorder(padding, titled));

        JPanel leftColumn = new JPanel();
        leftColumn.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        JPanel employeeActionButtonBox = new JPanel();
        employeeActionButtonBox.setLayout(new BoxLayout(employeeActionButtonBox, BoxLayout.PAGE_AXIS));
        employeeActionButtonBox.add(modifyHours);

        JPanel compounded = new JPanel();
        compounded.setLayout(new BorderLayout());
        compounded.add(leftColumn, BorderLayout.WEST);
        compounded.add(Box.createRigidArea(new Dimension(15,0)), BorderLayout.CENTER);
        compounded.add(rightColumn, BorderLayout.EAST);
        compounded.add(employeeActionButtonBox, BorderLayout.SOUTH);

        add(compounded);
    }


    public void update(Employee selected) {
        employeeNameLabel.setText(selected.getName());
        employeeIdLabel.setText(Integer.toString(selected.getEmployeeID()));
        employeePPHoursLabel.setText(Double.toString(selected.getPayPeriod(Calendar.getInstance()).getTotalHours()) + " Hours");
        employeeWeekHoursLabel.setText(Double.toString(selected.getTracker().getHours(TimeInterval.WEEKLY)) + " Hours");
        employeeTodayHoursLabel.setText(Double.toString(selected.getTracker().getHours(TimeInterval.TODAY)) + " Hours");
    }

}



class ModeratorButtonBox extends JPanel {

    private JButton add_employee;
    private JButton remove_employee;
    private JButton edit_employee;

    public ModeratorButtonBox(EmployeeListChangeListener requestListener) {

        add_employee = new JButton("Add New Employee");
        add_employee.addActionListener(e -> {

            String name = JOptionPane.showInputDialog(null, "Employee Name:");

            // If a name was entered, fire a new employee addition request
            if (name == null || !name.isEmpty())
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

    public void setButtonListener() {

    }

}



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



interface EmployeeChangeListener {
    public void fireEmployeeChangedEvent(EmployeeChangeEvent e);
}



interface EmployeeListChangeListener {
    public void fireNewEmployeeRequest(String name);
    public void fireEmployeeEditRequest();
    public void fireRemoveEmployeeRequest();
}