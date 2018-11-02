package com.austinramsay.timekeeperobjects;

import com.austinramsay.timekeeperobjects.EmployeeAction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Tracker implements Serializable {

    private final int employee_id;
    private final LinkedHashMap<Calendar, EmployeeAction> action_log;
    private final LinkedHashMap<Calendar, Double> hours_log;
    private Calendar lastEvent;

    public Tracker(int employee_id) {
        this.employee_id = employee_id;
        this.action_log = new LinkedHashMap<>();
        this.hours_log = new LinkedHashMap<>();
    }





    /**
     * @return the employee ID assigned to this tracker
     */
    public int getEmployeeID() {
        return employee_id;
    }





    /**
     * @return the last recorded event time
     */
    public Calendar getLastEvent() {
        return lastEvent;
    }





    /**
     * Add a new update to the tracker
     * @param action Clocking in / Clocking out
     * @param time Calendar time when action occurred
     */
    public void addUpdate(Calendar time, EmployeeAction action) {

        /*
        // Create a new calendar to set as the latest event for this employee
        // Use parameters from the 'time' argument
         */
        Calendar latestEvent = Calendar.getInstance();
        latestEvent.set(
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                time.get(Calendar.SECOND));
        this.lastEvent = latestEvent;


        // Add the event to the employee's records
        action_log.put(time, action);

    }





    /**
     * Add hours for a specific date
     * @param date Current calendar date
     * @param hours Hours to be added
     */
    public void addHours(Calendar date, double hours) {

        /*
        // Add a new hours log with corresponding hours
         */
        hours_log.put(date, hours);

    }





    /**
     * Get hours for specified time period
     */
    public double getHours(TimeInterval interval) {

        // Use matching_hours to add hours from specified interval. This value will be returned at end of method
        double matching_hours = 0;

        // Client is requesting the amount of hours clocked for today
        if (interval == TimeInterval.TODAY) {

            // User requesting hours for today
            Calendar today = Calendar.getInstance();

            // Iterate all entries
            // The map structure is a calendar for the day added as the key, and the amount of hours as the value
            for (Map.Entry<Calendar, Double> entry : hours_log.entrySet()) {

                // Iterate the entries and check if entries match today
                Calendar entry_date = entry.getKey();

                if (compareCalendar(today, entry_date)) {
                    // The entry date matches today
                    // Add these hours to our matching hours
                    matching_hours += entry.getValue();
                }

            }

            // Client is requesting amount of hours clocked for the week
        } else if (interval == TimeInterval.WEEKLY) {

            // User requesting hours for the week
            Calendar today = Calendar.getInstance();

            // Create alignment variable to subtract needed amount of days to get back to Monday
            int alignment = 0;

            // Determine current day of week
            int day = today.get(Calendar.DAY_OF_WEEK);

            // Find what the current day is, subtract needed amount of days to get back to Monday
            if (day == Calendar.MONDAY) {
                alignment = 0;
            } else if (day == Calendar.TUESDAY) {
                alignment = -1;
            } else if (day == Calendar.WEDNESDAY) {
                alignment = -2;
            } else if (day == Calendar.THURSDAY) {
                alignment = -3;
            } else if (day == Calendar.FRIDAY) {
                alignment = -4;
            } else if (day == Calendar.SATURDAY) {
                alignment = -5;
            } else if (day == Calendar.SUNDAY) {
                alignment = -6;
            }

            // Perform subtraction of days to roll back to Monday
            today.roll(Calendar.DAY_OF_WEEK, alignment);

            // Iterate 7 times for each day of the week, adding one day each iteration
            for (int i = 0; i < 7; i++) {

                // Iterate entries for this day of the week
                for (Map.Entry<Calendar, Double> entry : hours_log.entrySet()) {

                    // Get the calendar entry date for this entry
                    Calendar entry_date = entry.getKey();

                    // Check if the date matches the current day of week for this iteration
                    if (compareCalendar(today, entry_date)) {
                        matching_hours += entry.getValue();
                    }

                } // End inner for loop

                // Increase the day by one to iterate next
                today.roll(Calendar.DAY_OF_WEEK, 1);

            } // End outer for loop

        } // End else if for WEEKLY interval


        // Return the matched hours
        return matching_hours;

    }


    /**
     * @return the action log of the corresponding employee
     */
    public LinkedHashMap<Calendar, EmployeeAction> getActionLog() {
        return action_log;
    }





    /**
     * @return the hours log of the corresponding employee
     */
    public LinkedHashMap<Calendar, Double> getHoursLog() {
        return hours_log;
    }





    /**
     * Compare the month, day, and year of two calendars.
     * @return true if dates match, false if not
     */
    private boolean compareCalendar(Calendar date1, Calendar date2) {

        if (date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR))
            return false;

        if (date1.get(Calendar.MONTH) != date2.get(Calendar.MONTH))
            return false;

        if (date1.get(Calendar.DAY_OF_MONTH) != date2.get(Calendar.DAY_OF_MONTH))
            return false;

        return true;
    }

}
