package com.austinramsay.types;

public enum Filter {

    ACTIVE("Active Only"),
    NONACTIVE("History Only"),
    ALL("All");

    final String type;

    Filter(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
