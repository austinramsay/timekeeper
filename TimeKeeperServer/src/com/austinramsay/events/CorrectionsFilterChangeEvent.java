package com.austinramsay.events;

import com.austinramsay.types.Filter;

import java.util.EventObject;

public class CorrectionsFilterChangeEvent extends EventObject {

    public CorrectionsFilterChangeEvent(Object source, Filter filterType) {
        super(source);
    }

}
