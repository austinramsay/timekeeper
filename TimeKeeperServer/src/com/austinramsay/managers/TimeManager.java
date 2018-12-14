package com.austinramsay.managers;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.timekeeper.PayPeriod;
import com.austinramsay.timekeeper.Recurrence;
import com.austinramsay.timekeeper.Tracker;

import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author austinramsay
 */
public class TimeManager {

    /**
     * Builds a list of pay periods dependent on recurrence type.
     * Note: Start date is really only specific with bi-weekly payment methods, as other methods have set beginning and end dates.
     * Note: It's still necessary to include for bi-weekly pay periods to evaluate the year, which is really the only important part for dealing with bi-monthly/monthly payment methods.
     * This will only return ONE YEARs worth of pay periods. This is all that is necessary.
     * @param startDate the first start date of the organization's pay period 
     * @param recurrence Bi-weekly, twice a month, monthly (ex. use Recurrence.BIWEEKLY)
     * @return list of all pay periods matching requested start date to end date
     */
    public static ArrayList<PayPeriod> buildPayPeriods(Calendar startDate, Recurrence recurrence) {
        /*
        // Begin building pay periods for the YEAR with respect to recurrence
        */

        if (recurrence == Recurrence.BIWEEKLY) {

            // Bi-weekly pay periods are dependent on a specific start and end date

            // Create a start & end calendar to use as a placeholder for now until we actually set these values.
            Calendar initialStartDate = Calendar.getInstance();
            Calendar initialEndDate = Calendar.getInstance();

            // Create an initial start date based off of our argument start date
            initialStartDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));

