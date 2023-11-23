package edu.virginia.sde.reviews;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class CourseReview {
    private Course course;
    private Student postingStudent;
    private double rating;
    private String comment;
    private Timestamp timestamp;

    public Course getCourse() {
        return course;
    }

    public Student getPostingStudent() {
        return postingStudent;
    }

    public double getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setPostingStudent(Student postingStudent) {
        this.postingStudent = postingStudent;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTimestamp(Date date){
        this.timestamp = new Timestamp(date.getTime());
    }
}
