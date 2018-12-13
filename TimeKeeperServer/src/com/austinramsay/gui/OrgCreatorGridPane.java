package com.austinramsay.gui;

import com.austinramsay.events.NewOrganizationEvent;
import com.austinramsay.events.OrgCreatorListener;
import com.austinramsay.timekeeper.Recurrence;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

public class OrgCreatorGridPane extends JPanel {

    private OrgCreatorListener creatorListener;
    private JComboBox<Recurrence> payPeriodRecurrence = new JComboBox<>();
    private JTextField name_input = new JTextField();
    private JComboBox<Integer> yearInput = new JComboBox<>();
    private JComboBox<Integer> monthInput = new JComboBox<>();
    private JComboBox<Integer> dayInput = new JComboBox<>();
    private DefaultComboBoxModel<Integer> monthModel = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<Integer> daysModel = new DefaultComboBoxModel<>();

    public OrgCreatorGridPane(OrgCreatorListener creatorListener) {

        this.creatorListener = creatorListener;


        // Create labels
        JLabel org_name_label = new JLabel("Organization name:");
        JLabel pay_period_label = new JLabel("Pay Period Type:");
        JLabel start_year_label = new JLabel("Initial Year:");
        JLabel start_month_label = new JLabel("Initial Month:");
        JLabel start_day_label = new JLabel("Initial Day:");

        name_input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                creatorListener.fireVerifiedFields(verifyInputFields());
            }
        });


        // Add our recurrence types into the pay period selector box
        payPeriodRecurrence.addItem(Recurrence.BIWEEKLY);
        payPeriodRecurrence.addItem(Recurrence.BIMONTHLY);
        payPeriodRecurrence.addItem(Recurrence.MONTHLY);

        // Set a listener to wait for selection in the pay period recurrence type
        // Upon choosing bi-weekly, enable the month and day fields.
        // Upon choosing bi-monthly or monthly, disable the month and day fields.
        payPeriodRecurrence.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                creatorListener.fireVerifiedFields(verifyInputFields());

                // Pay period type was deselected - disable all date fields
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    yearInput.setEnabled(false);
                    monthInput.setEnabled(false);

                    // Clear days model and disable
                    populateDaysField(0);

                // Pay period type was selected - enable fields that correspond with type
                } else {
                    if ((Recurrence)payPeriodRecurrence.getSelectedItem() == Recurrence.BIWEEKLY) {
                        // Bi-weekly selected, enable all date fields.
                        yearInput.setEnabled(true);
                        monthInput.setEnabled(true);
                        dayInput.setEnabled(true);

                        // Set month to January as default and populate days model
                        monthModel.setSelectedItem(1);
                        populateDaysField(getCalculatedMaxDays((Integer)monthInput.getSelectedItem()));

                    } else if ((Recurrence)payPeriodRecurrence.getSelectedItem() == Recurrence.BIMONTHLY || (Recurrence)payPeriodRecurrence.getSelectedItem() == Recurrence.MONTHLY) {
                        // Monthly or bi-monthly selected, month and day fields not needed.
                        yearInput.setEnabled(true);
                        monthInput.setEnabled(false);

                        // Clear days model and disable
                        populateDaysField(0);
                    } else {
                        // Disable all fields if uncategorized
                        yearInput.setEnabled(false);
                        monthInput.setEnabled(false);

                        // Clear days model and disable
                        populateDaysField(0);
                    }
                }
            }
        });
        // Deselect any pay period type - user needs to select.
        payPeriodRecurrence.setSelectedItem(null);


        // Populate 'Year' input box \\
        // Uses past 10 years as well as next 10 years to populate model.
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = (thisYear - 10); i < (thisYear + 10); i++) {
            yearInput.addItem(i);
        }

        // Select this year in the input box since it's most likely going to be the user's choice.
        yearInput.setSelectedItem(thisYear);

        // Set a listener to wait for selection in the year - this will only verify input fields to enable/disable submit button
        yearInput.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                creatorListener.fireVerifiedFields(verifyInputFields());
            }
        });


        // Populate the 'Month' input box - Jan thru Dec \\
        monthInput.setModel(monthModel);

        for (int i = 1; i <= 12; i++) {
            monthModel.addElement(i);
        }

        // Set a listener to wait for selection in the month to correctly populate the days combo box
        monthInput.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                creatorListener.fireVerifiedFields(verifyInputFields());

                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    // Unpopulate the day input field, no month selected.
                    populateDaysField(0);

                } else {
                    // Populate the day field with all days in the selected month
                    // Get max days by passing in the selected month to the calculation method
                    populateDaysField(getCalculatedMaxDays((Integer)monthInput.getSelectedItem()));
                }
            }
        });


        // Set the 'Day' input field to use the 'daysModel' \\
        dayInput.setModel(daysModel);

        // Set a listener to wait for selection in the day - this will only verify input fields to enable/disable submit button
        dayInput.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                creatorListener.fireVerifiedFields(verifyInputFields());
            }
        });


        // Set grid layout
        setLayout(new GridLayout(5, 2, 10, 10));

        // Add components
        add(org_name_label);
        add(name_input);
        add(pay_period_label);
        add(payPeriodRecurrence);
        add(start_year_label);
        add(yearInput);
        add(start_month_label);
        add(monthInput);
        add(start_day_label);
        add(dayInput);

        // Set border
        Border padding = BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(padding);
    }


    /**
     * @param 1 to 12 to represent January thru December.
     * @return the maximum amount of days in a specified month (1-12 for Jan-Dec). Zero if param out of bounds.
     */
    private int getCalculatedMaxDays(int month) {
        // Get a calendar for testing and set the month to the selected month
        Calendar testCal = Calendar.getInstance();
        testCal.set(Calendar.MONTH, (month - 1));  // Note: Calendar uses 0-11 for month, which is why we have the minus 1.

        // Get the maximum amount of days that corresponds with the month
        int maxDays = testCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return maxDays;
    }


    /**
     * Populates the 'Day' input field according to specified parameter.
     * If the maxDays parameter equals zero, this will clear the model and disable the input field.
     * For a nonzero positive integer, the model will be populated and the input field will be enabled.
     * @param maxDays populate the days list from 1-maxDays
     */
    private void populateDaysField(int maxDays) {
        if (maxDays < 0) {
            return;
        }

        if (maxDays == 0) {
            daysModel.removeAllElements();
            dayInput.setEnabled(false);
        } else {
            for (int i = 1; i <= maxDays; i++) {
                daysModel.addElement(i);
            }
            dayInput.setEnabled(true);
        }
    }


    /**
     *  Builds a NewOrganizationEvent upon verifying input fields, and relays information to controller to create the new organization.
     */
    public void fireSubmissionEvent() {
        // Verify fields
        if (!verifyInputFields()) {
            return;
        }

        // Input fields verified.. continue..

        // Create a NewOrganizationEvent to be submitted to controller for processing
        NewOrganizationEvent noe;

        // If bi-weekly pay periods, fill all parameters. If not, we can leave out the month and day fields.
        if ((Recurrence)payPeriodRecurrence.getSelectedItem() == Recurrence.BIWEEKLY) {
            noe = new NewOrganizationEvent(this, name_input.getText(), (Recurrence)payPeriodRecurrence.getSelectedItem(), (Integer)yearInput.getSelectedItem(), (Integer)monthInput.getSelectedItem(), (Integer)dayInput.getSelectedItem());
        } else {
            noe = new NewOrganizationEvent(this, name_input.getText(), (Recurrence)payPeriodRecurrence.getSelectedItem(), (Integer)yearInput.getSelectedItem());
        }

        // Fire event
        creatorListener.submitNewOrganization(noe);
    }


    /**
     * Verify input fields to ensure no values are missing or out of range.
     * @return true if fields are complete
     */
    private boolean verifyInputFields() {
        boolean verifyMonthDay = false;

        if (payPeriodRecurrence.getSelectedItem() == null) {
            return false;
        } else if ((Recurrence)payPeriodRecurrence.getSelectedItem() == Recurrence.BIWEEKLY) {
            verifyMonthDay = true;
        } else {
            verifyMonthDay = false;
        }

        if (name_input.getText().isEmpty()) {
            return false;
        } else if (yearInput.getSelectedItem() == null) {
            return false;
        } else if (verifyMonthDay && monthInput.getSelectedItem() == null) {
            return false;
        } else if (verifyMonthDay && daysModel.getSelectedItem() == null) {
            return false;
        }

        return true;
    }
}
