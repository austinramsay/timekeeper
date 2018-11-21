package com.austinramsay.timekeeperserver;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Map;

public class HoursListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Map.Entry<Calendar, Double> entry = (Map.Entry<Calendar, Double>)value;

        Calendar entry_date = entry.getKey();

        // Get rendered string for minutes (ex. turn 5:5pm -> 5:05pm)
        String mins = TimeRenderer.renderMinutes(entry_date.get(Calendar.MINUTE));

        String entry_date_str = String.format("%d/%d %d:%s",
                (entry_date.get(Calendar.MONTH) + 1),
                entry_date.get(Calendar.DAY_OF_MONTH),
                (entry_date.get(Calendar.HOUR) == 0 ? 12 : (entry_date.get(Calendar.HOUR)) ),
                mins);

        Double entry_hours = round(entry.getValue());

        String full_entry = String.format("%s: %.2f Hours", entry_date_str, entry_hours);

        ((JLabel)renderer).setText(String.format("%s", full_entry));

        return renderer;
    }

    /**
     * @return a 2 decimal rounded double
     */
    private double round(double number) {
        BigDecimal to_round = BigDecimal.valueOf(number);
        to_round = to_round.setScale(2, RoundingMode.HALF_UP);
        return to_round.doubleValue();
    }

}
