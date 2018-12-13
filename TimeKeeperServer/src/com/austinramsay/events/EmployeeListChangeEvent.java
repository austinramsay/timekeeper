package com.austinramsay.events;

import java.util.EventObject;

public class EmployeeListChangeEvent extends EventObject {

    public EmployeeListChangeEvent(Object source) {
        super(source);
    }

}
