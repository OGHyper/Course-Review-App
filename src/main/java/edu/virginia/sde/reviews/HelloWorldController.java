package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class HelloWorldController {
    @FXML
    private Label messageLabel;

    public void handleButton() {
        messageLabel.setText("You pressed the button!");
    }
}
