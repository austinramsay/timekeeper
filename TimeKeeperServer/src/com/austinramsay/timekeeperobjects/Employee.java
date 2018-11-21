
package com.austinramsay.timekeeperobjects;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author austinramsay
 */
public class Employee implements Serializable {

    private final int employee_id;
    private String name;
    private boolean clocked_in;
    private final Tracker tracker;
    private final ArrayList<PayPeriod> payperiods;
    public Employee(int employee_id, String name, ArrayList<PayPeriod> payperiods, Tracker tracker)
    {
        this.employee_id = employee_id;
        this.payperiods = payperiods;
        this.tracker = tracker;
        this.name = name;
    }





    /**
     * @return the employee's unique ID number assigned at creation
     */
    public int getEmployeeID()
    {
        return employee_id;
    }





    /**
     * @return The employee's name
     */
    public String getName()
    {
        return name;
    }





    /**
     * @return The employee's tracker
     */
    public Tracker getTracker() { return tracker; }





    /**
     * @return The employee's list of pay periods
     */
    public ArrayList<PayPeriod> getPayPeriods() {
        return payperiods;
    }





    /**
     * Change the employee's name
     * @param newName the employee's updated name
     */
    public void setName(String newName) {
        this.name = newName;
    }





    /**
     * Set the clocked in variable for the employee
     * If clocking in, adds a tracker clock-in event and returns zero.
     * If clocking out, determines hours clocked by comparing time now versus time of last event.
     * Adds hours amount to the tracker, as well as a clock-out event.
     * Fetches current pay period, and adds calculated hours in.
     * Warning: Modifying an employee's hours while they are clocked in will affect calculation of clock out time!
     * @param clocked_in True if clocked in; False if not clocked in
     * @return The number of hours recorded. Returns zero if clocking in.
     */
    public double setClockedIn(boolean clocked_in)
    {
        // Set employee status
        this.clocked_in = clocked_in;


        // Get the current time to record event time in tracker
        Calendar current_time = Calendar.getInstance();


        if (clocked_in) {

            // Add a clocked in event to the tracker
            // Adding the event will automatically set the last known event in tracker
            tracker.addUpdate(current_time, EmployeeAction.CLOCKIN);

            return 0;

        } else {

            // Employee is clocking out

            // Resolve amount of hours the employee has worked
            // To resolve the amount of hours, we need to compare the time the employee clocked in versus the time now
            // We can retreive the last known event by the employee from the tracker

            // We will add an event to our action log (to be used for reports of an employee), add the hours to our hours log, and then add to the total pay period
            Calendar clocked_in_time = tracker.getLastEvent();

            // Compare current time to clocked in time
            long time = current_time.getTimeInMillis() - clocked_in_time.getTimeInMillis();

            // Convert time in milliseconds to hours to prepare to add to pay period
            // Millis / 1000 = seconds
            // Seconds / 60 = minutes
            // Minutes / 60 = hours
            double hours = (double)time / (1000 * 60 * 60);

            // Now that hours are calculated, create an update in our hours log
            tracker.addHours(current_time, hours);

            // Add a clocked out action event to the tracker
            tracker.addUpdate(current_time, EmployeeAction.CLOCKOUT);

            // Add the hours to the pay period
            PayPeriod current_pay_period = getPayPeriod(current_time);
            current_pay_period.addHours(hours);

            return hours;
        }
    }





    /**
     * @return The employee's current time state
     */
    public boolean isClockedIn()
    {
        return clocked_in;
    }





    /**
     * Attempt to find requested pay period to add to, if no pay period is matched, returns false.
     * If found, adds the hours to the employee's pay period and updates, and returns true.
     * Note: Request start and end dates from the server when using this method.
     * @param date the current date <b>requested from the server</b>
     * @return true if found pay period and added hours, false if failed
     */
    public boolean addToCurrentPayPeriod(Calendar date, double hours)
    {
        // First, we need to get the pay period before we can modify it
        PayPeriod current = getPayPeriod(date);


        // Verify the pay period was found, if the object is null the pay period wasn't found.
        // In this case, we can't modify it. Return false (failed)
        if (current == null)
            return false;


        // Add the requested amount of hours in
        current.addHours(hours);

        return true;
    }





    /**
     * Attempt to find the requested pay period of the employee. Returns null if no pay period was found.
     * @param date the date to match a pay period to
     * @return the employee's pay period between the requested date
     */
    public PayPeriod getPayPeriod(Calendar date)
    {
        // Attempt to find the requested pay period with a matching start/end date
        for (PayPeriod payperiod : payperiods)
        {
            Calendar start = payperiod.getStartDate();
            Calendar end = payperiod.getEndDate();

            // Get the pay periods month, day, and year. Determine if the date to match fits in between these days
            if (start.before(date) && end.after(date)) {
                return payperiod;
            }

            // The date didn't fit between the start and end date, check if the date lays on the pay period end date
            // Get the year, month, and date for all 3 fields (the date passed into the method, the pay period start date, and the pay period end date)
            int year = date.get(Calendar.YEAR); int month = date.get(Calendar.MONTH); int day = date.get(Calendar.DAY_OF_MONTH);
            int start_year = start.get(Calendar.YEAR); int start_month = start.get(Calendar.MONTH); int start_day = start.get(Calendar.DAY_OF_MONTH);
            int end_year = end.get(Calendar.YEAR); int end_month = end.get(Calendar.MONTH); int end_day = end.get(Calendar.DAY_OF_MONTH);

            // Check start date compared to today
            if ((start_year == year) && (start_month == month) && (start_day == day)) {
                return payperiod;
            }

            // Check end date compared to today
            if ((end_year == year) && (end_month == month) && (end_day == day)) {
                return payperiod;
            }
        }


        // The loop failed to match a pay period, return null
        return null;
    }





    /**
     * Override the equals method for employee comparison tests
     * @return true if the employees have matching ID numbers
     */
    @Override
    public boolean equals(Object compareTo)
    {
        // Verify the comparable employee is not null
        if (compareTo == null)
            return false;


        // Verify that an employee is being attempted to compare
        if (!(compareTo instanceof Employee))
            return false;


        // Cast the employee object
        Employee comparedEmployee = (Employee)compareTo;


        // Compare the employee's ID numbers
        // Return true if match, false if not
        return (comparedEmployee.getEmployeeID() == this.getEmployeeID());
    }


}
