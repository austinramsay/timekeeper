
package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.PayPeriod;
import com.austinramsay.timekeeperobjects.Recurrence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author austinramsay
 */
public class TimeManager {
    
    /**
     * Run once upon beginning hour tracking for an organization.
     * Possibly useful for repairing pay periods that were previously built.
     * Pay periods should be set in the Time Keeper server config file according to the organization name.
     * @param startDate the first start date of the organization's pay period 
     * @param recurrence Bi-weekly, twice a month, monthly (ex. use Recurrence.BIWEEKLY)
     * @return ArrayList of all pay periods matching requested start date to end date
     */
    public ArrayList<PayPeriod> buildPayPeriods(Calendar startDate, Recurrence recurrence)
    {
        /*
        // Begin building pay periods for the year with respect to recurrence
        // To build a PayPeriod list, we need a start date and end date of the initial pay period
        // We'll begin by building the initial pay period using the start date in our arguments
        */
        ArrayList<PayPeriod> payperiods = new ArrayList<>();
        
        
        /*
        // Create an initial start date so we can edit the original argument 'startDate'
        */
        Calendar initialStartDate = Calendar.getInstance();
        initialStartDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        
        
        /*
        // We'll use the initial start date to modify and get the initial end date
        */
        Calendar initialEndDate = startDate;
       
        
        /*
        // Check if recurrence should be bi-weekly
        */
        if (recurrence == Recurrence.BIWEEKLY)
        {
            // Add 13 days to the start date for the end date
             initialEndDate.add(Calendar.DAY_OF_MONTH, 13);
        }
        
        
        /*
        // We now have an initial start and end date
        // Use our known values to produce a new pay period for each recurrence
        */
        Calendar buildStart = initialStartDate;
        Calendar buildEnd = initialEndDate;
        boolean building = true;
        
        
        /*
        // Begin building loop
        */
        while (building)
        {
            // Create a pay period using newly built calendars
            PayPeriod built = new PayPeriod(buildStart, buildEnd);


            // Add the new pay period to our ArrayList
            payperiods.add(new PayPeriod(built));
            // New pay period complete
            
            
            // For bi-weekly, add 14 days to our start date and end dates for the next pay period
            if (recurrence == Recurrence.BIWEEKLY)
            {   
                int yearCheckOld = buildStart.get(Calendar.YEAR);
                
                // Use build start + 14 days for our next start
                buildStart.add(Calendar.DAY_OF_MONTH, 14);
                buildEnd.add(Calendar.DAY_OF_MONTH, 14);
                
                int yearCheckNew = buildStart.get(Calendar.YEAR);            
                if (yearCheckOld != yearCheckNew)
                {
                    // Stop building, the next pay period to be built will be for next year
                    building = false;
                }
            }
        }
        
        
       return payperiods;
    }
    
    
}
