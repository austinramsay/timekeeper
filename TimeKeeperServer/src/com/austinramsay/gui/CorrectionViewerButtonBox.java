package com.austinramsay.gui;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.events.CorrectionViewerButtonListener;
import com.austinramsay.types.Marked;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Provides 'Remove' button inside the correction request viewer to delete the currently opened correction
 */
public class CorrectionViewerButtonBox extends JPanel {

    /**
     * @param listener ActionListener that provides implementation to relay a remove request to the ModViewerLink
     */
    public CorrectionViewerButtonBox(CorrectionViewerButtonListener listener) {
        if (listener == null) {
            JOptionPane.showMessageDialog(null, "Correction viewer missing button handler component.");
        }

        JButton markComplete = new JButton("Mark Completed");
        markComplete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.fireMarkup(Marked.NONACTIVE);
            }
        });

        JButton remove = new JButton("Delete");
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.fireRemoveCorrection();
            }
        });

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.closeWindow();
            }
        });

        setLayout(new FlowLayout());
        add(markComplete);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(remove);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(close);
    }
}
