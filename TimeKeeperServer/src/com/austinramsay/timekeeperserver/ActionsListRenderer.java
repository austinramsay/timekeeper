package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.Employee;
import com.austinramsay.timekeeperobjects.EmployeeAction;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Map;

public class ActionsListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Map.Entry<Calendar, EmployeeAction> entry = (Map.Entry<Calendar, EmployeeAction>)value;

        Calendar entry_date = entry.getKey();

        String entry_date_str = String.format("%d/%d %d:%d",
                (entry_date.get(Calendar.MONTH) + 1),
                entry_date.get(Calendar.DAY_OF_MONTH),
                entry_date.get(Calendar.HOUR_OF_DAY),
                entry_date.get(Calendar.MINUTE));

        EmployeeAction entry_action = entry.getValue();
        String entry_action_str;

        if (entry_action == EmployeeAction.CLOCKIN) {
            entry_action_str = "Clocked In";
        } else {
            entry_action_str = "Clocked Out";
        }

        String full_entry = String.format("%s: %s", entry_date_str, entry_action_str);

        ((JLabel)renderer).setText(String.format("%s", full_entry));

        return renderer;

    }
}
