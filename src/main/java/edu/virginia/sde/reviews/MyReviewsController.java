package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Paint;

import javax.swing.text.html.Option;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class MyReviewsController {
    private final Paint red = Paint.valueOf("b12525");

    private final Paint green = Paint.valueOf("#37e127");
    private CourseReviewsApplication application;
    private Student loggedInStudent;
    private final DatabaseDriver db = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());
    @FXML
    private ListView<CourseReview> myReviews;
    @FXML
    private Label buttonMessage;

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

    public void selectCourse() throws Exception {
        CourseReview selectedCourseReview = myReviews.getSelectionModel().getSelectedItem();
        if (selectedCourseReview != null){
            var course = new Course(selectedCourseReview.getCourseSubject(), selectedCourseReview.getCourseNumber(), selectedCourseReview.getCourseTitle());
            application.goToCourseReviews(course, loggedInStudent);
        }
        else{
            handleButton("No review selected", red);
        }
    }

    public void goToCourseSearch() throws Exception {
        application.switchToCourseSearch(loggedInStudent);
    }

    public void handleButton(String message, Paint color) {
        buttonMessage.setTextFill(color);
        buttonMessage.setText(message);
    }

}
