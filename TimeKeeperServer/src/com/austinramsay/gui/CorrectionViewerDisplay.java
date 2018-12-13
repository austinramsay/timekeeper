package com.austinramsay.gui;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.timekeeper.CorrectionRequest;
import javax.swing.*;
import java.awt.*;

/**
 * Takes a correction request and creates a panel to display the correction's date, description, and name of employee details
 */
public class CorrectionViewerDisplay extends JPanel {

    /**
     * @param correction the correction to be evaluated and displayed
     */
    public CorrectionViewerDisplay(CorrectionRequest correction) {
        // Create date and name labels with left alignment
        JLabel nameLabel = new JLabel(String.format("Submitted by: %s", TimeKeeperServer.current_org.getEmployee(correction.getEmployeeId()).getName()));
        JLabel dateLabel = new JLabel(String.format("Submitted on: %s", correction.getDate()));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Create text area for correction description. Allow text wrapping and disable editing
        JTextArea descDisplay = new JTextArea(correction.getDescription());
        descDisplay.setColumns(20);
        descDisplay.setRows(8);
        descDisplay.setLineWrap(true);
        descDisplay.setWrapStyleWord(true);
        descDisplay.setEditable(false);

        // Use box layout page axis as layout manager
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(nameLabel);
        add(Box.createRigidArea(new Dimension(0,8)));
        add(dateLabel);
        add(Box.createRigidArea(new Dimension(0,8)));
        add(new JScrollPane(descDisplay));
    }
}
