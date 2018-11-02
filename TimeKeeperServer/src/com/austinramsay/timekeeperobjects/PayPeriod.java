
package com.austinramsay.timekeeperobjects;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Records total_hours in a pay period for an individual employee
 * @author austinramsay
 */
public class PayPeriod implements Serializable {

    /*
    // Copy constructor
    // Used when building initial pay periods to avoid pass-by-reference
    */
    private final Calendar start;
    private final Calendar end;
    private double total_hours;
    public PayPeriod(PayPeriod copy)
    {
        // Copy the start date of the other pay period
        this.start = Calendar.getInstance();
        this.start.set(
                copy.getStartDate().get(Calendar.YEAR),
                copy.getStartDate().get(Calendar.MONTH),
                copy.getStartDate().get(Calendar.DAY_OF_MONTH)
        );


        // Copy the end date of the other pay period
        this.end = Calendar.getInstance();
        this.end.set(
                copy.getEndDate().get(Calendar.YEAR),
                copy.getEndDate().get(Calendar.MONTH),
                copy.getEndDate().get(Calendar.DAY_OF_MONTH)
        );


        // Copy the amount of total_hours from the pay period
        this.total_hours = Double.valueOf(total_hours);
    }





    /*
    // Default constructor
    */
    public PayPeriod(Calendar start, Calendar end)
    {
        this.start = start;
        this.end = end;
        this.total_hours = 0;
    }





    /**
     * Used when comparing two pay periods
     * @return the calendar start date of the pay period
     */
    public Calendar getStartDate()
    {
        return start;
    }





    /**
     * Used when comparing two pay periods
     * @return the calendar end date of the pay period
     */
    public Calendar getEndDate()
    {
        return end;
    }





    /**
     * Keys: year_start, year_end, month_start, month_end, day_start, day_end <br>
     * Returns a HashMap to use with keys stated above to get pay period information
     * @return map of pay period dates
     */
    public HashMap<String, Integer> getDateInformation()
    {
        HashMap<String, Integer> information = new HashMap<>();
        information.put("year_start", start.get(Calendar.YEAR));
        information.put("year_end", end.get(Calendar.YEAR));
        information.put("month_start", start.get(Calendar.MONTH));
        information.put("month_end", end.get(Calendar.MONTH));
        information.put("day_start", start.get(Calendar.DAY_OF_MONTH));
        information.put("day_end", end.get(Calendar.DAY_OF_MONTH));

        return information;
    }





    /**
     * User clocked out or total_hours are being edited.
     * @param hours the amount of total_hours to be added
     */
    public void addHours(double hours)
    {
        this.total_hours += hours;
    }





    /**
     * Define a specific amount of total_hours for this pay period.
     * Useful if an employee failed to clock in/out and time needs to be changed.
     * @param total_hours the amount of total_hours to set this pay period to
     */
    public void setTotalHours(double total_hours)
    {
        this.total_hours = total_hours;
    }





    /**
     * @return total_hours employee has clocked for this pay period
     */
    public double getTotalHours()
    {
        return total_hours;
    }





    /*
    // Override the equals method
    // We'll need to compare start and end dates to verify if a pay period matches another
    */
    @Override
    public boolean equals(Object compareTo)
    {
        // Verify the object to compare to is not null
        if (compareTo == null)
            return false;


        // Is the object a pay period?
        if (!(compareTo instanceof PayPeriod))
            return false;


        // Cast the object as a pay period
        PayPeriod comparedPayPeriod = (PayPeriod)compareTo;


        // Compare the start dates year
        if (comparedPayPeriod.getStartDate().get(Calendar.YEAR) != this.getStartDate().get(Calendar.YEAR))
            return false;

        // Compare the start dates month
        if (comparedPayPeriod.getStartDate().get(Calendar.MONTH) != this.getStartDate().get(Calendar.MONTH))
            return false;

        // Compare the start dates day
        if (comparedPayPeriod.getStartDate().get(Calendar.DAY_OF_MONTH) != this.getStartDate().get(Calendar.DAY_OF_MONTH))
            return false;

        // Compare the end dates year
        if (comparedPayPeriod.getEndDate().get(Calendar.YEAR) != this.getEndDate().get(Calendar.YEAR))
            return false;

        // Compare the end dates month
        if (comparedPayPeriod.getEndDate().get(Calendar.MONTH) != this.getEndDate().get(Calendar.MONTH))
            return false;

        // Compare the end dates day
        if (comparedPayPeriod.getEndDate().get(Calendar.DAY_OF_MONTH) != this.getEndDate().get(Calendar.DAY_OF_MONTH))
            return false;


        // Nothing left to compare, the pay period is a match
        return true;
    }

}
