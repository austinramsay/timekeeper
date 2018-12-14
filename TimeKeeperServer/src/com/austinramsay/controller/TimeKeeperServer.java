package com.austinramsay.controller;

import com.austinramsay.events.MainLink;
import com.austinramsay.managers.FileManager;
import com.austinramsay.managers.OrganizationManager;
import com.austinramsay.model.Organization;
import com.austinramsay.networking.Networker;
import com.austinramsay.timekeeper.Tracker;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

/**
 *
 * @author austinramsay
 */
public class TimeKeeperServer {

    // Declare static world variables
    public static OrganizationManager org_manager;
    public static Organization current_org;
    public static ArrayList<Tracker> trackers = new ArrayList<>();

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        TimeKeeperServer server = new TimeKeeperServer();

        // Load server files: FileManager
        boolean ready = FileManager.loadOrganizationManager();
        if (!ready)
        {
            // The organization manager failed to load
            JOptionPane.showMessageDialog(null, "Failed to load organization manager!");
            System.exit(1);
        }

        /*
        // Create shutdown hook to save organization manager on close
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                // Call File Manager to perform our save function
                FileManager.updateOrganizationManager();
                System.out.println("Organization manager was updated on exit.");

            }
        });

        /*
        // The Organization Manager is ready
        // Display the start frame
         */
        server.getStartWindow();
    }


    private void getStartWindow() {
        StartWindow startWindow = new StartWindow(new MainLink() {
            @Override
            public void displayMainWindow() {
                // TODO: Implement
                MainFrame mainFrame = new MainFrame();
            }

            @Override
            public void startNetworker() {
                startGlobalNetworker();
            }
        });
    }


    /**
    * Begins networking functionality to begin listening for clients.
    */
    public void startGlobalNetworker() {
        Networker networker = new Networker();
        Thread networkThread = new Thread(networker);
        networkThread.start();
    }


    /**
     * Display message on the activity log
    * @param logMessage the message to be appended
    */
    public static final JTextArea trafficLog = new JTextArea();
    public static void broadcast(String logMessage)
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        if (TimeKeeperServer.trafficLog.getText().isEmpty())
            TimeKeeperServer.trafficLog.append(dateFormat.format(new Date()) + ": " + logMessage);
        else
            TimeKeeperServer.trafficLog.append("\n" + dateFormat.format(new Date()) + ": " + logMessage);
    }    
}

