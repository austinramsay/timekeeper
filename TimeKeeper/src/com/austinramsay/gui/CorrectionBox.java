package com.austinramsay.gui;

import com.austinramsay.controller.CorrectionRequestHandler;
import com.austinramsay.timekeeper.CorrectionRequest;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CorrectionBox {

    private CorrectionRequestHandler requestHandler;
    private int employeeId;


    public CorrectionBox(int employeeId) {
        this.employeeId = employeeId;
    }


    public void setRequestHandler(CorrectionRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }


    /**
     * GUI interface for user input to submit a time correction.
     * @return window containing description text area with submit/cancel buttons
     */
    private Stage getSubmissionFrame() {

        Stage correctionBox = new Stage();

        TextArea descArea = new TextArea();
        descArea.setPrefColumnCount(28);
        descArea.setPrefRowCount(6);
        descArea.setWrapText(true);
        descArea.setPromptText("Enter request description here...");

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {

            // Get description typed by user
            String description = descArea.getText();

            // Verify the user has input a description greater than 30 characters
            if (description == null || description.isEmpty() || description.length() < 30) {
                Platform.runLater(() -> {
                    Alert.display("Please enter a description greater than 30 characters.");
                });
                return;
            }

            // Create correction request object
            CorrectionRequest correction = new CorrectionRequest(employeeId, description);

            // Send correction through request handler
            requestHandler.sendCorrection(correction);

            // Done with this window
            correctionBox.close();
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> {
            correctionBox.close();
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(submit, cancel);

        VBox root = new VBox(8);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(new Label("Enter description:"), descArea, buttonBox);

        Scene main = new Scene(root);
        correctionBox.setScene(main);

        return correctionBox;
    }


    /**
     * Verifies a request handler is in place to process a submitted request.
     * Displays the correction box GUI interface.
     */
    public void show() {

        if (requestHandler == null) {
            Platform.runLater(() -> {
                Alert.display("Correction frame missing request handler component.");
            });
            return;
        }

        Stage window = getSubmissionFrame();
        window.show();

    }

}
