package com.austinramsay.controller;

import com.austinramsay.events.EmployeeChangeEvent;
import com.austinramsay.events.EmployeeChangeListener;
import com.austinramsay.events.EmployeeListChangeListener;
import com.austinramsay.events.EmployeeLogListener;
import com.austinramsay.exceptions.PayPeriodOutOfBoundsException;
import com.austinramsay.timekeeper.*;
import com.austinramsay.managers.FileManager;
import com.austinramsay.gui.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * This is basically the Moderator controller
 */
public class Moderator extends JFrame {

    private ModeratorForm form;
    private ModeratorList list;
    private ModeratorButtonBox buttonBox;

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

                if (selected == null) {
                    return;
                }

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
                    JOptionPane.showMessageDialog(null, "Failed to remove the hours entry from the tracker. Hours not affected.");
                    return;
                }

                // Get the matching pay period and subtract hours
                try {
                    selected.getPayPeriod(date).subtractHours(hours);
                } catch(NullPointerException npe) {
                    JOptionPane.showMessageDialog(null, "Failed to modify pay period hours. However, the tracker hours were updated.");
                    return;
                } catch(PayPeriodOutOfBoundsException ppe) {
                    // Failed to find pay period to match today's date
                    TimeKeeperServer.broadcast("Message: Pay periods out of bounds. Attempting to repair...");

                    // Let's attempt to build more pay periods to fill the gap
                    TimeKeeperServer.current_org.addNewPayPeriods();

                    // Re-attempt entry placement
                    fireHourEntryAddition(date, hours);
                }

                // Refresh the actions & hours list models to reflect changes
                updateFormLists();
                updateForm(selected, false);
            }


            @Override
            public void fireHourEntryAddition(Calendar date, Double hours) {

                // Fetch the selected employee
                int employee_id = list.getSelectedEmployeeId();
                Employee selected = TimeKeeperServer.current_org.getEmployee(employee_id);

                // Get the hours log map from the employee tracker & add new hours
                LinkedHashMap<Calendar, Double> hoursLog = selected.getTracker().getHoursLog();
                hoursLog.put(date, hours);

                // Get the matching pay period and add hours
                try {
                    selected.getPayPeriod(date).addHours(hours);
                } catch(NullPointerException npe) {
                    JOptionPane.showMessageDialog(null, "Failed to modify pay period hours. However, the tracker hours were updated.");
                    return;
                } catch(PayPeriodOutOfBoundsException ppe) {
                    // Failed to find pay period to match today's date
                    TimeKeeperServer.broadcast("Message: Pay periods out of bounds. Attempting to repair...");

                    // Let's attempt to build more pay periods to fill the gap
                    TimeKeeperServer.current_org.addNewPayPeriods();

                    // Re-attempt entry placement
                    fireHourEntryAddition(date, hours);
                }

                // Refresh the actions & hours list models to reflect changes
                updateFormLists();
                updateForm(selected, false);
            }


            @Override
            public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions() {

                // Fetch the selected employee ID
                int employee_id = list.getSelectedEmployeeId();

                // Determine selected pay period dates to prepare for retrieving corresponding events (if selected)
                PayPeriod selected = form.getSelectedPayPeriod();

                if (selected == null) {
                    return null;
                }

                Calendar payPeriodStart = selected.getStartDate();
                Calendar payPeriodEnd = selected.getEndDate();

                // We want to get a filtered list of action clock in/out events between given pay period time frame
                // Supply the dates, and retrieve the selected employee's action log from tracker to supply for filtering
                return form.getClockInEvents(payPeriodStart, payPeriodEnd, TimeKeeperServer.current_org.getEmployee(employee_id).getTracker().getActionLog()); // need the selected pay period dates for arguments here
            }
        });



        // Create the JPanel containing the JList of the organization's employees
        list = new ModeratorList(new EmployeeChangeListener() {
            @Override
            public void fireEmployeeChangedEvent(EmployeeChangeEvent e) {
                updateForm(e.getSelected(), true);
                if (e.getSelected() == null) {
                    form.setAddActionButton(false);
                    buttonBox.disableButtons();
                } else {
                    form.setAddActionButton(true);
                    buttonBox.enableButtons();
                }
            }
        });



        // Create the button box to be housed on bottom of moderator to add and remove employees
        buttonBox = new ModeratorButtonBox(new EmployeeListChangeListener() {
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
                // Create the corrections storage array to be assigned to the new employee
                // Use the argument 'name' for the employee's name
                 */
                Tracker new_tracker = new Tracker(employee_id);
                ArrayList<CorrectionRequest> corrections = new ArrayList<>();

                // Create new employee
                Employee new_employee = new Employee(employee_id, name, TimeKeeperServer.current_org.getPayPeriods(), new_tracker, corrections);

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

                if (updated_name == null || updated_name.equals(current_name)) {
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

        // NOTE: Set button box buttons default to disabled
        buttonBox.disableButtons();


        // Add panels to main Moderator frame
        add(list, BorderLayout.WEST);
        add(form, BorderLayout.CENTER);
        add(buttonBox, BorderLayout.SOUTH);

        // Define Moderator frame properties & display
        setSize(920, 720);

        setLocationRelativeTo(null);
        setVisible(true);
    }


    /**
     * Set form variables corresponding to newly selected employee.
     * @param selected employee selected to set 
     */
    private void updateForm(Employee selected, boolean refreshPayPeriods) {
        form.update(selected, refreshPayPeriods);
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



