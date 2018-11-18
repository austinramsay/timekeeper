package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.EmployeeAction;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Map;

public class ActionPrompt {

    private Map.Entry<Calendar, EmployeeAction> newEntry;

    public ActionPrompt() {
        newEntry = null;
    }

    public void showNewActionPrompt() {
        JDialog prompt = new JDialog();
        prompt.setTitle("Create Action Event");
        prompt.setLayout(new BorderLayout());

        JLabel dateLabel = new JLabel("Date:");
        JLabel typeLabel = new JLabel("Type:");
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JComboBox<Integer> month = new JComboBox<>();
        JComboBox<Integer> day = new JComboBox<>();
        JComboBox<Integer> year = new JComboBox<>();
        JComboBox<String> type = new JComboBox<>();

        for (int a = 1; a <= 31; a++) {
            day.addItem(Integer.valueOf(a));
        }

        for (int b = 1; b <= 12; b++) {
            month.addItem(Integer.valueOf(b));
        }

        for (int c = 2000; c <= 2030; c++) {
            year.addItem(Integer.valueOf(c));
        }

        type.addItem("Clocked In");
        type.addItem("Clocked Out");

        JPanel gridPanel = new JPanel();
        GridLayout grid = new GridLayout(2,4,8,8);
        gridPanel.setLayout(grid);
        gridPanel.add(dateLabel);
        gridPanel.add(month);
        gridPanel.add(day);
        gridPanel.add(year);
        gridPanel.add(typeLabel);
        gridPanel.add(type);
        gridPanel.add(Box.createGlue());
        gridPanel.add(Box.createGlue());
        Border padding = BorderFactory.createEmptyBorder(5,5,5,5);
        Border titled = BorderFactory.createTitledBorder("Action Information");
        Border compounded = BorderFactory.createCompoundBorder(padding, titled);
        gridPanel.setBorder(compounded);

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

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer selectedMonth = (Integer) month.getSelectedItem();
                Integer selectedDay = (Integer)day.getSelectedItem();
                Integer selectedYear = (Integer)year.getSelectedItem();

                Calendar actionDate = Calendar.getInstance();
                actionDate.set(selectedYear, selectedMonth, selectedDay);

                EmployeeAction actionType = (type.getSelectedIndex() == 0) ? EmployeeAction.CLOCKIN : EmployeeAction.CLOCKOUT;

                Map.Entry<Calendar, EmployeeAction> newEntry = new AbstractMap.SimpleEntry<Calendar, EmployeeAction>(actionDate, actionType);

                setNewEntry(newEntry);
                prompt.setVisible(false);
            }
        });

        cancel.addActionListener(e -> {
            prompt.setVisible(false);
        });

        prompt.add(gridPanel, BorderLayout.CENTER);
        prompt.add(buttonBox, BorderLayout.SOUTH);
        prompt.pack();
        prompt.setLocationRelativeTo(null);
        prompt.setVisible(true);

        // Select today's date
        Calendar today = Calendar.getInstance();
        int todaysMonth = today.get(Calendar.MONTH) + 1;
        int todaysDay = today.get(Calendar.DAY_OF_MONTH);
        int todaysYear = today.get(Calendar.YEAR);
        month.setSelectedItem(todaysMonth);
        day.setSelectedItem(todaysDay);
        year.setSelectedItem(todaysYear);
    }

    private void setNewEntry(Map.Entry<Calendar, EmployeeAction> newEntry) {
        this.newEntry = newEntry;
    }

    public Map.Entry<Calendar, EmployeeAction> getNewEntry() {
        return this.newEntry;
    }

}
