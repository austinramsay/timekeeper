package com.austinramsay.events;

import com.austinramsay.timekeeper.EmployeeAction;

import java.util.Calendar;

public interface NewActionListener {
    public void fireNewActionSubmission(Calendar date, EmployeeAction action);
}