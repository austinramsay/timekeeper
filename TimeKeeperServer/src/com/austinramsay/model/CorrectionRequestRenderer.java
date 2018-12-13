package com.austinramsay.model;

import com.austinramsay.controller.TimeKeeperServer;
import com.austinramsay.timekeeper.CorrectionRequest;

import javax.swing.*;
import java.awt.*;

public class CorrectionRequestRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        CorrectionRequest correction = (CorrectionRequest) value;

        // Find employee name that corresponds with ID number
        String empName = TimeKeeperServer.current_org.getEmployee(correction.getEmployeeId()).getName();

        ((JLabel)renderer).setText(String.format(" %s (%s) ", empName, correction.getDate()));

        return renderer;

    }

}
