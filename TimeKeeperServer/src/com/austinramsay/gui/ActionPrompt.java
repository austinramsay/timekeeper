package com.austinramsay.gui;

import com.austinramsay.events.NewActionListener;
import com.austinramsay.timekeeper.EmployeeAction;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class ActionPrompt {

    private NewActionListener requestListener;

    public ActionPrompt(NewActionListener requestListener) {
        this.requestListener = requestListener;
    }

    public void showNewActionPrompt() {

        // Define prompt dialog frame
        JDialog prompt = new JDialog();
        prompt.setTitle("Create Action Event");
        prompt.setLayout(new BorderLayout());

        // Create labels and center content
        JLabel dateLabel = new JLabel("Date:");
        JLabel timeLabel = new JLabel("Time (Hour/min):");
        JLabel typeLabel = new JLabel("Type:");
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Define all combo boxes
        JComboBox<Integer> month = new JComboBox<>();
        JComboBox<Integer> day = new JComboBox<>();
        JComboBox<Integer> year = new JComboBox<>();
        JComboBox<Integer> hour = new JComboBox<>();
        JComboBox<Integer> minute = new JComboBox<>();
        JComboBox<String> type = new JComboBox<>();

        // Add 1-31 for day values
        for (int a = 1; a <= 31; a++) {
            day.addItem(Integer.valueOf(a));
        }

        // Add 1-12 for month values
        for (int b = 1; b <= 12; b++) {
            month.addItem(Integer.valueOf(b));
        }

        // Add 2000-2030 for year values
        for (int c = 2018; c <= 2030; c++) {
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

        // Add clock in/out values to type combo box
        type.addItem("Clocked In");
        type.addItem("Clocked Out");

        // Define grid panel to set combo boxes and labels in
        JPanel gridPanel = new JPanel();
        GridLayout grid = new GridLayout(3,4,8,8);
        gridPanel.setLayout(grid);

        // First row
        gridPanel.add(dateLabel);
        gridPanel.add(month);
        gridPanel.add(day);
        gridPanel.add(year);

        // Second row
        gridPanel.add(timeLabel);
        gridPanel.add(hour);
        gridPanel.add(minute);
        gridPanel.add(Box.createGlue());

        // Third row
        gridPanel.add(typeLabel);
        gridPanel.add(type);
        gridPanel.add(Box.createGlue());
        gridPanel.add(Box.createGlue());

        // Define a titled border with 5 pixel padding
        Border padding = BorderFactory.createEmptyBorder(5,5,5,5);
        Border titled = BorderFactory.createTitledBorder("Action Information");
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

        // Define 'Submit' button logic
        // Upon submission, relay combo box values into a calendar date and determine the requested action type (clock in/out)
        // After creating the EmployeeAction with these values, relay the action back to the controller to add to the employee's action log
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer selectedMonth = ((Integer)month.getSelectedItem() - 1); // Calendar month goes from 0-11 (0 for January)
                Integer selectedDay = (Integer)day.getSelectedItem();
                Integer selectedYear = (Integer)year.getSelectedItem();
//                Integer selectedHour = ((Integer)hour.getSelectedItem() - 1);  // Calendar hour goes from 0-11 (0 for noon/midnight)
                Integer selectedHour = ((Integer)hour.getSelectedItem());  // Calendar hour goes from 0-11 (0 for noon/midnight)
                Integer selectedMin = (Integer)minute.getSelectedItem();

                Calendar actionDate = Calendar.getInstance();
                actionDate.set(selectedYear, selectedMonth, selectedDay);
                actionDate.set(Calendar.HOUR, selectedHour);
                actionDate.set(Calendar.MINUTE, selectedMin);

                EmployeeAction actionType = (type.getSelectedIndex() == 0) ? EmployeeAction.CLOCKIN : EmployeeAction.CLOCKOUT;

                requestListener.fireNewActionSubmission(actionDate, actionType);
                prompt.setVisible(false);
            }
        });

        // Define 'Cancel' button logic
        // Close prompt
        cancel.addActionListener(e -> {
            prompt.setVisible(false);
        });

        // Add elements to prompt frame
        prompt.add(gridPanel, BorderLayout.CENTER);
        prompt.add(buttonBox, BorderLayout.SOUTH);
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
}