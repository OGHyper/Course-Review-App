package edu.virginia.sde.reviews;

import java.sql.*;

public class CourseReview {
    private int id;
    private int studentID;
    private int courseID;
    private String courseSubject;
    private int courseNumber;
    private String courseTitle;
    private String postingStudentName;
    private int rating;
    private String comment;
    private Timestamp timestamp;

    public CourseReview(int studentID, String postingStudentName, int courseID, String courseSubject, int courseNumber, String courseTitle, int rating, String comment, Timestamp timestamp) {
        this.studentID = studentID;
        this.postingStudentName = postingStudentName;
        this.courseID = courseID;
        this.courseSubject = courseSubject;
        this. courseNumber = courseNumber;
        this.courseTitle = courseTitle;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public void setStudentID(int studentID){
        this.studentID = studentID;
    }

    public int getStudentID(){
        return this.studentID;
    }
    public int getCourseID() {
        return courseID;
    }

    public String getPostingStudentName() {return postingStudentName;}

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

    public void setPostingStudentName(String postingStudentName) {
        this.postingStudentName = postingStudentName;
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

    public String getCourseSubject() {
        return courseSubject;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String toString(){
        //TODO: Implement this
        return String.format("%s %d: %s\nRating: %d\nComment: %s\nLast edited: %s", getCourseSubject(),
                getCourseNumber(), getCourseTitle(),
                getRating(), getComment(), getTimestamp().toString());
    }
}
