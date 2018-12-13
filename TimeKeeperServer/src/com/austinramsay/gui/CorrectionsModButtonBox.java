package com.austinramsay.gui;

import com.austinramsay.events.CorrectionsActionListener;

import javax.swing.*;
import java.awt.*;

/**
 * Provides user with buttons to 'remove' the selected correction request, or 'open' to view the description/details.
 * Fires a CorrectionsActionListener event upon button action.
 * Note that the controller should implement the logic to handle removal/opening of the selected correction.
 */
public class CorrectionsModButtonBox extends JPanel {

    private CorrectionsActionListener requestListener;
    private JButton open;
    private JButton remove;
    private JButton close;

    public CorrectionsModButtonBox(CorrectionsActionListener requestListener) {

        // Verify request handler is available
        if (requestListener == null) {
            JOptionPane.showMessageDialog(null, "Corrections moderator missing button handler component.");
        }

        this.requestListener = requestListener;

        // 'Open' button -> send request to open selected correction
        open = new JButton("Open");
        open.addActionListener(e -> {

            // Fire event to open the selected correction
            // Note: The controller will determine which correction is selected and handle from there.
            requestListener.fireOpenCorrection();

        });

        // 'Remove' button -> send request to delete the selected correction
        remove = new JButton("Delete");
        remove.addActionListener(e -> {

            // Fire event to remove the selected correction
            // Note: The controller will determine which correction is selected and handle from there.
            requestListener.fireRemoveCorrection();

        });

        // 'Close' button -> close the moderator window
        close = new JButton("Close");
        close.addActionListener(e -> {

            // Close the corrections moderator
            requestListener.closeWindow();

        });

        // Use flow layout to add buttons together in a line
        setLayout(new FlowLayout());

        // Add all button elements
        add(open);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(remove);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(close);

        // Disable buttons by default
        disableButtons();
    }


    /**
     * Enable 'Open' and 'Remove' buttons
     */
    public void enableButtons() {
        open.setEnabled(true);
        remove.setEnabled(true);
    }


    /**
     * Disable 'Open' and 'Remove' buttons
     */
    public void disableButtons() {
        open.setEnabled(false);
        remove.setEnabled(false);
    }

}
