package com.austinramsay.controller;

import com.austinramsay.events.CorrectionViewerButtonListener;
import com.austinramsay.events.ModViewerLink;
import com.austinramsay.gui.CorrectionViewerButtonBox;
import com.austinramsay.gui.CorrectionViewerDisplay;
import com.austinramsay.timekeeper.CorrectionRequest;
import com.austinramsay.types.Marked;
import javax.swing.*;
import java.awt.*;

/**
 * Opens a frame to display a correction request with employee details
 */
public class CorrectionRequestViewer extends JFrame {

    public CorrectionRequestViewer(ModViewerLink modLink, CorrectionRequest correction) {

        CorrectionViewerDisplay displayPane = new CorrectionViewerDisplay(correction);

        CorrectionViewerButtonBox buttonPane = new CorrectionViewerButtonBox(new CorrectionViewerButtonListener() {
            // User clicked 'Remove' in the correction viewer window
            @Override
            public void fireRemoveCorrection() {
                // Confirm user action before deleting
                int confirm = JOptionPane.showConfirmDialog(null, "Warning: Correction requests cannot be restored once deleted. Continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.NO_OPTION) {
                    return;
                }

                // Action confirmed, continue...
                modLink.fireRemoveCorrection(correction);
                setVisible(false);
            }

            // User clicked 'Mark Complete' in the correction viewer window
            @Override
            public void fireMarkup(Marked mark) {
                // Set correction to active/nonactive depending on mark selected
                if (mark == Marked.ACTIVE) {
                    correction.setActive(true);
                } else if (mark == Marked.NONACTIVE) {
                    correction.setActive(false);
                }

                // Refresh the model to keep filters current
                modLink.refreshModel();

                // Close the viewer window
                setVisible(false);
            }

            // User clicked 'Close' in the correction viewer window
            @Override
            public void closeWindow() {
                setVisible(false);
            }
        });

        JPanel root = new JPanel();
        root.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
        root.add(displayPane, BorderLayout.CENTER);
        root.add(buttonPane, BorderLayout.SOUTH);
        setContentPane(root);

        setSize(new Dimension(360, 315));
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
