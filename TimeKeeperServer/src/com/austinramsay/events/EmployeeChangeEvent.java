package com.austinramsay.events;

import com.austinramsay.timekeeper.Employee;

import java.util.EventObject;

public class EmployeeChangeEvent extends EventObject {

    private Employee selected;

    public EmployeeChangeEvent(Object source, Employee selected) {
        super(source);
        this.selected = selected;
    }

    public Employee getSelected() {
        return selected;
    }

}
