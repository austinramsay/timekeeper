package com.austinramsay.events;

import com.austinramsay.timekeeper.Recurrence;

import java.util.EventObject;

public class NewOrganizationEvent extends EventObject {

    private final String orgName;
    private final Recurrence payPeriodRecurrence;
    private final int startYear;
    private final int startMonth;
    private final int startDay;

    /**
     * For use with bi-monthly and monthly pay period recurrence, where only the starting pay period year is a dependent variable.
     * For bi-weekly pay periods, use full parameter constructor.
     * @param source event call source
     * @param orgName name of new organization
     * @param payPeriodRecurrence type of pay period recurrence
     * @param startYear the start year to build pay periods for
     */
    public NewOrganizationEvent(Object source, String orgName, Recurrence payPeriodRecurrence, int startYear) {
        super(source);
        this.orgName = orgName;
        this.payPeriodRecurrence = payPeriodRecurrence;
        this.startYear = startYear;
        this.startMonth = 0;
        this.startDay = 0;
    }


    /**
     * For use with bi-weekly pay period recurrence, where start month, day, and year are all necessary variables.
     * For bi-monthly or monthly pay periods, use constructor that only needs the start year.
     * @param source event call source
     * @param orgName name of new organization
     * @param payPeriodRecurrence type of pay period recurrence
     * @param startYear the start year to build pay periods for
     * @param startMonth the first month of the first pay period
     * @param startDay the first day of the first pay period
     */
    public NewOrganizationEvent(Object source, String orgName, Recurrence payPeriodRecurrence, int startYear, int startMonth, int startDay) {
        super(source);
        this.orgName = orgName;
        this.payPeriodRecurrence = payPeriodRecurrence;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }


    public String getOrgName() {
        return orgName;
    }

    public Recurrence getPayPeriodRecurrence() {
        return payPeriodRecurrence;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartDay() {
        return startDay;
    }
}
