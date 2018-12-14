package com.austinramsay.gui;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.events.OrganizationSelectionListener;
import com.austinramsay.model.Organization;
import com.sun.org.apache.xpath.internal.operations.Or;

import javax.swing.*;
import java.awt.*;

public class OrgsListPane extends JPanel {

    private OrganizationSelectionListener listener;
    private JList<Organization> org_list = new JList<>();
    private DefaultListModel<Organization> orgsModel = new DefaultListModel<>();

    public OrgsListPane(OrganizationSelectionListener listener) {

        this.listener = listener;

        // Add all available organizations stored in the Organization Manager
        for (Organization availableOrg : TimeKeeperServer.org_manager.getOrganizations()) {
            orgsModel.addElement(availableOrg);
        }

        /*
        // Create the JList to display organization names
        // Set a preferred size so the window has a constant size
         */
        org_list.setModel(orgsModel);
        JScrollPane org_list_scrollpane = new JScrollPane(org_list);
        org_list_scrollpane.setPreferredSize(new Dimension(210, 300));

        add(org_list_scrollpane);

        setBorder(BorderFactory.createEmptyBorder(5,5,3,5));
    }

    public void fireSelectOrganization() {
        listener.defineCurrentOrg((Organization)org_list.getSelectedValue());
    }

}
