package com.austinramsay.controller;

import com.austinramsay.events.CorrectionsActionListener;
import com.austinramsay.events.CorrectionsFilterListener;
import com.austinramsay.events.CorrectionsListSelectionListener;
import com.austinramsay.events.ModViewerLink;
import com.austinramsay.gui.CorrectionViewerDisplay;
import com.austinramsay.gui.CorrectionsModButtonBox;
import com.austinramsay.gui.CorrectionsModFilterPane;
import com.austinramsay.gui.CorrectionsModListPane;
import com.austinramsay.timekeeper.CorrectionRequest;
import com.austinramsay.timekeeper.Employee;
import com.austinramsay.types.Filter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CorrectionsMod extends JFrame {

    private CorrectionsModFilterPane filterPane;
    private CorrectionsModListPane listPane;
    private CorrectionsModButtonBox buttonPane;

    public CorrectionsMod() {

        super("Corrections Moderator");

        filterPane = new CorrectionsModFilterPane(new CorrectionsFilterListener() {
            @Override
            public void applyFilter() {
                refreshListPaneModel();
            }
        });

        listPane = new CorrectionsModListPane(new CorrectionsListSelectionListener() {
            @Override
            public void enableButtons() {
                buttonPane.enableButtons();
            }

            @Override
            public void disableButtons() {
                buttonPane.disableButtons();
            }
        }, getCorrections(filterPane.getSelectedFilter()));

        buttonPane = new CorrectionsModButtonBox(new CorrectionsActionListener() {
            // Note: This method is called when a user requests to delete the selected correction in the MODERATOR
            @Override
            public void fireRemoveCorrection() {
                // Retrieve selected correction from list pane
                CorrectionRequest correction = listPane.getSelected();

                // Verify a correction is selected - if not, return.
                if (correction == null) {
                    return;
                }

                // Confirm user action before deleting
                int confirm = JOptionPane.showConfirmDialog(null, "Warning: Correction requests cannot be restored once deleted. Continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.NO_OPTION) {
                    return;
                }

                // Attempt to remove correction and get status
                boolean removed = searchAndRemove(correction);

                // Display user message if failed to remove
                // Update model if success
                if (!removed) {
                    JOptionPane.showMessageDialog(null, "Failed to remove selected correction.");
                } else {
                    // Correction removed, update list model to be current again
                    refreshListPaneModel();
                }
            }

            // Note: This method is called when a user requests to open the selected correction in the MODERATOR
            @Override
            public void fireOpenCorrection() {
                CorrectionRequestViewer viewer = new CorrectionRequestViewer(new ModViewerLink() {
                    // Note: This method is called when a user requests to REMOVE THE ALREADY OPENED correction in the VIEWER
                    // We specifically want to delete the opened correction, thus the selected correction in the list pane is not relevant (it could have changed from the time the viewer opened)
                    @Override
                    public void fireRemoveCorrection(CorrectionRequest correction) {
                        boolean removed = searchAndRemove(correction);

                        // Display user message if failed to remove
                        if (!removed) {
                            JOptionPane.showMessageDialog(null, "Failed to remove selected correction.");
                        } else {
                            // Correction removed, update list model to be current again
                            refreshListPaneModel();
                        }
                    }

                    //Note: This method is called upon changing a correction to active/nonactive and we need to refresh the model to keep it current.
                    @Override
                    public void refreshModel() {
                        refreshListPaneModel();
                    }

                }, listPane.getSelected());
            }

            @Override
            public void closeWindow() {
                setVisible(false);
            }
        });

        add(filterPane, BorderLayout.NORTH);
        add(listPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.SOUTH);

        pack();
        setSize(new Dimension(535, 350));
        setLocationRelativeTo(null);
        setVisible(true);
    }


    /**
     * Using the currently selected filter, pulls a current list of matching corrections available and updates the correction moderator's list model.
     */
    private void refreshListPaneModel() {
        if (listPane == null || filterPane == null) {
            return;
        }

        listPane.refreshModel(getCorrections(filterPane.getSelectedFilter()));
    }


    /**
     * Iterate employee list and return matching list of corrections dependant upon selected filter.
     * @return list of corrections to display to user
     */
    private ArrayList<CorrectionRequest> getCorrections(Filter filter) {
        ArrayList<CorrectionRequest> matched = new ArrayList<>();

        // Iterate through all available employees to check if each employee has outstanding corrections
        for (Employee employee : TimeKeeperServer.current_org.getEmployees()) {

            // If employee does have corrections, add it to the list to return
            if (!employee.getCorrections().isEmpty()) {

                // The employee does have corrections. Iterate all entries and add (IF MATCHING THE FILTER APPLIED)
                for (CorrectionRequest correction : employee.getCorrections()) {

                    // Active filter - add all entries that are active only
                    // Nonactive filter - add all entries that are nonactive only
                    if (filter == Filter.ACTIVE) {
                        if (correction.isActive()) {
                            matched.add(correction);
                        }
                    } else if (filter == Filter.NONACTIVE) {
                        if (!correction.isActive()) {
                            matched.add(correction);
                        }
                    } else if (filter == Filter.ALL) {
                        // Add all entries
                        matched.add(correction);
                    } // End filter check

                } // End search loop
            } // End if statement (for employee corrections list size check)
        } // End employee iteration loop

        return matched;
    }


    /**
     * Matches the correction to the corresponding employee it was submitted by.
     * Upon being matched, the correction is removed from the employee's corrections list.
     * If a match is not found, the method will return false.
     * If removal succeeded, the method will return true.
     * @return true if deleted, false if failed
     */
    private boolean searchAndRemove(CorrectionRequest correction) {
        if (correction == null) {
            return false;
        }

        // Iterate through available employees and attempt to match the correction to their ID number
        for (Employee employee : TimeKeeperServer.current_org.getEmployees()) {

            // If the employee has no corrections, skip this search iteration
            if (employee.getCorrections().isEmpty()) {
                continue;
            }

            // Employee does have corrections, we will continue to search for a match
            if (employee.getEmployeeID() == correction.getEmployeeId()) {
                // The employee is a match. Remove this correction from their requests.
                return employee.getCorrections().remove(correction);
            }
        }

        return false;
    }
}
