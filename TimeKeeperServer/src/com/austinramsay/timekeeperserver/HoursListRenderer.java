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

        String entry_date_str = String.format("%d/%d %d:%d",
                (entry_date.get(Calendar.MONTH) + 1),
                entry_date.get(Calendar.DAY_OF_MONTH),
                entry_date.get(Calendar.HOUR_OF_DAY),
                entry_date.get(Calendar.MINUTE));

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
