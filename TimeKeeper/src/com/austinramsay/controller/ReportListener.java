package com.austinramsay.controller;

import com.austinramsay.timekeeper.Employee;
import com.austinramsay.timekeeper.PayPeriod;

public interface ReportListener {
    Employee getSelectedEmployee();
    void firePayPeriodSelected(PayPeriod payPeriod);
    void fireClearPayPeriodDependents();
    void displayCorrectionBox(int employeeId);
}
