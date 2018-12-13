package com.austinramsay.events;

public interface EmployeeChangeListener {
    public void fireEmployeeChangedEvent(EmployeeChangeEvent e);
}