package com.austinramsay.events;

public interface CorrectionsActionListener {
    void fireRemoveCorrection();
    void fireOpenCorrection();
    void closeWindow();
}
