package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javax.swing.text.html.Option;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class MyReviewsController {
    private CourseReviewsApplication application;
    private Student loggedInStudent;
    private final DatabaseDriver db = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());
    @FXML
    private ListView<CourseReview> myReviews;

    public void setScene(){
        try {
            myReviews.getItems().addAll(getMyReviews());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setLoggedInStudent(Student student){
        this.loggedInStudent = student;
        //System.out.println(this.loggedInStudent + " is set to loggedInStudent");
    }

    public Student getLoggedInStudent(){
        return this.loggedInStudent;
    }

    public void setApplication(CourseReviewsApplication application){
        this.application = application;
    }

    public List<CourseReview> getMyReviews() throws SQLException {
        return db.getReviewsFromStudent(this.loggedInStudent);
    }

    public void goToCourseSearch() throws Exception {
        application.switchToCourseSearch(this.loggedInStudent);
    }

    public void goToCourseReview(){
        // TODO: Implement this
    }

    /*@Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            myReviews.getItems().addAll(getMyReviews());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
}
