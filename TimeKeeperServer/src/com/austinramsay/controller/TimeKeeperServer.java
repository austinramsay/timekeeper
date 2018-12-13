package com.austinramsay.controller;

import com.austinramsay.managers.FileManager;
import com.austinramsay.managers.OrganizationManager;
import com.austinramsay.model.Organization;
import com.austinramsay.networking.Networker;
import com.austinramsay.timekeeper.Recurrence;
import com.austinramsay.timekeeper.Tracker;

import java.awt.*;

import static java.awt.Component.LEFT_ALIGNMENT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.Border;

/**
 *
 * @author austinramsay
 */
public class TimeKeeperServer {

    // Declare static world variables
    public static OrganizationManager org_manager;
    public static Organization current_org;
    public static ArrayList<Tracker> trackers;
    public static String dbName, dbIpAddr, dbUsername, dbPassword;

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        TimeKeeperServer server = new TimeKeeperServer();

        // Initialiaze trackers list
        trackers = new ArrayList<>();

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
        server.displayStart();
    }
    

    
    /**
     * Display the welcome screen
     * Gives option to create a new organization, load preferences from an existing organization, or to close the program
     */
    private JFrame startFrame;
    private void displayStart() {

        startFrame = new JFrame("Server Start");
        JPanel mainPanel = new JPanel();
        
        
        JLabel welcomeLabel = new JLabel("Time Keeper Server");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        JButton loadOrgButton = new JButton("Load Existing Organization");
        loadOrgButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadOrgButton.addActionListener(e -> displayOrganizations());
        
        
        JButton newOrgButton = new JButton("Create New Organization");
        newOrgButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newOrgButton.addActionListener(e -> {
            OrganizationCreator orgCreator = new OrganizationCreator();
        });
        
        
        JButton cancelButton = new JButton("Close");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> System.exit(0));
        
        
        /*
        // Using the 'Main Panel', set to use the Y-axis to add components
        // Create a 10 pixel gap between components
        */
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,12)));
        mainPanel.add(loadOrgButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0,12)));
        mainPanel.add(newOrgButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0,12)));
        mainPanel.add(cancelButton);

        
        this.startFrame.setLayout(new FlowLayout());
        this.startFrame.add(mainPanel);
        this.startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.startFrame.pack();
        this.startFrame.setLocationRelativeTo(null);
        this.startFrame.setVisible(true);
    }



    /**
     Displays the list of organizations pulled from the Organization Manager.
     Upon user selection of an organization, begin server according to organization preferences
     */
    private void displayOrganizations() {

        /*
        // Create the main dialog window
        // Create two containers to house nodes
        // Add a border layout manager for the root container
        // Add a flow layout manager for the button box
         */
        JDialog organization_display = new JDialog();
        JPanel root = new JPanel();
        JPanel button_box = new JPanel();
        root.setLayout(new BorderLayout());
        button_box.setLayout(new FlowLayout());


        /*
        // We want to display a list of stored organization names
        // Get the number of organizations that are stored, and iterate the list
        // Add each name to an array of strings to utilize in the JList
         */
        String[] orgs = new String[org_manager.getListSize()];
        for (int i = 0; i < org_manager.getListSize(); i++)
        {
            orgs[i] = org_manager.getOrganization(i).toString();
        }


        /*
        // Create the JList to display organization names
        // Set a preferred size so the window has a constant size
         */
        JList<String> org_list = new JList<>(orgs);
        JScrollPane org_list_scrollpane = new JScrollPane(org_list);
        org_list_scrollpane.setPreferredSize(new Dimension(210, 300));


        /*
        // Create buttons
         */
        JButton start = new JButton("Start");
        JButton cancel = new JButton("Cancel");


        /*
        // Cancel button logic
         */
        cancel.addActionListener(e -> organization_display.setVisible(false));


        /*
        // Start button logic
         */
        start.addActionListener(e -> {

            /*
            // Set the current organization to be used program-wide
             */
            TimeKeeperServer.current_org = org_manager.getOrganization(org_list.getSelectedIndex());


            /*
            // Just to verify before starting full functionality, make sure our organization was really set
             */
            if (TimeKeeperServer.current_org == null)
            {
                JOptionPane.showMessageDialog(null, "The global organization failed to be set.");
                return;
            }


            /*
            // With the current organization now set, open the main moderator window
            // We can begin networking and start accepting client requests
             */
            display();  // Displays moderator windw
            start();    // Begins networking


            /*
            // We're done with this window, close it now
            // Close the start window as well
             */
            organization_display.setVisible(false);
            startFrame.setVisible(false);

        });


        /*
        // Center the button box and the two buttons
         */
        button_box.setAlignmentX(SwingConstants.CENTER);
        start.setAlignmentX(SwingConstants.CENTER);
        cancel.setAlignmentX(SwingConstants.CENTER);


        /*
        // Add buttons to the button box
         */
        button_box.add(start);
        button_box.add(cancel);


        /*
        // Create a 10 pixel border around root container
         */
        Border padding = BorderFactory.createEmptyBorder(10, 10, 5, 10);
        root.setBorder(padding);


        /*
        // Setup and pack the main window
         */
        root.add(org_list_scrollpane, BorderLayout.CENTER);
        root.add(button_box, BorderLayout.PAGE_END);
        organization_display.add(root, BorderLayout.CENTER);
        organization_display.pack();
        organization_display.setLocationRelativeTo(null);
        organization_display.setVisible(true);
    }



    /**
    * Begins networking functionality to begin listening for clients.
    */
    private void start() {

        Networker networker = new Networker();
        Thread networkThread = new Thread(networker);
        networkThread.start();
    }



    /*
    // Build and display the server user interface
    */
    private final JFrame moderator = new JFrame("Time Keeper Server");
    private static final JTextArea trafficLog = new JTextArea();
    private void display() {

        /*
        // Menus
        */
        JMenuBar menubar = new JMenuBar();
        JMenu serverMenu = new JMenu("Server");
        JMenu adminMenu = new JMenu("Administration");
        
        
        /*
        // Server Menu Items
        */
        JMenuItem closeServerMenuItem = new JMenuItem("Close Server");
        closeServerMenuItem.addActionListener(e -> System.exit(0));
        
        
        /*
        // Admin Menu Items
        */
        JMenuItem moderatorMI = new JMenuItem("Moderator");
        JMenuItem correctionsMI = new JMenuItem("Time Corrections");
        JMenuItem contactListMI = new JMenuItem("Contact List");
        moderatorMI.addActionListener(e -> displayEmployeeModerator());
        correctionsMI.addActionListener(e -> displayCorrections());
        contactListMI.addActionListener(e -> displayContactList());


        /*
        // Add to server menu
        */
        serverMenu.add(closeServerMenuItem);
        
        
        /*
        // Add to admin menu
        */
        adminMenu.add(moderatorMI);
        adminMenu.add(correctionsMI);
        adminMenu.add(contactListMI);
        
        
        /*
        // Add both menus to the menu bar
        */
        menubar.add(serverMenu);
        menubar.add(adminMenu);
        // End menus
        
        
        /*
        // Activity log text area
        */
        trafficLog.setEditable(false);
        trafficLog.setLineWrap(true);
        JScrollPane logPane = new JScrollPane(trafficLog);
        logPane.setAlignmentX(LEFT_ALIGNMENT);
        
        
        /*
        // Add the text area to the main log panel
        */
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.PAGE_AXIS));
        logPanel.add(new JLabel("Activity Log:"));
        logPanel.add(Box.createRigidArea(new Dimension(0,5)));
        logPanel.add(logPane);
        logPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        

        /*
        // Build main JFrame
        */
        moderator.add(logPanel, BorderLayout.CENTER);
        moderator.setJMenuBar(menubar);
        moderator.setSize(900, 600);
        moderator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        moderator.setLocationRelativeTo(null);
        moderator.setVisible(true);      
    }



    /**
     * Displays a window containing options to add new employees, and modify existing employees.
     */
    private void displayEmployeeModerator() {

        Moderator mod = new Moderator();

    }



    /**
     * Displays a window containing a filtered list of either all outstanding (active) correction requests or a list of all corrections submitted history
     */
    private void displayCorrections() {

        CorrectionsMod corMod = new CorrectionsMod();

    }



    /**
     *
     */
    private void displayContactList() {

        ContactsMod conMod = new ContactsMod();

    }



    /**
     * Get the tracker of an employee.
     * @param employee_id the unique ID number of the employee
     */
    public static Tracker getTracker(int employee_id) {

        // Attempt to find an existing tracker for this employee
        for (Tracker tracker : trackers) {
            if (tracker.getEmployeeID() == employee_id)
                return tracker;
        }

        // Tracker not available for this employee, let's create one, add it to the list, and return it to be used
        Tracker new_tracker = new Tracker(employee_id);
        trackers.add(new_tracker);

        return new_tracker;
    }



    /**
     * Display message on the activity log
    * @param logMessage the message to be appended
    */
    public static void broadcast(String logMessage)
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        if (TimeKeeperServer.trafficLog.getText().isEmpty())
            TimeKeeperServer.trafficLog.append(dateFormat.format(new Date()) + ": " + logMessage);
        else
            TimeKeeperServer.trafficLog.append("\n" + dateFormat.format(new Date()) + ": " + logMessage);
    }    
}

