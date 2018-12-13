package com.austinramsay.controller;

import com.austinramsay.events.NewOrganizationEvent;
import com.austinramsay.events.OrgCreatorButtonListener;
import com.austinramsay.events.OrgCreatorListener;
import com.austinramsay.gui.OrgCreatorButtonPane;
import com.austinramsay.gui.OrgCreatorGridPane;
import com.austinramsay.managers.FileManager;

import javax.swing.*;
import java.awt.*;

public class OrganizationCreator extends JFrame {

    private OrgCreatorGridPane gridPane;
    private OrgCreatorButtonPane buttonPane;

    public OrganizationCreator() {

        super("Create Organization");

        gridPane = new OrgCreatorGridPane(new OrgCreatorListener() {
            @Override
            public void submitNewOrganization(NewOrganizationEvent noe) {

                // Submit the event to the Organization Manager to process
                boolean created = TimeKeeperServer.org_manager.createOrganization(noe);

                // Verify organization was built correctly and added to the manager.
                // If done, update the manager file.
                if (created) {

                    // The organization was created, update the local server file
                    FileManager.updateOrganizationManager();
                    JOptionPane.showMessageDialog(null, "Success.");

                } else {

                    // The creation failed
                    JOptionPane.showMessageDialog(null, "Failed to create organization.");

                }
            }

            @Override
            public void fireVerifiedFields(boolean verified) {
                if (buttonPane != null) {
                    buttonPane.setSubmitButton(verified);
                }
            }
        });

        buttonPane = new OrgCreatorButtonPane(new OrgCreatorButtonListener() {
            @Override
            public void submit() {
                gridPane.fireSubmissionEvent();
            }

            @Override
            public void close() {
                setVisible(false);
            }
        });

        // Build root container with grid pane and button pane
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.PAGE_AXIS));
        root.add(gridPane);
        root.add(buttonPane);

        // Set content pane and display frame
        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
