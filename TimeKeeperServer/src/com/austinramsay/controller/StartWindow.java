package com.austinramsay.controller;

import com.austinramsay.events.MainLink;
import com.austinramsay.events.StartWindowListener;
import com.austinramsay.gui.StartPane;

import javax.swing.*;
import java.awt.*;

public class StartWindow extends JFrame {

    /**
     * Display the welcome screen
     * Gives option to create a new organization, load preferences from an existing organization, or to close the program
     */
    public StartWindow(MainLink link) {

        super("Server Start");

        StartPane startPane = new StartPane(new StartWindowListener() {
            @Override
            public void displayAvailableOrganizations() {
                OrganizationListViewer listViewer = new OrganizationListViewer(new MainLink() {
                    @Override
                    public void displayMainWindow() {
                        // Relay back to main controller
                        link.displayMainWindow();
                        setVisible(false);
                    }

                    @Override
                    public void startNetworker() {
                        // Relay back to main controller
                        link.startNetworker();
                    }
                });
            }

            @Override
            public void displayOrganizationCreator() {
                OrganizationCreator orgCreator = new OrganizationCreator();
            }
        });

        setContentPane(startPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
