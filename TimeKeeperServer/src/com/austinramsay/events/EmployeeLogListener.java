package com.austinramsay.events;

import com.austinramsay.timekeeper.EmployeeAction;

import java.util.Calendar;
import java.util.LinkedHashMap;

public interface EmployeeLogListener {
    public void fireActionEntryRemoval(Calendar date, EmployeeAction action);
    public void fireActionEntryAddition(Calendar date, EmployeeAction action);
    public void fireHourEntryRemoval(Calendar date, Double hours);
    public void fireHourEntryAddition(Calendar date, Double hours);
    public LinkedHashMap<Calendar, EmployeeAction> retrieveEmployeeActions();
}
