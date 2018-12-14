package com.austinramsay.model;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.managers.TimeManager;
import com.austinramsay.timekeeper.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author austinramsay
 */
public class Organization implements Serializable {

    /*
    // When a new employee is created, the employee's pay periods should be created using the set pay periods from this organization
    */
    private final ArrayList<Employee> employees;
    private final ArrayList<PayPeriod> payperiods;
    private final ArrayList<Tracker> employee_trackers;
    private final Recurrence payPeriodRecurrence;
    private final String name;
    public Organization(String name, Recurrence payPeriodRecurrence, ArrayList<PayPeriod> payperiods)
    {
        this.employees = new ArrayList<>();
        this.payperiods = new ArrayList(payperiods);
        this.employee_trackers = new ArrayList<>();
        this.payPeriodRecurrence = payPeriodRecurrence;
        this.name = name;
    }


    /**
     * @return The organization's name
     */
    public String getName()
    {
        return this.name;
    }


    /**
     * @return the next available employee ID number
     */
    public int getNextEmployeeID() {

        /*
        // Iterate each employee and set an available=false flag if an employee matches an ID number of 'i'
        // At the end of iterating, if available is still holding true for that value of 'i', this is our next available ID number
        // Available should be set true at the beginning of testing for each of value of 'i'
         */
        boolean available;
        for (int i = 0; i < (employees.size() + 1); i++)
        {
            available = true;
            for (Employee current_employee : employees)
            {
                if (current_employee.getEmployeeID() == i) {
                    available = false;
                    break;
                }
            }

            if (available)
                return i;
        }

        // If for some reason the search failed, return -1
        // We'll need to check if we get this value before assigning it to an employee
        return -1;
    }


    /**
     * Add a collection of pay periods to the current list.
     * This may be useful for when a PayPeriodsOutOfBoundsException is thrown, and we need to expand our range.
     * NOTE: This forces update to all organization employees as well.
     */
    public void addNewPayPeriods() {
        // Retrieve pay period with latest end date to determine where to begin new pay period list
        int lastIndex = (payperiods.size() - 1);
        Calendar lastEntry = payperiods.get(lastIndex).getEndDate();

        // Add one day to the last entry to set start date for new pay periods
        lastEntry.add(Calendar.DAY_OF_MONTH, 1);

        // Build new pay periods with TimeManager using known recurrence from organization initialization
        ArrayList<PayPeriod> newPayPeriods = TimeManager.buildPayPeriods(lastEntry, this.payPeriodRecurrence);

        // Send message to traffic log
        TimeKeeperServer.broadcast(String.format("Message: %d new pay periods created.", newPayPeriods.size()));

        // Add new pay periods to our current list in the organization
        payperiods.addAll(newPayPeriods);

        // Use new array list to hold copied versions of new pay periods, to be placed into the employees lists
        ArrayList<PayPeriod> payperiods_created;

        // Force update to all organization employees
        for (Employee employee : this.employees) {

            // Create a new list for each employee
            // Using the pay period copy constructor, create a full new list of pay periods for the employee
            // After creating a deep copy of each pay period, add it to the arraylist to be returned
            payperiods_created = new ArrayList<>();
            for (PayPeriod period : newPayPeriods) {
                payperiods_created.add(new PayPeriod(period));
            }

            // Add the new list to this iteration of employee
            employee.addNewPayPeriods(payperiods_created);
        }
    }


    /**
     * NOTE: This returns a COPY of the pay periods for the organization.
     * This is meant to be used when assigning pay periods to a new employee.
     * @return a copy of the pay periods defined for this specific organization
     */
    public ArrayList<PayPeriod> getPayPeriods()
    {
        // Create a new arraylist to return after iteration
        ArrayList<PayPeriod> payperiods_created = new ArrayList<>();

        // Using the pay period copy constructor, create a full new list of pay periods for the employee
        // After creating a deep copy of each pay period, add it to the arraylist to be returned
        for (PayPeriod period : payperiods) {
            payperiods_created.add(new PayPeriod(period));
        }

        // Return created list
        return payperiods_created;
    }


    /**
     * @return the full list of this organization's employees
     */
    public ArrayList<Employee> getEmployees() { return (new ArrayList<>(employees)); }


    /**
     * Find and return the requested employee
     * @param employee_id ID number
     * @return the employee matching the ID number
     */
    public Employee getEmployee(int employee_id) {
        /*
        // Iterate the employee list and return the match
         */
        for (Employee employee : employees)
        {
            if (employee.getEmployeeID() == employee_id)
                return employee;
        }


        /*
        // If we've reached this point,
        // Failed to find requested employee
         */
        return null;
    }


    /**
     * @param index the index of the employee in the organizations employee list
     * @return the employee located at index provided
     */
    public Employee getEmployeeByIndex(int index) {
        /*
        // Extract employee at provided index
        // Verify an index is provided that is in range of the ArrayList
         */
        if (employees.size() < index || index < 0)
            return null;

        return employees.get(index);
    }


    /**
     * Add new employee to this organization.
     * Creates a tracker for the employee assigned by the employee ID.
     * The new employee should use the set pay periods from this organization before being added
     * @param newEmployee the built employee to be added
     * @return true if add succeeded, false if not
     */
    public boolean addEmployee(Employee newEmployee)
    {
        return employees.add(newEmployee);
    }


     /**
     * Remove old employee from this organization
     * @param employee_id the unique ID of the employee to be removed
     * @return true if remove succeeded, false if not
     */   
    public boolean removeEmployee(int employee_id) {

        ArrayList<Employee> toRemove = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getEmployeeID() == employee_id) {
                toRemove.add(employee);
                break;
            }
        }

        // If remove succeeds, trim size of arraylist and return true
        if (employees.removeAll(toRemove)) {
            employees.trimToSize();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Override the toString method so when the organization is pulled in server start to populate the JList, it returns the name of the organization
     * @return The organization name
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
