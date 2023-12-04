package edu.virginia.sde.reviews;

import javafx.fxml.FXML;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

import java.util.*;
import java.sql.*;
public class ReviewsController {
    private final Paint red = Paint.valueOf("b12525");

    private final Paint green = Paint.valueOf("#37e127");
    private CourseReviewsApplication application;
    private final DatabaseDriver db = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());
    private Student loggedInStudent;
    private Course course;
    private CourseReview loggedInStudentReview;
    private final Integer[] ratingValues = {1, 2, 3 ,4 ,5};

    @FXML
    private ListView<CourseReview> reviewsForCourse;
    @FXML
    private Label courseName;
    @FXML
    private Label studentRating;
    @FXML
    private Label studentComment;
    @FXML
    private ChoiceBox<Integer> ratingChoice;
    @FXML
    private TextField newComment;
    @FXML
    private Label buttonMessage;

    public void setScene() throws SQLException {
        reviewsForCourse.getItems().addAll(getReviewsForCourse(this.course));
        courseName.setText(String.format("Reviews for %s %d: %s", course.getSubjectNmeumonic(), course.getCourseNumber(), course.getCourseTitle()));
        ratingChoice.getItems().addAll(ratingValues);
    }

    public void setApplication(CourseReviewsApplication application){
        this.application = application;
    }

    public void postStudentReview() throws SQLException {
        // TODO: Should take all the inputs and return a new CourseReview object
        int rating = ratingChoice.getValue();
        String comment = newComment.getText();
        if (comment.isEmpty()){
            comment = "No comment";
        }
        CourseReview inputtedReview = new CourseReview(loggedInStudent.getUsername(), course.getId(), course.getSubjectNmeumonic(),
                course.getCourseNumber(), course.getCourseTitle(), rating, comment, new Timestamp(System.currentTimeMillis()));
        if (db.getReviewsFromStudent(loggedInStudent).contains(inputtedReview)){
            updateStudentReview(inputtedReview);
        }
        else {
            db.addReview(inputtedReview);
            handleButton("Review posted", green);
        }
    }

    public void updateStudentReview(CourseReview review) throws SQLException {
        // TODO: should update the student's review with the new one
        db.updateReview(review);
        this.studentRating.setText(String.format("Rating: %d", review.getRating()));
        this.studentComment.setText(review.getComment());
        handleButton("Review updated", green);
    }

    public List<CourseReview> getReviewsForCourse(Course course) throws SQLException {
        return db.getReviewsFromCourse(course);
    }

    public void goToCourseSearch() throws Exception {
        application.switchToCourseSearch(loggedInStudent);
    }

    public void handleButton(String message, Paint color) {
        buttonMessage.setTextFill(color);
        buttonMessage.setText(message);
    }
}
