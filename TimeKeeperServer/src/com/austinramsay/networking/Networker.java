
package com.austinramsay.networking;

import com.austinramsay.controller.TimeKeeperServer;

import javax.swing.*;
import java.io.IOException;
import java.net.*;

/**
 * Network Thread
 * Awaits client connections and assigns a RequestWorker upon connection
 * Passes socket connection to the RequestWorker for further communication with client
 * @author austinramsay
 */
public class Networker implements Runnable {



    private final int PORT = 6263;
    private final String IPADDR;



    public Networker()
    {
        /*
        // Define IPADDR - use getIpAddress() to resolve the host IP
        */
        IPADDR = getIpAddress();
    }
    



    
    /**
     * Resolve the host IP address
     * @return Host IP Address
     */
    private String getIpAddress()
    {
        try {
            String ipaddr = InetAddress.getLocalHost().getHostAddress();
            return ipaddr;
        } catch (UnknownHostException e) { 
            TimeKeeperServer.broadcast("Failed to return host IP address.");
            return null;
        }
    }



    
    
    @Override
    public void run() 
    {
        /*
        // Begin continuous listening loop
        */
        openNetwork();
    }
    



    
    /**
     * The sole purpose of this method is to provide an endless loop to await connections <br>
     * Re-executes upon an exception being thrown if for some reason an issue occurred dealing with a client connection
     */
    private void openNetwork()
    {
        ServerSocket server = getServerSocket();

        TimeKeeperServer.broadcast("Awaiting client connections...");

        while (true)
        {
            try {
                
                /*
                // Pass server socket to the method to accept a client
                // If the server socket is for some reason null, throw ServerLostException
                // We will attempt to reinitialize, and try again
                // If we fail to retrieve a new server socket, the application will not be able to run and will exit
                */
                if (server != null)
                    awaitConnections(server);
                else 
                    throw new ServerLostException("The server socket is null. Attempting to reinitialize.");
                
            } catch (IOException e) {
                // TODO: Log the exception thrown
                // Failed client connection
                TimeKeeperServer.broadcast("Failed client connection. Resuming...");
            }
            catch (ServerLostException e) {
                // TODO: Log the exception thrown
                TimeKeeperServer.broadcast(e.getMessage());
                server = getServerSocket();
                // We attempted to pull a new server socket, if it was successfully the application is still running
                // The loop will reattempt
            }
        }
    }
    
    



    /**
     * Attempts to open server socket on defined final port <br>
     * If an IOException occurs, the exception is logged, and the application is closed <br>
     * @return Server socket on defined final port
     */
    private ServerSocket getServerSocket()
    {
        ServerSocket server;
        
        /*
        // Establish new server socket with previously defined port
        */
        try {

            server = new ServerSocket(PORT);
            return server;

        } catch (BindException e) {
            JOptionPane.showMessageDialog(null, "Port not available. Exiting!");
            System.exit(0);
        }catch (IOException e) {
            // TODO: Log the exception thrown
            JOptionPane.showMessageDialog(null, "Server Socket IO Exception. Exiting.");
            e.printStackTrace();
            System.exit(1);
        }
       
        return null;
    }
    



    
    /**
     * Accepts a client connection given a prepared ServerSocket<br><br>
     * Upon successful connection, a RequestWorker thread is assigned to the client to process what is needed
     * @param server A prepared server socket ready to accept clients
     */
    private void awaitConnections(ServerSocket server) throws IOException
    {
        /*
        // Accept the client
         */
        Socket client = server.accept();


        /*
        // Client connection was successful
        // Create a request worker for the client and begin the thread
        */
        RequestWorker clientWorker = new RequestWorker(client);
        Thread workerThread = new Thread(clientWorker);
        workerThread.start();
    }


}


class ServerLostException extends Exception {
    
    public ServerLostException(String message)
    {
        super(message);
    }
    
}