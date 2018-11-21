package com.austinramsay.timekeeperserver;

import com.austinramsay.timekeeperobjects.EmployeeAction;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Map;

public class ActionsBoxRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Map.Entry<Calendar, EmployeeAction> entry = (Map.Entry<Calendar, EmployeeAction>)value;

        if (entry == null) {
            return renderer;
        }

        Calendar entry_date = entry.getKey();

        // Get rendered string for minutes (ex. turn 5:5pm -> 5:05pm)
        String mins = TimeRenderer.renderMinutes(entry_date.get(Calendar.MINUTE));

        String entry_date_str = String.format("%d/%d %d:%s",
                (entry_date.get(Calendar.MONTH) + 1),
                entry_date.get(Calendar.DAY_OF_MONTH),
                (entry_date.get(Calendar.HOUR) == 0 ? 12 : (entry_date.get(Calendar.HOUR)) ),
                mins);

        ((JLabel)renderer).setText(entry_date_str);

        return renderer;

    }
}
