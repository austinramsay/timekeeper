package com.austinramsay.gui;

import com.austinramsay.controller.OrganizationCreator;
import com.austinramsay.events.StartWindowListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class StartPane extends JPanel {

    public StartPane(StartWindowListener listener) {

//        JLabel welcomeLabel = new JLabel("Time Keeper Server");
//        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JButton loadOrgButton = new JButton("Load Existing Organization");
        loadOrgButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadOrgButton.addActionListener(e -> {
            listener.displayAvailableOrganizations();
        });


        JButton newOrgButton = new JButton("Create New Organization");
        newOrgButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newOrgButton.addActionListener(e -> {
            listener.displayOrganizationCreator();
        });


        JButton cancelButton = new JButton("Close");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> System.exit(0));

        // Create titled border
        Border padding = BorderFactory.createEmptyBorder(5,5,5,5);
        Border titled = BorderFactory.createTitledBorder("Time Keeper Server");
        Border compounded = BorderFactory.createCompoundBorder(padding, titled);

        // Set the new border
        setBorder(compounded);

        /*
        // Using the 'Main Panel', set to use the Y-axis to add components
        // Create a 10 pixel gap between components
        */
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(Box.createRigidArea(new Dimension(0,12)));
        add(loadOrgButton);
        add(Box.createRigidArea(new Dimension(0,12)));
        add(newOrgButton);
        add(Box.createRigidArea(new Dimension(0,12)));
        add(cancelButton);
    }

}
