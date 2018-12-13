
package com.austinramsay.timekeeper;

/**
 *
 * @author austinramsay
 */
public enum Recurrence {

    BIWEEKLY("Bi-Weekly"),
    BIMONTHLY("Bi-Monthly"),
    MONTHLY("Monthly");

    private String type;

    Recurrence(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
