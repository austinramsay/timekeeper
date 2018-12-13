package com.austinramsay.model;

import com.austinramsay.timekeeper.Employee;

import javax.swing.*;
import java.awt.*;

public class EmployeeListRenderer extends DefaultListCellRenderer {


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Employee employee = (Employee)value;

        ((JLabel)renderer).setText(String.format(" %s ", employee.getName()));

        return renderer;

    }


}
