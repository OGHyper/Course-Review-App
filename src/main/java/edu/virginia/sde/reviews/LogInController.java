package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class LogInController {
    private CourseReviewsApplication crapp;
    private DatabaseDriver db;
    private final  Paint red = Paint.valueOf("b12525");

    private final Paint green = Paint.valueOf("#37e127");

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label buttonMessage;

    public void logInController(){
        this.db = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());
        try {
            this.db.connect();
            this.db.createTables();
        } catch (SQLException e) {
            handleButton("Unable to connect to database", red);
        }
    }

    public void register(ActionEvent event) throws SQLException {
        // Need to check if username or password is empty

        if (db.studentExists(username.getText())){ // Need to check if the user is already registered.
            handleButton("User already exists!", red);
        }
        else if (!db.studentExists(username.getText())){ // If not, create new student in the database then display success message
            Student newStudent = new Student(username.getText(), password.getText());
            db.addStudent(newStudent);
            handleButton("User successfully created!", green);
        }
    }

    public void login(ActionEvent event){
        // Check if the user is in the database, then sign in and switch scenes
        // If the user is not in the database, then display error message
        // Check if the username is right but the password is not. Maybe use two booleans
    }

    public void quit(ActionEvent event){

    }

    public void handleButton(String message, Paint color) {
        // Maybe make this take in a string argument and then set whatever condition to the arg
        buttonMessage.setTextFill(color);
        buttonMessage.setText(message);
    }

    public void setApplication(CourseReviewsApplication application){
        this.crapp = application;
    }

    public void setDatabaseDriver(DatabaseDriver db){
        this.db = db;
    }
}
