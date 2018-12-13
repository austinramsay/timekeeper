package com.austinramsay.model;

import com.austinramsay.timekeeper.*;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class PayPeriodsListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        PayPeriod payperiod = (PayPeriod)value;

        Calendar start = payperiod.getStartDate();
        Calendar end = payperiod.getEndDate();
        String payperiod_date = String.format("%d/%d/%d - %d/%d/%d",
                (start.get(Calendar.MONTH) + 1),
                start.get(Calendar.DAY_OF_MONTH),
                start.get(Calendar.YEAR),
                (end.get(Calendar.MONTH) + 1),
                end.get(Calendar.DAY_OF_MONTH),
                end.get(Calendar.YEAR));

        ((JLabel)renderer).setText(String.format("%s", payperiod_date));

        return renderer;

    }

}
