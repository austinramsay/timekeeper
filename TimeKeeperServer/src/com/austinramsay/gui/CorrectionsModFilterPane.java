package com.austinramsay.gui;

import com.austinramsay.events.CorrectionsFilterListener;
import com.austinramsay.types.Filter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CorrectionsModFilterPane extends JPanel {

    private JComboBox<Filter> filters = new JComboBox<>();

    public CorrectionsModFilterPane(CorrectionsFilterListener filterListener) {

        filters.addItem(Filter.ACTIVE);
        filters.addItem(Filter.NONACTIVE);
        filters.addItem(Filter.ALL);
        filters.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // A new filter was selected
                    filterListener.applyFilter();
                }
            }
        });

        JLabel filterLabel = new JLabel("Filter:");

        BoxLayout layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        setLayout(layout);

        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(filterLabel);
        add(Box.createRigidArea(new Dimension(8,0)));
        add(filters);
    }



    /**
     * Returns the selected filter in the corrections mod filter pane
     * Used for getting the first selected filter when opening the corrections moderator in order to correctly populate the list model (to correspond with the filter)
     * May return null if the object failed to cast as a filter
     * @return the selected corrections mod filter
     */
    public Filter getSelectedFilter() {
        if (filters.getSelectedItem() instanceof Filter) {
            return (Filter)filters.getSelectedItem();
        } else {
            return null;
        }
    }

}
