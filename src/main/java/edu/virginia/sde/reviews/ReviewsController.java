package edu.virginia.sde.reviews;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.paint.Paint;

import java.text.DecimalFormat;
import java.util.*;
import java.sql.*;
public class ReviewsController {
    private static final DecimalFormat df = new DecimalFormat("0.00");
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
    private Label reviewDeletedMsg;
    @FXML
    private ChoiceBox<Integer> ratingChoice;
    @FXML
    private TextArea newComment;
    @FXML
    private Label buttonMessage;
    @FXML
    private Label avgRatingMsg;

    public void setScene() throws SQLException {
        courseName.setText(String.format("Reviews for %s %d: %s", course.getSubjectNmeumonic(), course.getCourseNumber(), course.getCourseTitle()));
        reviewsForCourse.getItems().setAll(getReviewsForCourse(this.course));
        ratingChoice.getItems().addAll(ratingValues);
        avgRatingMsg.setText("Avg. Rating: " + getAvgRating());
        showStudentReview();
    }

    public void setApplication(CourseReviewsApplication application){
        this.application = application;
    }

    public void setLoggedInStudent(Student student){
        this.loggedInStudent = student;
    }

    public void setCourse(Course course){
        this.course = course;
    }

    public void postStudentReview() throws SQLException {
        if (ratingChoice.getValue() == null){
            handleButton("Rating cannot be empty", red);
            return;
        }
        int rating = ratingChoice.getValue();
        String comment = newComment.getText();
        if (comment.isEmpty()){
            comment = "No comment";
        }
        int courseID = db.getCourseId(course.getCourseTitle(), course.getCourseNumber(), course.getCourseTitle());
        CourseReview inputtedReview = new CourseReview(db.getStudentId(loggedInStudent.getUsername()), loggedInStudent.getUsername(), courseID, course.getSubjectNmeumonic(),
                course.getCourseNumber(), course.getCourseTitle(), rating, comment, new Timestamp(System.currentTimeMillis()));
        CourseReview existingReview = db.getReviewOfCourseFromStudent(course, loggedInStudent);
        if (existingReview != null && existingReview.getCourseSubject().equals(inputtedReview.getCourseSubject()) && existingReview.getCourseNumber() == inputtedReview.getCourseNumber() && existingReview.getCourseTitle().equals(inputtedReview.getCourseTitle()) && existingReview.getPostingStudentName().equals(inputtedReview.getPostingStudentName())){
            updateStudentReview(inputtedReview);
        }
        else {
            db.addReview(inputtedReview);
            updateReviewList();
            this.studentRating.setText(String.format("Rating: %d", inputtedReview.getRating()));
            this.studentComment.setText(inputtedReview.getComment());
            handleButton("Review posted", green);
        }
    }

    public void updateStudentReview(CourseReview review) throws SQLException {
        db.updateReview(review);
        this.studentRating.setText(String.format("Rating: %d", review.getRating()));
        this.studentComment.setText(review.getComment());
        updateReviewList();
        handleButton("Review updated", green);
    }

    public void showStudentReview() throws SQLException {
        CourseReview studentReview = db.getReviewOfCourseFromStudent(course, loggedInStudent);
        if (studentReview != null){
            studentRating.setText("Rating: " + studentReview.getRating());
            studentComment.setText(studentReview.getComment());
        }
    }

    public void updateReviewList() throws SQLException {
        reviewsForCourse.getItems().setAll(getReviewsForCourse(this.course));
        //TODO: Have it update the average rating
        avgRatingMsg.setText("Avg. Rating: " + getAvgRating());
    }

    public void deleteStudentReview() throws SQLException {
        db.deleteStudentReview(course, loggedInStudent);
        updateReviewList();
        studentRating.setText("Rating: ");
        studentComment.setText("");
        this.reviewDeletedMsg.setTextFill(green);
        this.reviewDeletedMsg.setText("Review deleted");
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

    public String getAvgRating() throws SQLException {
        var allReviews = db.getReviewsFromCourse(course);
        int total = 0;
        for (CourseReview review : allReviews){
            total += review.getRating();
        }
        double avg = total*1.0 / allReviews.size();
        if (df.format(avg).equals("NaN")){
            return "";
        }
        return df.format(avg);
    }
}
