package com.austinramsay.timekeeper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Alert {


    public static void display(String message) {


        /*
        // Create a window to display the message
         */
        Stage window = new Stage();


        /*
        // Build a label to show the message
         */
        Label message_label = new Label(message);


        /*
        // Create a 'Close' button
         */
        Button close = new Button("Close");
        close.setOnAction(e -> window.close());


        /*
        // Build a VBox to house the button and label
        // Center all nodes
        // Add a 10 pixel padding
         */
        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));


        /*
        // Add the label and button to the VBox
         */
        root.getChildren().addAll(message_label, close);


        /*
        // Build a scene for root
         */
        Scene scene = new Scene(root);


        /*
        // Finally, set the scene for the window
        // Display the window
         */
        window.setScene(scene);
        window.showAndWait();


    }
}
