package com.austinramsay.timekeeper;

import com.austinramsay.timekeeperobjects.Employee;
import com.austinramsay.timekeeperobjects.EmployeeAction;
import com.austinramsay.timekeeperobjects.EmployeeList;
import com.austinramsay.timekeeperobjects.EmployeeUpdate;

import java.util.ArrayList;

public class RequestWorker {


    private final Networker network;
    public RequestWorker() {

        // Create a networker to use
        this.network = new Networker();

    }





    /**
     * Contacts the server and requests a full list of employees.
     * @return organization's employee list
     */
    public ArrayList<Employee> getEmployees() {

        /*
        // Create an employee list request
         */
        EmployeeList list = new EmployeeList();


        /*
        // Send the list to populated by the server
         */
        Object response = network.request(list);
        if (response == null)
            return null;


        /*
        // We got an object back, let's attempt to recast it back to an EmployeeList
         */
        try {

            list = (EmployeeList)response;

        } catch (ClassCastException e) {

            Alert.display("(Employee List) Response received from server couldn't be returned to its original state.");
            return null;

        }


        /*
        // Extract the array list of employees from the EmployeeList object
         */
        return list.getEmployees();
    }





    /**
     * Creates an employee update and sends to the server
     * @param employee_id employee's unique ID number
     * @param action Clocking in / Clocking out
     */
    public boolean requestAction(int employee_id, EmployeeAction action) {

        /*
        // Create an EmployeeUpdate object with argument values
         */
        EmployeeUpdate update_request = new EmployeeUpdate(employee_id, action);

        // Send through networker
        Object response = network.request(update_request);


        if (response instanceof EmployeeUpdate) {
            update_request = (EmployeeUpdate)response;
            return update_request.getUpdated();
        }
        else
            return false;
    }




    public void syncTimes() {

        /*
        // Create a TimeSync request
         */

    }


}
