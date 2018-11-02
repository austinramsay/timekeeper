
package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.Employee;
import com.austinramsay.timekeeperobjects.PayPeriod;
import com.austinramsay.timekeeperobjects.Recurrence;
import com.austinramsay.timekeeperobjects.Tracker;

import java.awt.*;

import static java.awt.Component.LEFT_ALIGNMENT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.Border;

/**
 *
 * @author austinramsay
 */
public class TimeKeeperServer {


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


        /*
        // Load the server files
         */
        boolean ready = FileManager.loadOrganizationManager();
        if (!ready)
        {
            // The organization manager failed to load
            JOptionPane.showMessageDialog(null, "Failed to load organization manager.");
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
    JFrame startFrame;
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
        newOrgButton.addActionListener(e -> createOrganization());
        
        
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
     Display a 'new organization' window.
     Get the user's organization name, the initial start date for pay periods, and pay period recurrence type. (Possible values: Bi-Weekly, Bi-Monthly, Monthly)
     Using the organization manager, create the new organization, and save the new manager once complete.
     */
    private boolean createOrganization() {

        // Create new JDialog and JPanel
        JDialog org_creator = new JDialog();
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        JPanel creator_panel = new JPanel();
        JPanel button_box = new JPanel();


        /*
        // Create labels
         */
        JLabel org_name_label = new JLabel("Organization name:");
        JLabel pay_period_label = new JLabel("Pay Period Type:");
        JLabel start_year_label = new JLabel("Initial Year (xxxx):");
        JLabel start_month_label = new JLabel("Initial Month (1-12):");
        JLabel start_day_label = new JLabel("Initial Day (1-31):");


        // We will use this as our list of options for the pay period selector drop down list
        String[] pay_period_list = { "Bi-Weekly", "Bi-Monthly", "Monthly" };


        /*
        // Create input fields for organization name and pay period selector
         */
        JComboBox pay_period_selector = new JComboBox(pay_period_list);
        JTextField name_input = new JTextField();
        JTextField year_input = new JTextField();
        JTextField month_input = new JTextField();
        JTextField day_input = new JTextField();


        /*
        // Create buttons
        // Align to center
        // Hide the window on 'cancel'
         */
        JButton submit = new JButton("Create"); // Logic coded after this small block
        JButton cancel = new JButton("Cancel");



        /*
        // Button logic
         */
        submit.addActionListener(e -> {

            // Create a boolean to ensure all values were met before submitting
            boolean requirements_met = true;


            // Set the requested pay period occurrence to use when creating the new organization
            Recurrence payperiod_recurrence = null;
            switch (pay_period_selector.getSelectedIndex()) {

                // Index 0 corresponds to Bi-Weekly
                case 0:
                    payperiod_recurrence = Recurrence.BIWEEKLY;
                    break;

                // Index 1 corresponds to Bi-Monthly
                case 1:
                    payperiod_recurrence = Recurrence.BIMONTHLY;
                    break;

                case 2:
                    payperiod_recurrence = Recurrence.MONTHLY;
                    break;

            }


            boolean created = TimeKeeperServer.org_manager.createOrganization(
                    name_input.getText(),
                    year_input.getText(),
                    month_input.getText(),
                    day_input.getText(),
                    payperiod_recurrence
                    );


            if (created) {

                // The organization was created, update the local server file
                FileManager.updateOrganizationManager();
                JOptionPane.showMessageDialog(null, "Success.");
                org_creator.setVisible(false);

            } else {

                // The creation failed
                JOptionPane.showMessageDialog(null, "Failed to create organization.");

            }


            // End submit button logic
        });

        cancel.addActionListener(e -> org_creator.setVisible(false));


        /*
        // Set layout managers for the creator panel and the button box
         */
        creator_panel.setLayout(new GridLayout(5, 2, 10, 10));
        button_box.setLayout(new FlowLayout());


        /*
        // Begin adding nodes to panels
         */
        creator_panel.add(org_name_label);
        creator_panel.add(name_input);
        creator_panel.add(pay_period_label);
        creator_panel.add(pay_period_selector);
        creator_panel.add(start_year_label);
        creator_panel.add(year_input);
        creator_panel.add(start_month_label);
        creator_panel.add(month_input);
        creator_panel.add(start_day_label);
        creator_panel.add(day_input);

        button_box.add(submit);
        button_box.add(cancel);


        /*
        // Create a 10 pixel gap between the two panels
        // We'll add a 10 pixel top border to the button box
         */
        Border button_box_border = BorderFactory.createEmptyBorder(10,0,0,0);
        button_box.setBorder(button_box_border);


        /*
        // Center the button box and the two buttons
         */
        button_box.setAlignmentX(SwingConstants.CENTER);
        submit.setAlignmentX(SwingConstants.CENTER);
        cancel.setAlignmentX(SwingConstants.CENTER);


        /*
        // Build the main JDialog 'root'
         */
        root.add(creator_panel, BorderLayout.CENTER);
        root.add(button_box, BorderLayout.PAGE_END);


        /*
        // Create a 10 pixel border around root panel
         */
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        root.setBorder(padding);


        /*
        // Pack the frame, and display
         */
        org_creator.add(root);
        org_creator.pack();
        org_creator.setLocationRelativeTo(null);
        org_creator.setVisible(true);

        return true;
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
        JMenu adminMenu = new JMenu("Admin Actions");
        
        
        /*
        // Server Menu Items
        */
        JMenuItem closeServerMenuItem = new JMenuItem("Close Server");
        closeServerMenuItem.addActionListener(e -> System.exit(0));
        
        
        /*
        // Admin Menu Items
        */
        JMenuItem editEmployeesMI = new JMenuItem("Edit Employees");
        editEmployeesMI.addActionListener(e -> displayEmployeeModerator());
        
        
        /*
        // Add to server menu
        */
        serverMenu.add(closeServerMenuItem);
        
        
        /*
        // Add to admin menu
        */
        adminMenu.add(editEmployeesMI);
        
        
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

        /*
        // Create a window for the employee moderator
         */
        JDialog employee_mod = new JDialog();


        /*
        // Create moderator buttons (Create, Modify, Remove)
        // Center all buttons
        // Add button logic to end of method
         */
        JButton create = new JButton("Add New Employee");
        JButton modify = new JButton("Existing Employees");
        JButton close = new JButton("Close");
        create.setAlignmentX(Component.CENTER_ALIGNMENT);
        modify.setAlignmentX(Component.CENTER_ALIGNMENT);
        close.setAlignmentX(Component.CENTER_ALIGNMENT);


        /*
        // Create a panel to vertically stack buttons
        // Add a 10 pixel border for padding
         */
        JPanel button_box = new JPanel();
        button_box.setLayout(new BoxLayout(button_box, BoxLayout.PAGE_AXIS));
        button_box.setAlignmentX(Component.CENTER_ALIGNMENT);
        button_box.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


        /*
        // Add buttons to new panel
         */
        button_box.add(create);
        button_box.add(Box.createRigidArea(new Dimension(0,10)));
        button_box.add(modify);
        button_box.add(Box.createRigidArea(new Dimension(0,10)));
        button_box.add(close);


        /*
        // Add the button box to the main dialog content pane
         */
        employee_mod.getContentPane().add(button_box, BorderLayout.CENTER);


        /*
        // Pack the frame and display
         */
        employee_mod.pack();
        employee_mod.setLocationRelativeTo(null);
        employee_mod.setVisible(true);



        /*
        // Set button logic
         */


        // Close button logic
        close.addActionListener(e -> employee_mod.setVisible(false));


        // Create button logic
        create.addActionListener(e -> {

            /*
            // Create a window with an input form for employee details
             */
            JDialog new_employee_window = new JDialog();


            /*
            // We really only need the employee's name
            // Create a label and text field
             */
            JTextField name_input = new JTextField();


            /*
            // Create a horizontal box layout for the name input and label
             */
            JPanel input_panel = new JPanel();
            input_panel.setLayout(new BoxLayout(input_panel, BoxLayout.LINE_AXIS));
            input_panel.setAlignmentX(Component.CENTER_ALIGNMENT);


            /*
            // Add the text field and label to the new input panel
             */
            input_panel.add(new JLabel("Employee Name:"));
            input_panel.add(Box.createRigidArea(new Dimension(8,0)));
            input_panel.add(name_input);


            /*
            // Create two buttons for submitting or closing
             */
            JButton submit = new JButton("Submit");
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(e2 -> new_employee_window.setVisible(false));


            /*
            // Create a horizontal box for our buttons
             */
            JPanel new_employee_button_box = new JPanel();
            new_employee_button_box.setLayout(new BoxLayout(new_employee_button_box, BoxLayout.LINE_AXIS));
            new_employee_button_box.setAlignmentX(Component.CENTER_ALIGNMENT);


            /*
            // Add the buttons to the button box
             */
            new_employee_button_box.add(submit);
            new_employee_button_box.add(Box.createRigidArea(new Dimension(8,0)));
            new_employee_button_box.add(cancel);


            /*
            // Create a 'root' pane to add border padding around our two panels
             */
            JPanel new_employee_root = new JPanel();
            new_employee_root.setLayout(new BoxLayout(new_employee_root, BoxLayout.PAGE_AXIS));
            new_employee_root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


            /*
            // Add the two panels to root, and create a 10 pixel space between them
             */
            new_employee_root.add(input_panel);
            new_employee_root.add(Box.createRigidArea(new Dimension(0,10)));
            new_employee_root.add(new_employee_button_box);


            /*
            // Add our panels to the main content pane
             */
            new_employee_window.getContentPane().add(new_employee_root, BorderLayout.CENTER);


            /*
            // Display the new employee window
            // Resize to 370 width to ensure the input text field is wide enough
             */
            new_employee_window.setPreferredSize(new Dimension(370,110));
            new_employee_window.pack();
            new_employee_window.setLocationRelativeTo(employee_mod);
            new_employee_window.setVisible(true);


            /*
            // Submit (New Employee Submission) logic
             */
            submit.addActionListener(e2 -> {

                /*
                // Before we can create an employee, we need to get the next avaialble ID number for this organization
                 */
                int employee_id = current_org.getNextEmployeeID();
                if (employee_id == -1)  // The getNextEmployeeID() method returns -1 if it fails to find a next avaialable ID number.
                {
                    JOptionPane.showMessageDialog(new_employee_window, "Failed to retrieve next available employee ID.");
                    return;
                }


                /*
                // Now that we have an ID number to use, create the employee's tracker and then the employee
                // Use the current organizations pay periods (the organizations getPayPeriods() method returns a copied array list!
                // Use the name_input text as the employee name
                 */
                Tracker new_tracker = new Tracker(employee_id);
                Employee new_employee = new Employee(employee_id, name_input.getText(), current_org.getPayPeriods(), new_tracker);


                /*
                // Add to the organization using organization manager
                 */
                if (current_org.addEmployee(new_employee)) {

                    FileManager.updateOrganizationManager();
                    JOptionPane.showMessageDialog(new_employee_window, "New employee added. Assigned ID number: " + employee_id + ".");
                    new_employee_window.setVisible(false);

                }
                else
                    JOptionPane.showMessageDialog(new_employee_window, "Failed to add employee.");

            });

        });


        /*
        // Modify (Existing Employee) logic
         */
        modify.addActionListener(e -> {


            /*
            // Create new window to display a list of available employees
             */
            JDialog employee_editor_window = new JDialog();


            /*
            // Create a string array from all the employee names in the organization
             */
            String[] names = new String[current_org.getEmployees().size()];
            for (int i = 0; i < current_org.getEmployees().size(); i++)
            {
                names[i] = current_org.getEmployeeByIndex(i).getName();
            }


            /*
            // Create a JList to display all employees
             */
            JList<String> existing_employee_list = new JList(names);


            /*
            // Create a JScrollPane for the existing employee list
             */
            JScrollPane existing_list_scrollpane = new JScrollPane(existing_employee_list);
            existing_employee_list.setPreferredSize(new Dimension(210,300));


            /*
            // Create a panel to hold the existing employee list
             */
            JPanel existing_list_panel = new JPanel();
            existing_list_panel.setLayout(new FlowLayout());
            existing_list_panel.add(existing_list_scrollpane);


            /*
            // Create a remove and modify button
            // Assign cancel button to close window upon clicking
             */
            JButton edit = new JButton("Edit");
            JButton remove = new JButton("Remove");
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(e2 -> employee_editor_window.setVisible(false));


            /*
            // Create a panel for the buttons
            // Assign center alignment
             */
            JPanel employee_mod_button_box = new JPanel();
            employee_mod_button_box.setLayout(new BoxLayout(employee_mod_button_box, BoxLayout.LINE_AXIS));
            employee_mod_button_box.setAlignmentX(Component.CENTER_ALIGNMENT);


            /*
            // Add the buttons to the button box
            // Put an 8 pixel space between each button
             */
            employee_mod_button_box.add(edit);
            employee_mod_button_box.add(Box.createRigidArea(new Dimension(8,0)));
            employee_mod_button_box.add(remove);
            employee_mod_button_box.add(Box.createRigidArea(new Dimension(8,0)));
            employee_mod_button_box.add(cancel);


            /*
            // Create a panel to add border padding to before adding to main content pane
            // Add a 10 pixel padding
             */
            JPanel employee_mod_root = new JPanel();
            employee_mod_root.setLayout(new BoxLayout(employee_mod_root, BoxLayout.PAGE_AXIS));
            employee_mod_root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


            /*
            // Add our built nodes to the root panel
             */
            employee_mod_root.add(existing_list_panel);
            employee_mod_root.add(Box.createRigidArea(new Dimension(0,10)));
            employee_mod_root.add(employee_mod_button_box);


            /*
            // Root panel is built, add to the main dialog content pane
             */
            employee_editor_window.getContentPane().add(employee_mod_root, BorderLayout.CENTER);
            employee_editor_window.pack();
            employee_editor_window.setLocationRelativeTo(employee_mod);
            employee_editor_window.setVisible(true);



            /*
            // Set button logic for buttons inside the employee editor window
             */

            // Edit button logic
            edit.addActionListener(e2 -> {

                // Using the JList selected index, select our employee from the current organizations employee list
                // This index will match up with the current organization's employees array list index
                int selected_index = existing_employee_list.getSelectedIndex();
                Employee selected = current_org.getEmployees().get(selected_index);

                // Create new popup window to edit employee name
                JDialog employee_edit_popup = new JDialog();

                // Create a text field to allow editing of the employee name
                JTextField name_input = new JTextField();
                name_input.setText(selected.getName());

                // Create two buttons to submit changes or cancel
                JButton submit_edit = new JButton("Submit");
                submit_edit.addActionListener(e4 -> {

                    // Submit edit button logic
                    selected.setName(name_input.getText());
                    if (FileManager.updateOrganizationManager())
                        JOptionPane.showMessageDialog(employee_edit_popup, "Employee name updated.");
                    else
                        JOptionPane.showMessageDialog(null, "Employee name updated, but the organization failed to update.");

                    employee_edit_popup.setVisible(false);
                    employee_editor_window.setVisible(false);

                });
                JButton cancel_edit = new JButton("Cancel");
                cancel_edit.addActionListener(e3 -> employee_edit_popup.setVisible(false));

                // Create a horizontal box layout for the label and name text field
                // Add a 10 pixel buffer to the panel that contains all nodes (edit_root)
                JPanel edit_button_box = new JPanel();
                edit_button_box.setLayout(new BoxLayout(edit_button_box, BoxLayout.LINE_AXIS));

                JPanel edit_fields = new JPanel();
                edit_fields.setLayout(new BoxLayout(edit_fields, BoxLayout.LINE_AXIS));

                JPanel edit_root = new JPanel();
                edit_root.setLayout(new BoxLayout(edit_root, BoxLayout.PAGE_AXIS));
                edit_root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

                // Add nodes to corresponding panels
                edit_button_box.add(submit_edit);
                edit_button_box.add(Box.createRigidArea(new Dimension(10,0)));
                edit_button_box.add(cancel_edit);

                edit_fields.add(new JLabel("Employee Name:"));
                edit_fields.add(Box.createRigidArea(new Dimension(10,0)));
                edit_fields.add(name_input);

                edit_root.add(edit_fields);
                edit_root.add(Box.createRigidArea(new Dimension(0,10)));
                edit_root.add(edit_button_box);

                employee_edit_popup.getContentPane().add(edit_root);
                employee_edit_popup.setPreferredSize(new Dimension(370,110));
                employee_edit_popup.pack();
                employee_edit_popup.setLocationRelativeTo(employee_editor_window);
                employee_edit_popup.setVisible(true);

            }); // End Edit button logic


            // Remove button logic
            remove.addActionListener(e2 -> {

                // Remove button logic
                // Get the employee's index
                int selected_index = existing_employee_list.getSelectedIndex();
                if (current_org.removeEmployee(selected_index))
                {
                    if (FileManager.updateOrganizationManager())
                        JOptionPane.showMessageDialog(employee_editor_window, "Remove Succeeded.");
                    else
                        JOptionPane.showMessageDialog(employee_editor_window, "Remove succeeded, but the organization failed to update.");

                    employee_editor_window.setVisible(false);
                }
                else
                    JOptionPane.showMessageDialog(employee_editor_window, "Remove failed.");


            });

        }); // End modify button logic

    } // End displayEmployeeModerator() method





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