            // We'll set the initial end date to this default values of the argumented start date just for now, then we will offset the days to reflect the correct end of a bi-weekly pay period.
            initialEndDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));

            // Add 13 days to the start date for the end date -- this reflects a correct bi-weekly end date.
            initialEndDate.add(Calendar.DAY_OF_MONTH, 13);

            // The correct initial start & end dates are correct. We can use them to create pay periods for the rest of the year.
            // Build bi-weekly pay periods. Return the result.
            return TimeManager.getBiWeeklyPayPeriods(initialStartDate, initialEndDate);

        } else if (recurrence == Recurrence.BIMONTHLY) {

            // Start date is irrelevant, we have a set 1st and 15th start/end date for EVERY pay period
            return TimeManager.getBiMonthlyPayPeriods(startDate.get(Calendar.YEAR));

        } else if (recurrence == Recurrence.MONTHLY) {

            // Start date is irrelevant, we have a set date of pay periods from the 1st to the end of the month
            return TimeManager.getMonthlyPayPeriods(startDate.get(Calendar.YEAR));
        }

        // If failed, we'll return null.
        return null;
    }


    /**
     * Creates a full year set of pay periods consisting of two weeks per pay period throughout the year.
     * The start date month and day will affect the entire rest of the year's pay period calculations.
     * For example, starting the first pay period on a January 2nd will build different pay periods than starting on a January 1st.
     * Usually a bi-weekly pay period begins on a Monday, and ends the next weekend on Sunday.
     * @param buildStart the first start date for the first pay period
     * @param buildEnd the first ending date for the first pay period
     * @return a list of bi-weekly pay periods throughout the year
     */
    private static ArrayList<PayPeriod> getBiWeeklyPayPeriods(Calendar buildStart, Calendar buildEnd) {
        // Create pay period holder
        ArrayList<PayPeriod> payPeriods = new ArrayList<>();

        // Define a variable to determine end of build
        boolean building = true;

        // Begin building loop
        while (building) {
            // Create a pay period using newly built calendars
            PayPeriod built = new PayPeriod(buildStart, buildEnd);

            // Add the new pay period to our ArrayList
            payPeriods.add(new PayPeriod(built));

            // ** New pay period complete ** \\


            // ** Begin next pay period based off previous date values ** \\

            // For bi-weekly, add 14 days to our start date and end dates for the next pay period

            // Our next start date should not be in the next year, so we'll save this for reference.
            int yearCheckOld = buildStart.get(Calendar.YEAR);

            // Use build start + 14 days for our next start
            buildStart.add(Calendar.DAY_OF_MONTH, 14);
            buildEnd.add(Calendar.DAY_OF_MONTH, 14);

            int yearCheckNew = buildStart.get(Calendar.YEAR);
            if (yearCheckOld != yearCheckNew) {
                // Stop building, the next pay period to be built will be for next year
                building = false;
            }
        }

        return payPeriods;
    }


    /**
     * Creates a full year set of pay periods consisting of two per month.
     * The first begins on the 1st of the month thru the 14th.
     * The second begins on the 15th and continues to the end of that month.
     * @param startYear the year the pay periods should be calculated for
     * @return a list of bi-monthly pay periods throughout the year
     */
    private static ArrayList<PayPeriod> getBiMonthlyPayPeriods(int startYear) {
        // Create pay period holder
        ArrayList<PayPeriod> payPeriods = new ArrayList<>();

        // Define variable to determine end of build
        boolean building = true;

        // Define a starting point - January 1, StartYear
        Calendar buildStart = Calendar.getInstance();
        buildStart.set(startYear, Calendar.JANUARY, 1);

        Calendar buildEnd = Calendar.getInstance();
        buildEnd.set(startYear, Calendar.JANUARY, 14);

        // Begin building loop
        while (building) {
            // Create a pay period using newly built calendars
            PayPeriod built = new PayPeriod(buildStart, buildEnd);

            // Add the new pay period to our ArrayList
            payPeriods.add(new PayPeriod(built));

            // ** New pay period complete ** \\


            // ** Begin next pay period based off previous date values ** \\

            // Offset the start date to reflect new start date
            buildStart.set(buildEnd.get(Calendar.YEAR), buildEnd.get(Calendar.MONTH), buildEnd.get(Calendar.DAY_OF_MONTH));
            buildStart.add(Calendar.DAY_OF_MONTH, 1);

            // Verify the start year hasn't run over to the next year. If so, we are done.
            if (buildStart.get(Calendar.YEAR) != startYear) {
                break;
            }

            // Get our current build start values to compare when creating next pay period so we know when we run past the month
            // Note: This is only applicable for pay periods from the 15th to the 30th/31s, because pay periods from the 1st to 14th don't have a change of month.
            int currentMonth = buildStart.get(Calendar.MONTH);

            // There are two events that mark the start/end of a bi-monthly pay period:
            // All bi-monthly pay periods start on the 1st of the month, end on the 14th. Then begin again on the 15th, end on the last day of THAT month (Note: NOT the next upcoming month!)
            // 1. If we're building the first pay period, the 1st-14th, we can determine the end of this pay period by checking if the current build end equals 14.
            // 2. If we're building the second pay period, the 15th to the end of the month (15th-30th, 15th-31st, 15-28th, 15-29th), we can determine the end of the pay period by checking if we have
            //    overrun into the next month. If we have done so, we need to backtrack one day so we end back up in the same month as the rest of the pay period.
            // Using this method will allow us to just ADD one day to our previous build END date for EITHER type of pay period, to determine our next build START date.
            // Example: Adding 1 day to the 31st of January would place us into February 1st. Both are valid pay period end dates and start dates respectively.

            // It will never take more than 17 additions to the previous pay period end day to determine a month change or to find the 14th of the month.
            for (int i = 0; i < 17; i++) {

                // Add a day to the build end date
                buildEnd.add(Calendar.DAY_OF_MONTH, 1);

                if (buildEnd.get(Calendar.MONTH) != currentMonth) {
                    // The month has changed which mean that we are at the next month. Stop here
                    buildEnd.add(Calendar.DAY_OF_MONTH, -1);
                    break;

                } else if (buildEnd.get(Calendar.DAY_OF_MONTH) == 14) {
                    // The 14th marks the end of the first pay period
                    break;
                }
            }
        } // End while 'building' loop

        return payPeriods;
    }


    /**
     * Creates a full year set of pay periods consisting of one pay period per month.
     * All pay periods begin on the 1st of the month, and end the last day of the month.
     * @param startYear the year the pay periods should be calculated for
     * @return a list of monthly pay periods throughout the year
     */
    private static ArrayList<PayPeriod> getMonthlyPayPeriods(int startYear) {
        // Create pay period holder
        ArrayList<PayPeriod> payPeriods = new ArrayList<>();

        // Define variable to determine end of build
        boolean building = true;

        // Define first pay period starting point - January 1, StartYear
        Calendar buildStart = Calendar.getInstance();
        buildStart.set(startYear, Calendar.JANUARY, 1);

        // Define first pay period end point - January 31, StartYear
        // Note: We'll use getActualMaximum to determine the last integer day in the month
        Calendar buildEnd = Calendar.getInstance();
        int endOfMonthDay = buildStart.getActualMaximum(Calendar.DAY_OF_MONTH);
        buildEnd.set(buildStart.get(Calendar.YEAR), buildStart.get(Calendar.MONTH), endOfMonthDay);

        // Begin building loop
        while (building) {
            // Create a pay period using newly built calendars
            PayPeriod built = new PayPeriod(buildStart, buildEnd);

            // Add the new pay period to our ArrayList
            payPeriods.add(new PayPeriod(built));

            // ** New pay period complete ** \\


            // ** Begin next pay period based off previous date values ** \\

            // Offset the start date to reflect new start date
            buildStart.set(buildEnd.get(Calendar.YEAR), buildEnd.get(Calendar.MONTH), buildEnd.get(Calendar.DAY_OF_MONTH));
            buildStart.add(Calendar.DAY_OF_MONTH, 1);

            // Verify the start year hasn't run over to the next year. If so, we are done.
            if (buildStart.get(Calendar.YEAR) != startYear) {
                break;
            }

            // At this point, our build start is now set for the 1st of the next month

            endOfMonthDay = buildStart.getActualMaximum(Calendar.DAY_OF_MONTH);
            buildEnd.set(startYear, buildStart.get(Calendar.MONTH), endOfMonthDay);

        } // End while 'building' loop

        return payPeriods;
    }


    /**
     * Get the tracker of an employee.
     * @param employee_id the unique ID number of the employee
     */
    public static Tracker getTracker(int employee_id) {

        // Attempt to find an existing tracker for this employee
        for (Tracker tracker : TimeKeeperServer.trackers) {
            if (tracker.getEmployeeID() == employee_id)
                return tracker;
        }

        // Tracker not available for this employee, let's create one, add it to the list, and return it to be used
        Tracker new_tracker = new Tracker(employee_id);
        TimeKeeperServer.trackers.add(new_tracker);

        return new_tracker;
    }
}
