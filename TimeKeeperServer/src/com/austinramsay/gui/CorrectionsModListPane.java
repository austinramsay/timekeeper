package com.austinramsay.gui;

import com.austinramsay.events.CorrectionsListSelectionListener;
import com.austinramsay.model.CorrectionRequestRenderer;
import com.austinramsay.timekeeper.CorrectionRequest;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * Creates and maintains a JList for employee corrections.
 */
public class CorrectionsModListPane extends JPanel {

    private JList<CorrectionRequest> list = new JList<>();
    private DefaultListModel<CorrectionRequest> model = new DefaultListModel<>();

    /**
     * List pane requires a list of items to populate the JList
     * @param correctionsList full list of correction requests resulted from a full employee search
     */
    public CorrectionsModListPane(CorrectionsListSelectionListener selectionListener, ArrayList<CorrectionRequest> correctionsList) {

        // Define list model and cell renderer
        // Define new selection listener logic to fire events
        list.setModel(model);
        list.setCellRenderer(new CorrectionRequestRenderer());

        // When a correction is selected/deselected, enable and disable buttons respectively
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (list.getSelectedValue() == null) {
                        selectionListener.disableButtons();
                    } else {
                        selectionListener.enableButtons();
                    }
                }
            }
        });

        // Populate list
        refreshModel(correctionsList);

        // Create scroll pane for JList
        JScrollPane listPane = new JScrollPane(list);

        // Define preferred width of scroll pane, the goal is to widen it out for readability of names
        int prefHeight = listPane.getPreferredSize().height;
        int prefWidth = listPane.getPreferredSize().width + 10;

        // Cap width at 150
        if (prefWidth > 150) {
            prefWidth = 150;
        }
        listPane.setPreferredSize(new Dimension(prefWidth, prefHeight));  // BorderLayout doesn't respect preferred height but that's fine, just want to widen it out

        // Add to ModeratorList pane
        setLayout(new BorderLayout());
        add(listPane, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(5,5,0,0));
    }


    /**
     * Updates the corrections list model with elements in argumented array.
     * Note that the array passed in should be matching elements dependent upon the current filter selected in the corrections moderator.
     * Using methods from the filter pane, the filter can be determined. The controller should sort this list before passing it in.
     * @param correctionRequests the list of corrections to be displayed
     */
    public void refreshModel(ArrayList<CorrectionRequest> correctionRequests) {
        // Verify model is initialized
        if (model == null) {
            return;
        }

        // Prepare model for updating
        model.clear();

        // Iterate list of correction requests and add each to the model
        for (CorrectionRequest correction : correctionRequests) {
            // Add each element from the argument list
            model.addElement(correction);
        }
    }


    /**
     * @return the selected correction request from the list
     */
    public CorrectionRequest getSelected() {
        return list.getSelectedValue();
    }
}
