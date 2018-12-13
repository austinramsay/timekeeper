package com.austinramsay.networking;

import com.austinramsay.gui.Alert;
import com.austinramsay.timekeeper.*;
import javafx.application.Platform;

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


    /**
     * Submit an employee time correction.
     * @param correction
     */
    public void sendCorrection(CorrectionRequest correction) {

        // Send through networker
        Object response = network.request(correction);

        if (response == null) {
            return;
        }

        if (response instanceof ResponseCode) {
            if (response == ResponseCode.RECEIVED_FAIL) {
                Platform.runLater(() -> {
                    Alert.display("The server received the request, but failed to process it.");
                });
            } else if (response == ResponseCode.RECEIVED_OK) {
                // RECEIVED SUCCESSFULLY
                Platform.runLater(() -> {
                    Alert.display("Correction request successfully sent.");
                });
                return;
            }
        } else {
            Platform.runLater(() -> {
                Alert.display("Unexpected response received from server.");
                System.out.println("Expected response code; Received " + response.getClass().getName() + ".");
            });
            return;
        }
    }
}
