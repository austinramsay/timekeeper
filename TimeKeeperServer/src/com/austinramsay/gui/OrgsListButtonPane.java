package com.austinramsay.gui;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.events.OrganizationListButtonListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrgsListButtonPane extends JPanel {

    public OrgsListButtonPane(OrganizationListButtonListener listener) {

        JButton start = new JButton("Start");
        JButton cancel = new JButton("Cancel");

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.start();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.close();
            }
        });

        setLayout(new FlowLayout());

        setBorder(BorderFactory.createEmptyBorder(0,5,5,5));

        add(start);
        add(cancel);
    }

}
