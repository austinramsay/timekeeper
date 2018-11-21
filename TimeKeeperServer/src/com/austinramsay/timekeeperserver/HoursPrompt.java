package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.EmployeeAction;
import jdk.nashorn.internal.scripts.JO;
import sun.awt.image.ImageWatched;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class HoursPrompt {

    private NewHoursEventListener requestListener;
    private DefaultComboBoxModel<Map.Entry<Calendar, EmployeeAction>> actionModel = new DefaultComboBoxModel<>();

    private JComboBox<Integer> month = new JComboBox<>();
    private JComboBox<Integer> day = new JComboBox<>();
    private JComboBox<Integer> year = new JComboBox<>();
    private JComboBox<Integer> hour = new JComboBox<>();
    private JComboBox<Integer> minute = new JComboBox<>();
    private JComboBox<Map.Entry<Calendar, EmployeeAction>> availableActions;

    public HoursPrompt(NewHoursEventListener requestListener) {
        this.requestListener = requestListener;
    }

    public void showNewHoursPrompt() {

        // Define prompt dialog frame
        JDialog prompt = new JDialog();
        prompt.setTitle("Create Event");
        prompt.setLayout(new BorderLayout());

        // Create labels and center content
        JLabel dateLabel = new JLabel("Date:");
        JLabel timeLabel = new JLabel("Time:");
        JLabel amountLabel = new JLabel("Amount of Hours:");
        JLabel correspondingLabel = new JLabel("Corresponding Clock-In:");
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        amountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        correspondingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Define hours amount input field
        JTextField amountInput = new JTextField();

        // Define available action events dropdown
        availableActions = new JComboBox<>();
        availableActions.setRenderer(new ActionsBoxRenderer());
        availableActions.setModel(actionModel);
        populateAvailableActions();  // Populate actions with employee action log (filtered with only clock-in events), this is requested from the requestListener

        // Define auto-create checkbox
        JCheckBox autoCreate = new JCheckBox("Auto-create Clock Out Event");
        autoCreate.setToolTipText("Enabling this option will create a clock-out event that matches the amount of hours the employee worked with respect to the clock-in time you select.");

        // Add 1-31 for day values
        for (int a = 1; a <= 31; a++) {
            day.addItem(Integer.valueOf(a));
        }

        // Add 1-12 for month values
        for (int b = 1; b <= 12; b++) {
            month.addItem(Integer.valueOf(b));
        }

        // Add 2000-2030 for year values
        for (int c = 2000; c <= 2030; c++) {
            year.addItem(Integer.valueOf(c));
        }

        // Add 1-12 for hour values
        for (int d = 1; d <= 12; d++) {
            hour.addItem(Integer.valueOf(d));
        }

        // Add 1-59 for minute values
        for (int e = 1; e <= 59; e++) {
            minute.addItem(Integer.valueOf(e));
        }

        // Define grid panel to set combo boxes and labels in
        JPanel gridPanel = new JPanel();
        GridLayout grid = new GridLayout(4,4,8,8);
        gridPanel.setLayout(grid);

        // First row (Date entry dropdowns)
        gridPanel.add(dateLabel);
        gridPanel.add(month);
        gridPanel.add(day);
        gridPanel.add(year);

        // Second row (Time entry dropdowns)
        gridPanel.add(timeLabel);
        gridPanel.add(hour);
        gridPanel.add(minute);
        gridPanel.add(Box.createGlue());

        // Third row (Hours amount entry box)
        gridPanel.add(amountLabel);
        gridPanel.add(amountInput);
        gridPanel.add(Box.createGlue());
        gridPanel.add(Box.createGlue());

        // Fourth row (Corresponding action dropdown)
        gridPanel.add(correspondingLabel);
        gridPanel.add(availableActions);
        gridPanel.add(Box.createGlue());
        gridPanel.add(Box.createGlue());

        // Define a titled border with 5 pixel padding
        Border padding = BorderFactory.createEmptyBorder(5,5,5,5);
        Border titled = BorderFactory.createTitledBorder("Hours Clocked Information");
        Border compounded = BorderFactory.createCompoundBorder(padding, titled);
        gridPanel.setBorder(compounded);

        // Create JPanel containing 'Submit' and 'Cancel' buttons
        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.LINE_AXIS));
        JButton submit = new JButton("Submit");
        JButton cancel = new JButton("Cancel");
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(submit);
        buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
        buttonBox.add(cancel);
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));

        // Create compounded JPanel 'lower panel' containing 'Auto-create' checkbox with 'buttonBox' below
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.PAGE_AXIS));
        lowerPanel.add(Box.createVerticalGlue());
        lowerPanel.add(autoCreate);
        lowerPanel.add(Box.createRigidArea(new Dimension(0,5)));
        lowerPanel.add(buttonBox);
        lowerPanel.add(Box.createVerticalGlue());

        // Define 'Submit' button logic
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Check if auto-create clock-out is selected
                if (autoCreate.isSelected()) {

                    // Create a clock-out event based off clock-in event selection as well as an hours entry

                    // Determine amount of hours to clock from input field information (attempt to cast double from string input)
                    Double hoursClocked;
                    Double millisDbl;
                    try {
                        // Cast input field text as a double
                        hoursClocked = Double.valueOf(amountInput.getText());

                        // Convert hours to milliseconds (1 hour = 3,600,000 milliseconds)
                        millisDbl = hoursClocked * 3600000;

                    } catch (NullPointerException npe) {
                        JOptionPane.showMessageDialog(prompt, "Please input a numberic value in the hours input field.");
                        return;
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(prompt, "Numeric decimal not found in hours input field.");
                        return;
                    }

                    // Determine clock-in time based off available action that is selected
                    Calendar clockInTime;
                    try {
                        clockInTime = (Calendar) ((Map.Entry<Calendar, EmployeeAction>) availableActions.getSelectedItem()).getKey();
                    } catch (NullPointerException npe) {
                        JOptionPane.showMessageDialog(prompt, "No corresponding clock-in time selected.");
                        return;
                    }

                    // Determine clock-out time by offsetting clock-in time with the determined hours clocked numeric value
                    Calendar clockOutTime = Calendar.getInstance();
                    clockOutTime.set(clockInTime.get(Calendar.YEAR), clockInTime.get(Calendar.MONTH), clockInTime.get(Calendar.DAY_OF_MONTH), clockInTime.get(Calendar.HOUR_OF_DAY), clockInTime.get(Calendar.MINUTE));

                    // Offset clock-out time with clock-in time
                    Integer millis = millisDbl.intValue();
                    clockOutTime.add(Calendar.MILLISECOND, millis);

                    // Relay new action entry and hours amount to the request listener
                    requestListener.fireNewHoursEventSubmission(hoursClocked, clockOutTime);

                } else {

                    // Create manually selected time values clock-out event as well as an hours entry
                    Integer selectedMonth = ((Integer)month.getSelectedItem() - 1); // Calendar month goes from 0-11 (0 for January)
                    Integer selectedDay = (Integer)day.getSelectedItem();

                }

                prompt.setVisible(false);

                // requestListener.fireEvent
            }
        });

        // Define 'Cancel' button logic
        // Close prompt
        cancel.addActionListener(e -> {
            prompt.setVisible(false);
        });

        // Define 'Auto-create' checkbox logic
        // When checked, disable input fields
        autoCreate.addChangeListener(e -> {

            // Set auto create method will enable/disable necessary fields depending on if feature is selected or not
            setAutoCreate(autoCreate.isSelected());

        });

        // Set default to auto-create a clock-out event
        autoCreate.setSelected(true);

        // Add elements to prompt frame
        prompt.add(gridPanel, BorderLayout.CENTER);
        prompt.add(lowerPanel, BorderLayout.SOUTH);
        prompt.pack();
        prompt.setLocationRelativeTo(null);
        prompt.setVisible(true);

        // Preset today's date and time in the combo box options
        Calendar today = Calendar.getInstance();
        int todaysMonth = today.get(Calendar.MONTH) + 1;
        int todaysDay = today.get(Calendar.DAY_OF_MONTH);
        int todaysYear = today.get(Calendar.YEAR);
        int hourNow = today.get(Calendar.HOUR);
        // If hour now is noon/midnight, the calendar hour will be 0 which is not an option.
        // This is actually 12 o'clock
        if (hourNow == 0) {
            hourNow = 12;
        }
        int minuteNow = today.get(Calendar.MINUTE);
        month.setSelectedItem(todaysMonth);
        day.setSelectedItem(todaysDay);
        year.setSelectedItem(todaysYear);
        hour.setSelectedItem(hourNow);
        minute.setSelectedItem(minuteNow);
    }


    private void populateAvailableActions() {

        // Retrieve employee available actions (only clock-in events between the dates of the selected pay period are returned)
        LinkedHashMap<Calendar, EmployeeAction> actions = requestListener.retrieveEmployeeActions();

        // Clear current model data
        actionModel.removeAllElements();

        // If actions map is null..there is no need to continue
        if (actions == null) {
            return;
        }

        // Iterate action entries and add each element to model
        for (Map.Entry<Calendar, EmployeeAction> entry : actions.entrySet()) {
            actionModel.addElement(entry);
        }
    }


    private void setAutoCreate(boolean enabled) {

        // If auto-create is enabled, we can disable the date & time dropdown boxes because
        // the date/time information will be defined by the selected corresponding action event (ex. the last event when the employee clocked in)
        if (enabled) {
            month.setEnabled(false);
            day.setEnabled(false);
            year.setEnabled(false);
            hour.setEnabled(false);
            minute.setEnabled(false);
            availableActions.setEnabled(true);
        } else {
            month.setEnabled(true);
            day.setEnabled(true);
            year.setEnabled(true);
            hour.setEnabled(true);
            minute.setEnabled(true);
            availableActions.setEnabled(false);
        }

        // Using auto-create, we can define the new clock-out event by adding the amount of hours from the 'amount' input field, to the corresponding clock-in event
    }
}
