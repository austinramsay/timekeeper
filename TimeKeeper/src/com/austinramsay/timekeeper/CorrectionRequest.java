package com.austinramsay.timekeeper;

import java.io.Serializable;
import java.util.Calendar;

public class CorrectionRequest implements Serializable {

    private final int employeeId;
    private final String description;
    private final Calendar submitDate;
    private boolean isActive;

    public CorrectionRequest(int employeeId, String description) {
        this.employeeId = employeeId;
        this.description = description;
        this.submitDate = Calendar.getInstance();
        this.isActive = true;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return String.format("%d/%d/%d %d:%s",
                submitDate.get(Calendar.MONTH),
                submitDate.get(Calendar.DAY_OF_MONTH),
                submitDate.get(Calendar.YEAR),
                (submitDate.get(Calendar.HOUR) == 0 ? 12 : submitDate.get(Calendar.HOUR)),
                TimeRenderer.renderMinutes(submitDate.get(Calendar.MINUTE)));
    }

    private Calendar getCalendar() {
        return submitDate;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object compare) {
        if (compare == null) {
            return false;
        }

        // Verify object is indeed a correction request
        if (!(compare instanceof CorrectionRequest)) {
            return false;
        }

        // Downcast object
        CorrectionRequest correction = (CorrectionRequest)compare;

        // Compare fields to check for equality
        // Check employee ID and submission date
        if (correction.getEmployeeId() != this.getEmployeeId()) {
            return false;
        } else if (!correction.getCalendar().equals(this.getCalendar())) {
            return false;
        }

        // Fields match
        return true;
    }
}
