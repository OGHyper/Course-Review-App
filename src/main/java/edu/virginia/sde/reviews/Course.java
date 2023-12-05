package edu.virginia.sde.reviews;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Course {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private int id;
    private String subjectNmeumonic;
    private int courseNumber;
    private String courseTitle;
    private Double avgRating;


    public Course(String subjectNmeumonic, int courseNumber, String courseTitle) {
        this.subjectNmeumonic = subjectNmeumonic;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectNmeumonic() {
        return subjectNmeumonic;
    }

    public void setSubjectNmeumonic(String subjectNmeumonic) {
        this.subjectNmeumonic = subjectNmeumonic;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setAvgRating(Double avgRating){
        this.avgRating = avgRating;
    }

    public Double getAvgRating(){
        return  this.avgRating;
    }

    @Override
    public String toString() {
        double avgRating = getAvgRating();
        String formattedRatingAvg = df.format(avgRating);
        if (formattedRatingAvg.equals("NaN")){
            formattedRatingAvg = "";
        }
        return String.format("%s %d: %s \nAvg Rating: %s", subjectNmeumonic.toUpperCase(), courseNumber, courseTitle, formattedRatingAvg);
    }
}
