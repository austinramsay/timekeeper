package com.austinramsay.timekeeperserver;

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

        // Get rendered string for minutes (ex. turn 5:5pm -> 5:05pm)
        String mins = TimeRenderer.renderMinutes(entry_date.get(Calendar.MINUTE));

        String entry_date_str = String.format("%d/%d %d:%s",
                (entry_date.get(Calendar.MONTH) + 1),
                entry_date.get(Calendar.DAY_OF_MONTH),
                (entry_date.get(Calendar.HOUR) == 0 ? 12 : (entry_date.get(Calendar.HOUR)) ),
                mins);

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
