package com.austinramsay.controller;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {

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
        moderatorMI.addActionListener(e -> {
            Moderator mod = new Moderator();
        });
        correctionsMI.addActionListener(e -> {
            CorrectionsMod correctionsMod = new CorrectionsMod();
        });
        contactListMI.addActionListener(e -> {
            ContactsMod contactsMod = new ContactsMod();
        });


        /*
        // Add to admin menu
        */
        adminMenu.add(moderatorMI);
        adminMenu.add(correctionsMI);
        adminMenu.add(contactListMI);


        /*
        // Add to server menu
        */
        serverMenu.add(closeServerMenuItem);


        /*
        // Add both menus to the menu bar
        */
        menubar.add(serverMenu);
        menubar.add(adminMenu);
        // End menus


        /*
        // Activity log text area
        */
        TimeKeeperServer.trafficLog.setEditable(false);
        TimeKeeperServer.trafficLog.setLineWrap(true);
        JScrollPane logPane = new JScrollPane(TimeKeeperServer.trafficLog);
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


        add(logPanel, BorderLayout.CENTER);
        setJMenuBar(menubar);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
