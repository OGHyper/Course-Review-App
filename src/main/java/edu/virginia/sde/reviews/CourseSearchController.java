package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class CourseSearchController {
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

    public void searchCourse(ActionEvent event){

    }

    public void registerCourse(ActionEvent event){

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
}
