
package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.Employee;
import com.austinramsay.timekeeperobjects.EmployeeAction;
import com.austinramsay.timekeeperobjects.EmployeeList;
import com.austinramsay.timekeeperobjects.EmployeeUpdate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author austinramsay
 */
public class RequestWorker implements Runnable {

    private final Socket client;
    private final String CLIENT_IP;
    private boolean save_flag;
    public RequestWorker(Socket client) {

        this.client = client;
        this.CLIENT_IP = getClientIP();
        save_flag = false;

    }
    



    
    /**
     * @return Client IP Address
     */
    private String getClientIP() {

        /*
        // Verify that the client socket is not null
        */
        if (client == null)
            return null;
        
        return client.getInetAddress().getHostAddress();

    }
    
    



    @Override
    public void run() {

        /*
        // First, let's get the client's request so we can determine what they need
        */
        Object request = getClientRequest();
        
        
        /*
        // Now that we have the client's request
        // Process the request accordingly
        */
        Object response = processRequest(request);


        /*
        // If the save flag has been marked true, something in the organization has been changed
        // We need to update the organization in the manager to keep our changes server side
        // For optimization purposes, it's best to do this now instead of letting our client wait for us to write to disk before getting a response
         */
        if (save_flag) {
            FileManager.updateOrganizationManager();
        }


        /*
        // Return the response to the client
         */
        reply(response);
    }



    
    
    /**
     * Open object input stream between client and server <br>
     * @return ObjectInputStream connected to the client
     */
    private ObjectInputStream getClientInputStream() {

        try {
            
            // Get the client's input stream
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());
            
            return input;
            
        } catch (IOException e) {
            // TODO: Log thrown exception
            TimeKeeperServer.broadcast(String.format("Client %s: Failed to get client input stream.", CLIENT_IP));
            return null;
        }
    }

    



    /**
     * Open object output stream between client and server <br>
     * @return ObjectOutputStream connected to the client
     */
    private ObjectOutputStream getClientOutputStream() {

        try {
            
            // Get the client's output stream
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            return output;
            
        } catch (IOException e) {
            
            // TODO: Log thrown exception
            TimeKeeperServer.broadcast(String.format("Client %s: Failed to get client output stream.", CLIENT_IP));
            return null;
            
        }
    }    




    
    /**
     * For new client connections <br>
     * Receive the client's request object <br>
     * @return Request object sent by client
     */
    private Object getClientRequest() {

        /*
        // At this point a client has already sent the server a request
        // Get the client's input stream and read the request
        */
        ObjectInputStream input = getClientInputStream();


        try {
            
            // Read request from the input stream
            Object request = input.readObject();
            return request;

        } catch (IOException e) {
            
            // TODO: Log the thrown exception
            TimeKeeperServer.broadcast(String.format("Client %s: Failed to retrieve request.", CLIENT_IP));
            e.printStackTrace();
            return null;
            
        } catch (ClassNotFoundException e) {
            
            // TODO: Log the thrown excpetion
            TimeKeeperServer.broadcast(String.format("Client %s: Failed to determine request type.", CLIENT_IP));
            return null;
            
        } finally {

            // There's no reason we need to be taking anymore input from the client
            try {
                client.shutdownInput();
            } catch (IOException e) {
                TimeKeeperServer.broadcast(String.format("Client %s: Failed to shutdown client connection input.", CLIENT_IP));
            }

        }
    }
    
    



    /**
     * Given an a request from the client, determine and take an appropriate action <br>
     * @param request The request received from the client
     */
    private Object processRequest(Object request) {

        /*
        // Compare request object to request types
        // Types:
        //      - EmployeeList: Client needs a list of the organization employees
        //      - EmployeeUpdate: Client is clocking in or out an employee
        */

        Object response = null;


        if (request instanceof EmployeeList)
        {
            // Send a full list of employees back to the client

            EmployeeList employee_list = (EmployeeList)request;
            employee_list.setEmployees(TimeKeeperServer.current_org.getEmployees());
            response = employee_list;

            /*

            THIS WAS FOR DEBUG PURPOSES..NOT NEEDED

            for (Employee sent : employee_list.getEmployees()) {
                System.out.println("Name: " + sent.getName() + "...Clocked: " + sent.isClockedIn() + "...Hours pay period: " + sent.getPayPeriod(Calendar.getInstance()).getTotalHours() + "...Current pay period: " + sent.getPayPeriod(Calendar.getInstance()).hashCode());
            }*/

            TimeKeeperServer.broadcast(String.format("Client %s: Employee list requested.", CLIENT_IP));
        }


        else if (request instanceof EmployeeUpdate)
        {
            // Employee is clocking in or out


            // Get update information
            EmployeeUpdate employee_update = (EmployeeUpdate)request;
            EmployeeAction action = employee_update.getAction();


            // Get all employee information
            int employee_id = employee_update.getEmployeeID();
            Employee employee = TimeKeeperServer.current_org.getEmployee(employee_id);


            // Handle times
            if (action == EmployeeAction.CLOCKIN) {

                // Employee is clocking in

                // Employee is not already clocked in, continue to clock in
                if (!employee.isClockedIn()) {

                    // Set our employee to clocked in
                    employee.setClockedIn(true);

                    employee_update.setUpdated(true);

                    TimeKeeperServer.broadcast(String.format("Client %s: %s clocked in.", CLIENT_IP, employee.getName()));

                } else {

                    // Our employee is already clocked in, client out of sync
                    employee_update.setUpdated(false);

                    TimeKeeperServer.broadcast(String.format("Client %s: Client out of sync. Failed to clock in %s.", CLIENT_IP, employee.getName()));

                }

            } else if (action == EmployeeAction.CLOCKOUT) {

                // Employee is clocking out

                // Employee is clocked in, continue to clock out
                if (employee.isClockedIn()) {

                    // Set our employee to clocked out
                    // setClockedIn(false) will return a number of hours value
                    double hours_clocked;
                    hours_clocked = employee.setClockedIn(false);

                    employee_update.setUpdated(true);

                    TimeKeeperServer.broadcast(String.format("Client %s: %s clocked %.2f hours.", CLIENT_IP, employee.getName(), hours_clocked));

                } else {

                    // Our employee is already clocked out, client out of sync
                    employee_update.setUpdated(false);

                    TimeKeeperServer.broadcast(String.format("Client %s: Client out of sync. Failed to clock out %s.", CLIENT_IP, employee.getName()));

                }

            } // End clock in/clock out if statement


            // Confirm update to client
            response = employee_update;


            // At this point, if something has been updated for an employee...
            // Let's save the organization manager so the results are not somehow lost.
            // Set the save flag to true, so that after we reply with our response, we know to update the organization manager before our worker closes
            if (employee_update.getUpdated())
                save_flag = true;
        }


        return response;
    }





    /**
     * Return a processed response to a client
     * @param response the prepared response to reply to client
     */
    private void reply(Object response) {

        ObjectOutputStream output = getClientOutputStream();
        if (output == null)
            return;


        try {

            // Read request from the input stream
            output.writeObject(response);

        } catch (IOException e) {

            // TODO: Log the thrown exception
            TimeKeeperServer.broadcast(String.format("Client %s: Failed to send response.", CLIENT_IP));

        }
        finally {
            // Complete. Close streams.
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // TODO: Log the thrown exception.
                    TimeKeeperServer.broadcast(String.format("Client %s: Failed to close output stream.", CLIENT_IP));
                }
            }
        }

    }
}
