package com.austinramsay.networking;

import com.austinramsay.gui.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Networker {


    /**
     * Using the IP address defined in this method, attempts to get a socket connection to the server.
     * @return a socket connection to the Time Keeper server
     */
    private Socket getConnection() {

        /*
        // Create a new socket to connect to the server
         */
        try {

            Socket connection = new Socket("127.0.0.1", 6263);
            //Socket connection = new Socket("70.93.96.32", 6263);
            return connection;

        } catch (UnknownHostException e) {

            Alert.display("Failed to locate the server.");
            return null;

        } catch (IOException e) {

            Alert.display("Failed to contact server.");
            return null;

        }

    }


    /**
     * Sends a request object to the server, and returns the reply received.
     * @param clientRequest the client request object
     * @return the response received back from the server
     */
    public Object request(Object clientRequest) {

        // Get a server connection
        Socket connection = getConnection();


        // Verify we have a server connection
        if (connection == null)
            return null;


        // Get an output stream to send our request
        ObjectOutputStream oos = getOutputStream(connection);


        // Send request
        try {

            /*
            // Attempt to send the client request through our output stream
             */
            oos.writeObject(clientRequest);

        } catch (IOException e) {

            Alert.display("Connection reset. Failed to send request.");

            // Close the connection
            if (connection != null)
            {
                try {
                    connection.close();
                } catch (IOException e2) { Alert.display("Failed to close resources."); }
            }

            return null;

        }



        /*
        // We've sent our request, receive the server response now
         */

        // Get an input stream to receive server response
        ObjectInputStream ois = getInputStream(connection);

        // Receive server response
        try {

            Object response = ois.readObject();
            return response;

        } catch (IOException e) {

            Alert.display("Connection reset. Failed to receive server response.");
            return null;

        } catch (ClassNotFoundException e) {

            Alert.display("Response received from server couldn't be generically interpreted.");
            return null;

        } finally {

            // Close all resources at this point
            try {
                if (ois != null)
                    ois.close();
                if (connection != null)
                    connection.close();
            } catch (IOException e) { Alert.display("Failed to close resources."); }

        }
    }


    /**
     * @param connection a socket connection to the server
     * @return an object output stream connected to the server
     */
    private ObjectOutputStream getOutputStream(Socket connection) {
        /*
        // Create an object output stream with our argument socket
         */
        try {

            ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
            return oos;

        } catch (IOException e) {

            Alert.display("Connection reset. Failed to retrieve output stream to server.");
            return null;

        }
    }


    /**
     * @param connection a socket connection to the server
     * @return an object input stream connected to the server
     */
    private ObjectInputStream getInputStream(Socket connection) {
        /*
        // Create an object output stream with our argument socket
         */
        try {

            ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
            return ois;

        } catch (IOException e) {

            Alert.display("Connection reset. Failed to retrieve input stream from server.");
            return null;

        }
    }
}