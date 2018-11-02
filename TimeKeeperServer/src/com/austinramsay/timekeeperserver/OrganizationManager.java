
package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.PayPeriod;
import com.austinramsay.timekeeperobjects.Recurrence;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author austinramsay
 */
public class OrganizationManager implements Serializable {

    private final ArrayList<Organization> organizations;
    public OrganizationManager()
    {
        this.organizations = new ArrayList<>();
    }
    
    



    public Organization getOrganization(int index)
    {
        return organizations.get(index);
    }





    /**
     * @return The amount of organizations stored
     */
    public int getListSize()
    {
        return organizations.size();
    }





    /**
     *
     * @param name organization name
     * @param initial_year begninning year of pay period (for the year)
     * @param initial_month beginning month of pay period (for the year)
     * @param initial_day beginning day of pay period (for the year)
     * @param pay_period_recurrence pay period recurrence (bi-weekly, bi-monthly, monthly)
     * @return true if organization is added to the manager, false if failed
     */
    public boolean createOrganization(String name, String initial_year, String initial_month, String initial_day, Recurrence pay_period_recurrence)
    {
        /*
        // Get a Time Manager to create a list of pay periods
         */
        TimeManager timemanager = new TimeManager();


        /*
        // Create a Calendar for the initial start date of the pay periods
         */
        int year, month, day;
        try {
            year = Integer.parseInt(initial_year);
            month = Integer.parseInt(initial_month);
            day = Integer.parseInt(initial_day);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Failed to parse number values for start date.");
            return false;
        }

        // Because the user input a value from 1 to 12 (January thru December),
        // We need to subtract one from the user input to correctly set the calendar
        // Calendar uses 0-11 instead of 1-12
        month -= 1;


        // Now we can create the calendar
        Calendar start_day = Calendar.getInstance();
        start_day.set(year, month, day);


        /*
        // Create the pay period list according to the recurrence requested
         */
        ArrayList<PayPeriod> payperiods = timemanager.buildPayPeriods(start_day, pay_period_recurrence);
        if (payperiods == null)
        {
            JOptionPane.showMessageDialog(null, "Failed to build pay periods.");
            return false;
        }

        /*
        // Create the new Organization object
         */
        Organization new_organization = new Organization(name, payperiods);


        /*
        // Add to our list of organizations and force update to the file
         */
        return(organizations.add(new_organization));
    }





}
