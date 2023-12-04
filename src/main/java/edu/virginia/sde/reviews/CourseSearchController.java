package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class CourseSearchController implements Initializable {
    private final Paint red = Paint.valueOf("b12525");

    private final Paint green = Paint.valueOf("#37e127");
    private Student loggedInStudent;
    private CourseReviewsApplication application;
    private final DatabaseDriver db = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());;

    @FXML
    private TextField courseSubject;
    @FXML
    private TextField courseNumber;
    @FXML
    private TextField courseTitle;
    @FXML
    private Label buttonMessage;
    @FXML
    private ListView<Course> courseList;

    public List<Course> getAllCourses() throws SQLException {
        List<Course> allCourses = db.getAllCourses();
        List<Course> allCoursesSorted = allCourses.stream()
                .sorted(Comparator.comparing(Course::getSubjectNmeumonic))
                .toList();
        // TODO: Try to get this to be sorted by course number too
        return allCoursesSorted;
    }
    public void searchCourse(ActionEvent event) throws SQLException {
        String subject = courseSubject.getText();
        int number = Integer.parseInt(courseNumber.getText());
        String title = courseTitle.getText();
        if (!db.courseAlreadyExists(subject, number, title)){
            handleButton("Course does not exist", red);
        }
        else{
            // TODO: Searching for the courses
            db.getCoursesBySubject(subject);
            db.getCoursesByNumber(number);
            db.getCoursesByTitle(title);
        }

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
            courseList.getItems().setAll(getAllCourses());
            handleButton("Course added", green);
        }
    }

    public void returnToLogin(ActionEvent event) throws Exception {
        // Might switch button to "Sign out"
        application.switchToLogin();
    }

    public void goToMyReviews(ActionEvent event) throws Exception {
        application.goToMyReviews();
    }

    public void setApplication(CourseReviewsApplication application){
        this.application = application;
    }

    public void setDatabaseDriver(DatabaseDriver dbDriver){
        //this.db = dbDriver;
    }

    public void setLoggedInStudent(Student loggedInStudent) {
        this.loggedInStudent = loggedInStudent;
    }

    public void handleButton(String message, Paint color) {
        buttonMessage.setTextFill(color);
        buttonMessage.setText(message);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            courseList.getItems().addAll(getAllCourses());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
