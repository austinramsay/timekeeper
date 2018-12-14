
package com.austinramsay.managers;

import com.austinramsay.events.NewOrganizationEvent;
import com.austinramsay.timekeeper.PayPeriod;
import com.austinramsay.timekeeper.Recurrence;
import com.austinramsay.model.Organization;
import com.sun.org.apache.xpath.internal.operations.Or;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author austinramsay
 */
public class OrganizationManager implements Serializable {

    private final ArrayList<Organization> organizations = new ArrayList<>();
    

    public ArrayList<Organization> getOrganizations() {
        return organizations;
    }


    /**
     * Creates a new organization derived from information contained in the parameter event.
     * Adds to the organization manager once complete.
     * Forces update to server file.
     * @param noe a NewOrganizationEvent containing all necessary fields to create an organization
     * @return true if organization is added to the manager, false if failed (pay period build fail)
     */
    public boolean createOrganization(NewOrganizationEvent noe)
    {
        int year = noe.getStartYear();
        int month = noe.getStartMonth();
        int day = noe.getStartDay();

        // Because the user input a value from 1 to 12 (January thru December),
        // We need to subtract one from the user input to correctly set the calendar
        // Calendar uses 0-11 instead of 1-12
        month -= 1;

        // Now we can create the calendar
        Calendar startDate = Calendar.getInstance();
        startDate.set(year, month, day);

        /*
        // Create the pay period list according to the recurrence requested
        // If the returned build list is null, build failed.
         */
        ArrayList<PayPeriod> payPeriods = TimeManager.buildPayPeriods(startDate, noe.getPayPeriodRecurrence());
        if (payPeriods == null) {
            JOptionPane.showMessageDialog(null, "Failed to build pay periods.");
            return false;
        }

        /*
        // Create the new Organization object
         */
        Organization new_organization = new Organization(noe.getOrgName(), noe.getPayPeriodRecurrence(), payPeriods);


        /*
        // Add to our list of organizations and force update to the file
         */
        return(organizations.add(new_organization));
    }
}
