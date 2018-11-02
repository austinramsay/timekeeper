package com.austinramsay.timekeeper;

import javafx.application.Platform;

/**
 * Provides sync service during specified interval to keep employee time's up to date
 */
public class SyncWorker implements Runnable {

    private final RequestWorker worker;
    private final int WAIT_TIME = 50000; // 5 Minutes
    public SyncWorker(RequestWorker worker) {
        this.worker = worker;
    }


    /**
     * Provide endless loop with 5 minute update times.
     * Requests a list of times from the server.
     */
    @Override
    public void run() {
        try {
            while (true) {
                worker.syncTimes();
                Thread.sleep(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            Platform.runLater(() -> Alert.display("Sync worker interrupted."));
        }
    }

}
