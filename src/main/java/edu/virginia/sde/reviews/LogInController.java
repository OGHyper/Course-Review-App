package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LogInController {
    @FXML
    public void register(ActionEvent event){
        // Need to check if the user is already registered.
        // If not, create new student in the database then display success message
        // Display a message if the user is already in the database
    }

    public void login(ActionEvent event){
        // Check if the user is in the database, then sign in and switch scenes
        // If the user is not in the database, then display error message
    }
}
