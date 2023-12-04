package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.*;
import java.util.*;

public class MyReviewsController implements Initializable {
    private Student loggedInStudent;
    private final DatabaseDriver db = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());
    @FXML
    private ListView<CourseReview> myReviews;

    public void setLoggedInStudent(Student student){
        this.loggedInStudent = student;
    }

    public List<CourseReview> getMyReviews(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            myReviews.getItems().addAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
