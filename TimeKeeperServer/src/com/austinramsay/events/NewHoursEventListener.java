package com.austinramsay.events;

import com.austinramsay.timekeeper.EmployeeAction;

import java.util.Calendar;
import java.util.LinkedHashMap;

public interface NewHoursEventListener {
    public void fireNewHoursEventSubmission(Double hoursClocked, Calendar clockOutTime);
    public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions();
}