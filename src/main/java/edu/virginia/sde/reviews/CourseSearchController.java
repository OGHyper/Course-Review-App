package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Paint;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;


public class CourseSearchController implements Initializable {
    private final Paint red = Paint.valueOf("b12525");

    private final Paint green = Paint.valueOf("#37e127");
    private Student loggedInStudent;
    private CourseReviewsApplication application;
    private final DatabaseDriver db = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());

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
        return allCoursesSorted;
    }

    public void displayCourse(Course course){
        courseList.getItems().setAll(course);
    }

    public void displayCourses(List<Course> courses){
        courseList.getItems().setAll(courses);
    }

    public void displayAllCourses() throws SQLException {
        var allCourses = getAllCourses();
        courseList.getItems().setAll(allCourses);
    }

    public void searchCourse(ActionEvent event) throws SQLException {
        String subject = courseSubject.getText();
        String number = courseNumber.getText();
        String title = courseTitle.getText();
        if (subject.isEmpty() && title.isEmpty() && number.isEmpty()){
            displayAllCourses();
        }
        if ((!subject.isEmpty() && !title.isEmpty() && !number.isEmpty()) && !db.courseAlreadyExists(subject, Integer.parseInt(number), title)){
            // If all fields are filled and it does not exist
            handleButton("Course does not exist", red);
            return;
        }
        else if (!subject.isEmpty() && !title.isEmpty() && !number.isEmpty() && db.courseAlreadyExists(subject, Integer.parseInt(number), title)){
            // If all the fields match an existing course
            Course foundCourse = new Course(subject, Integer.parseInt(number), title);
            displayCourse(foundCourse);
        }
        else{   // If there is at least one field filled
            // TODO: Implement searching for a course
            if (!subject.isEmpty() && number.isEmpty() && title.isEmpty()){
                displayCourses(db.getCoursesBySubject(subject));
            }
            else if (subject.isEmpty() && !number.isEmpty() && title.isEmpty()){
                displayCourses(db.getCoursesByNumber(Integer.parseInt(number)));
            }
            else if (subject.isEmpty() && number.isEmpty() && !title.isEmpty()){
                displayCourses(db.getCoursesByTitle(title));
            }
            else if(!subject.isEmpty() && !number.isEmpty() && title.isEmpty()){
                List<Course> subjects = db.getCoursesBySubject(subject);
                List<Course> numbers = db.getCoursesByNumber(Integer.parseInt(number));
                ArrayList<Course> common = new ArrayList<>();
                for (Course sub : subjects){    // O(n^2) time lol
                    for (Course num : numbers){
                        if (sub.getSubjectNmeumonic().equals(num.getSubjectNmeumonic()) && sub.getCourseNumber() == num.getCourseNumber()){
                            if (!common.contains(sub)){ // Makes sure duplicates are not added
                                common.add(sub);
                            }
                        }
                    }
                }
                displayCourses(common);
            }
            else if (!subject.isEmpty() && number.isEmpty() && !title.isEmpty()){
                List<Course> subjects = db.getCoursesBySubject(subject);
                List<Course> titles = db.getCoursesByTitle(title);
                ArrayList<Course> common = new ArrayList<>();
                for (Course sub : subjects){
                    for (Course tit : titles){
                        if (sub.getSubjectNmeumonic().equals(tit.getSubjectNmeumonic()) && sub.getCourseTitle().equals(tit.getCourseTitle())){
                            if (!common.contains(sub)){
                                common.add(sub);
                            }
                        }
                    }
                }
                displayCourses(common);
            }
            else if (subject.isEmpty() && !number.isEmpty() && !title.isEmpty()){
                List<Course> numbers = db.getCoursesByNumber(Integer.parseInt(number));
                List<Course> titles = db.getCoursesByTitle(title);
                ArrayList<Course> common = new ArrayList<>();
                for (Course num : numbers){
                    for (Course tit : titles){
                        if (num.getCourseNumber() == tit.getCourseNumber() && num.getCourseTitle().equals(tit.getCourseTitle())){
                            if (!common.contains(num)){
                                common.add(num);
                            }
                        }
                    }
                }
                displayCourses(common);
            }
        }
    }

    public void registerCourse(ActionEvent event) throws SQLException {
        String subject = courseSubject.getText();
        String title = courseTitle.getText();
        int number = 0; // This should be changed under all circumstances
        if (subject.isEmpty() || courseNumber.getText().isEmpty() || title.isEmpty()){
            handleButton("Cannot have empty fields", red);
            return;
        }
        if (isNumeric(courseNumber.getText())){
            number = Integer.parseInt(courseNumber.getText());
        }
        else if (!isNumeric(courseNumber.getText()) && !courseNumber.getText().isEmpty()){
            handleButton("Invalid course number", red);
            return;
        }
        if (subject.length() < 2 || subject.length() > 4){
            handleButton("Subject MUST be 2-4 characters long", red);
        }
        else if (courseNumber.getText().length() != 4){
            handleButton("Course number MUST be 4 digits long", red);
        }
        else if (title.length() > 50){
            handleButton("Title MUST be <= 50 characters long", red);
        }
        else if (db.courseAlreadyExists(subject, number, title)){
            handleButton("Course already exists", red);
        }
        else {
            Course newCourse = new Course(subject, number, title);
            db.addCourse(newCourse);
            courseList.getItems().setAll(getAllCourses());
            handleButton("Course added!", green);
        }
    }

    public void returnToLogin(ActionEvent event) throws Exception {
        // Might switch button to "Sign out"
        application.switchToLogin();
    }

    public void goToMyReviews(ActionEvent event) throws Exception {
        application.goToMyReviews(loggedInStudent);
    }

    public void goToCourseReviews(ActionEvent event){
        // TODO: Implement this
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

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
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
