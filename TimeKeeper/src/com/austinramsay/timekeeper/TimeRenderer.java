package com.austinramsay.timekeeper;

public class TimeRenderer {

    /**
     * Adds a zero before a single digit if needed.
     * Example. 5:5pm -> 5:05pm
     * @param minutes minutes value
     * @return minutes value in readable clock form
     */
    public static String renderMinutes(int minutes) {
        String mins = String.valueOf(minutes);

        if (minutes < 10) {
            // Need to add a zero before the single digit value for readability
            // Ex. if minutes is 5, turn it into 05.
            // For something like 5:5pm -> 5:05pm
            return String.format("0%s", mins);
        } else {
            return mins;
        }
    }

}
