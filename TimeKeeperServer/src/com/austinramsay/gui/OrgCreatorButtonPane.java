package com.austinramsay.gui;

import com.austinramsay.events.OrgCreatorButtonListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrgCreatorButtonPane extends JPanel {

    private JButton submit = new JButton("Create");

    public OrgCreatorButtonPane(OrgCreatorButtonListener listener) {

        // Define 'Create' button logic
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.submit();
            }
        });

        // Disable 'Create' by default - let verification enable/disable as needed
        submit.setEnabled(false);


        // Define 'Cancel' button logic
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.close();
            }
        });


        // Set flow layout
        setLayout(new FlowLayout());

        // Add components
        add(submit);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(cancel);

        // Set border padding
        setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
    }


    /**
     * Set's the submit button to enabled or disabled.
     * To be used after a submission form fields are verified and a submission is possible.
     * @param enabled
     */
    public void setSubmitButton(boolean enabled) {
        submit.setEnabled(enabled);
    }

}
