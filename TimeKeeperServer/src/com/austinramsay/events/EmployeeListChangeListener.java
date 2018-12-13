package com.austinramsay.events;

public interface EmployeeListChangeListener {
    public void fireNewEmployeeRequest(String name);
    public void fireEmployeeEditRequest();
    public void fireRemoveEmployeeRequest();
}
