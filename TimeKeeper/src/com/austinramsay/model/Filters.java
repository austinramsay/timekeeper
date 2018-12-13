package com.austinramsay.model;

import com.austinramsay.timekeeper.EmployeeAction;
import com.austinramsay.timekeeper.TimeRenderer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class Filters {



    /**
     * If start & end arguments are null, the filter will return all entries
     * @param start the start day to dependent on end date
     * @param end the end day to check dependent on start date
     * @param actions the action log of the employee
     * @return an observable list of formatted string action entries (Date: Action)
     */
    public static ObservableList<String> getActionEntries(Calendar start, Calendar end, LinkedHashMap<Calendar, EmployeeAction> actions) {

        // Create observable list to add to while searching for entries
        ObservableList<String> obs_actions = FXCollections.observableArrayList();

        for (Map.Entry<Calendar, EmployeeAction> entry : actions.entrySet()) {

            // Get the entry date for the action
            Calendar entry_date = entry.getKey();

            // Get rendered string for minutes (ex. turn 5:5pm -> 5:05pm)
            String mins = TimeRenderer.renderMinutes(entry_date.get(Calendar.MINUTE));

            // If the start or end date argumented is null, include all entries
            // If the entry date is between the requested start/end dates, include the entry
            if (((start == null) && (end == null)) || (betweenDate(start, end, entry_date))) {
                String entry_date_str = String.format("%d/%d/%d %d:%s",
                        (entry_date.get(Calendar.MONTH) + 1),
                        entry_date.get(Calendar.DAY_OF_MONTH),
                        entry_date.get(Calendar.YEAR),
                        entry_date.get(Calendar.HOUR) == 0 ? 12 : (entry_date.get(Calendar.HOUR)),
                        mins);

                EmployeeAction entry_action = entry.getValue();
                String entry_action_str;

                if (entry_action == EmployeeAction.CLOCKIN) {
                    entry_action_str = "Clocked In";
                } else {
                    entry_action_str = "Clocked Out";
                }

                String full_entry = String.format("%s: %s", entry_date_str, entry_action_str);

                obs_actions.add(full_entry);
            }

        } // End action entry search 'for' loop

        return obs_actions;
    }


    /**
     * If start & end arguments are null, the filter will return all entries
     * @param start the start day to dependent on end date
     * @param end the end day to check dependent on start date
     * @param hours the hours log of the employee
     * @return an observable list of formatted string hours entries (Date: Hours clocked)
     */
    public static ObservableList<String> getHoursEntries(Calendar start, Calendar end, LinkedHashMap<Calendar, Double> hours) {

        // Create observable list to add to while searching for entries
        ObservableList<String> obs_hours = FXCollections.observableArrayList();

        for (Map.Entry<Calendar, Double> entry : hours.entrySet()) {

            Calendar entry_date = entry.getKey();

            // If the start or end date argumented is null, include all entries
            // If the entry date is between the requested start/end dates, include the entry
            if (((start == null) && (end == null)) || (betweenDate(start, end, entry_date))) {
                String entry_date_str = String.format("%d/%d/%d %d:%d",
                        (entry_date.get(Calendar.MONTH) + 1),
                        entry_date.get(Calendar.DAY_OF_MONTH),
                        entry_date.get(Calendar.YEAR),
                        entry_date.get(Calendar.HOUR) == 0 ? 12 : (entry_date.get(Calendar.HOUR)),
                        entry_date.get(Calendar.MINUTE));

                Double entry_hours = round(entry.getValue());

                String full_entry = String.format("%s: %.2f Hours", entry_date_str, entry_hours);

                obs_hours.add(full_entry);
            }

        } // End hours entry search 'for' loop

        return obs_hours;
    }


    /**
     * Tests two Calendar dates MM/dd/YYYY for equality
     * @return true if MM/dd/YYYY match, false if not
     */
    private static boolean betweenDate(Calendar start, Calendar end, Calendar compare) {

        // First just check if the date fits between the start and end dates
        if (compare.after(start) && compare.before(end))
            return true;

        // Check if the date matches exactly the start date
        if ((start.get(Calendar.YEAR) == compare.get(Calendar.YEAR)) && (start.get(Calendar.MONTH) == compare.get(Calendar.MONTH)) && (start.get(Calendar.DAY_OF_MONTH) == compare.get(Calendar.DAY_OF_MONTH)))
            return true; StringBuilder builder = new StringBuilder();

        // Check if the date matches exactly the end date
        if ((end.get(Calendar.YEAR) == compare.get(Calendar.YEAR)) && (end.get(Calendar.MONTH) == compare.get(Calendar.MONTH)) && (end.get(Calendar.DAY_OF_MONTH) == compare.get(Calendar.DAY_OF_MONTH)))
            return true;

        return false;
    }


    /**
     * @return a 2 decimal rounded double
     */
    public static double round(double number) {
        BigDecimal to_round = BigDecimal.valueOf(number);
        to_round = to_round.setScale(2, RoundingMode.HALF_UP);
        return to_round.doubleValue();
    }

}
