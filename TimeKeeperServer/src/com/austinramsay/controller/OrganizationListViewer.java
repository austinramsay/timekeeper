package com.austinramsay.controller;

import com.austinramsay.events.MainLink;
import com.austinramsay.events.OrganizationListButtonListener;
import com.austinramsay.events.OrganizationSelectionListener;
import com.austinramsay.gui.OrgsListButtonPane;
import com.austinramsay.gui.OrgsListPane;
import com.austinramsay.model.Organization;

import javax.swing.*;

public class OrganizationListViewer extends JFrame {

    public OrganizationListViewer(MainLink link) {

        OrgsListPane listPane = new OrgsListPane(new OrganizationSelectionListener() {
            @Override
            public void defineCurrentOrg(Organization selectedOrg) {
                TimeKeeperServer.current_org = selectedOrg;
            }
        });

        OrgsListButtonPane buttonPane = new OrgsListButtonPane(new OrganizationListButtonListener() {
            @Override
            public void start() {
                listPane.fireSelectOrganization();
                link.startNetworker();
                link.displayMainWindow();
                setVisible(false);
            }

            @Override
            public void close() {
                setVisible(false);
            }
        });

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.PAGE_AXIS));

        root.add(listPane);
        root.add(buttonPane);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
