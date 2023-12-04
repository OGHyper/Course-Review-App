package edu.virginia.sde.reviews;

import java.sql.*;
import java.util.*;

public class CourseReview {
    private int id;
    private int courseID;
    private int postingStudentID;
    private int rating;
    private String comment;
    private Timestamp timestamp;

    public CourseReview(int courseID, int postingStudentID, int rating, String comment, Timestamp timestamp) {
        this.courseID = courseID;
        this.postingStudentID = postingStudentID;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public int getCourseID() {
        return courseID;
    }

    public int getPostingStudentID() {return postingStudentID;}

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setCourseID(int courseID) {this.courseID = courseID; }

    public void setPostingStudentID(int postingStudentID) {
        this.postingStudentID = postingStudentID;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }

    public void updateTimestamp(){
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String toString(){
        return "";
    }
}
