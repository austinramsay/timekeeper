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

        Calendar entry_date = entry.getKey();

        String entry_date_str = String.format("%d/%d %d:%d",
                (entry_date.get(Calendar.MONTH) + 1),
                entry_date.get(Calendar.DAY_OF_MONTH),
                entry_date.get(Calendar.HOUR_OF_DAY),
                entry_date.get(Calendar.MINUTE));

        ((JLabel)renderer).setText(entry_date_str);

        return renderer;

    }
}
