package com.austinramsay.timekeeper;

import java.io.Serializable;

public class EmployeeUpdate implements Serializable {

    private final int employee_id;
    private final EmployeeAction action;
    private boolean updated;
    public EmployeeUpdate(int employee_id, EmployeeAction action) {
        this.employee_id = employee_id;
        this.action = action;
        this.updated = false;
    }





    /**
     * @return the action requested by client to act on specified user
     */
    public EmployeeAction getAction() {
        return action;
    }





    /**
     * @return the corresponding employee to modify ID number
     */
    public int getEmployeeID() {
        return employee_id;
    }





    /**
     * The request has been acted upon.
     * Set 'updated' variable to respective value depending on if the request was successful/failed
     */
    public void setUpdated(boolean updated) {
        this.updated = updated;
    }





    /**
     * @return updated whether or not the action was successful after being returned from the server
     */
    public boolean getUpdated() {
        return updated;
    }


}
