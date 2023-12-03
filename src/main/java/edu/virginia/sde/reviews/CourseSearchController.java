package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class CourseSearchController {
    private final Paint red = Paint.valueOf("b12525");

    private final Paint green = Paint.valueOf("#37e127");
    private Stage currStage;
    private Student loggedInStudent;
    private CourseReviewsApplication application;
    private DatabaseDriver db;
    @FXML
    private TextField courseSubject;
    @FXML
    private TextField courseNumber;
    @FXML
    private TextField courseTitle;
    @FXML
    private Label buttonMessage;

    public void searchCourse(ActionEvent event){

    }

    public void registerCourse(ActionEvent event) throws SQLException {
        String subject = courseSubject.getText();
        // I might need to check if this is actually a number being passed in
        int number = Integer.parseInt(courseNumber.getText());
        String title = courseTitle.getText();
        if (subject.isEmpty() || courseNumber.getText().isEmpty() || title.isEmpty()){
            handleButton("Cannot have empty fields", red);
        }
        else if (db.courseAlreadyExists(subject, number, title)){
            handleButton("Course already exists", red);
        }
        else {
            Course newCourse = new Course(subject, number, title);
            db.addCourse(newCourse);
            handleButton("Course successfully added", green);
        }
    }

    public void returnToLogin(ActionEvent event) throws Exception {
        application.switchToLogin();
    }
    public void setApplication(CourseReviewsApplication application){
        this.application = application;
    }

    public void setDatabaseDriver(DatabaseDriver dbDriver){
        this.db = dbDriver;
    }

    public void setLoggedInStudent(Student loggedInStudent) {
        this.loggedInStudent = loggedInStudent;
    }

    public void handleButton(String message, Paint color) {
        buttonMessage.setTextFill(color);
        buttonMessage.setText(message);
    }
}
