package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class LogInController {
    private CourseReviewsApplication application;
    private DatabaseDriver db;
    private final  Paint red = Paint.valueOf("b12525");

    private final Paint green = Paint.valueOf("#37e127");

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

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
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty()){
            handleButton("Username cannot be empty", red);
        }
        else if (password.isEmpty()){
            handleButton("Password cannot be empty", red);
        }
        if (db.studentExists(username)){ // Need to check if the user is already registered.
            handleButton("User already exists!", red);
        }
        else if (!db.studentExists(username)){ // If not, create new student in the database then display success message
            //Student newStudent = new Student(username.getText(), password.getText());
            try {
                db.addStudent(new Student(username, password));
                handleButton("User successfully created!", green);
            } catch(SQLException e){
                try{
                    db.rollback();
                } catch (SQLException ex){
                    handleButton("Error while creating user", red);
                }
                e.printStackTrace();
            }
        }
    }

    public void login(ActionEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Student signInStudent = new Student(username, password);
        if (username.isEmpty()){
            handleButton("Username cannot be empty", red);
        }
        else if (password.isEmpty()){
            handleButton("Password cannot be empty", red);
        }
        if (db.studentExists(username)) {    // Check if the user is in the database, then sign in and switch scenes
            if (db.getPasswordForStudent(signInStudent).equals(Optional.of(password))){
                //handleButton("*goes to course search*", green);
                application.switchToCourseSearch(signInStudent);
            }
            else{
                handleButton("Incorrect password", red);
            }
        }
        else if (!db.studentExists(username)){   // If the user is not in the database, then display error message
            handleButton("User does not exist", red);
        }
    }

    public void quit(ActionEvent event){
        System.exit(0);
    }

    public void handleButton(String message, Paint color) {
        buttonMessage.setTextFill(color);
        buttonMessage.setText(message);
    }

    public void setApplication(CourseReviewsApplication application){
        this.application = application;
    }

    public void setDatabaseDriver(DatabaseDriver db){
        this.db = db;
    }
}
