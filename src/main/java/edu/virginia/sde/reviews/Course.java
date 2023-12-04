package edu.virginia.sde.reviews;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Course {
    private int id;
    private String subjectNmeumonic;
    private int courseNumber;
    private String courseTitle;
    private List<CourseReview> reviews = new ArrayList<>();


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

    public List<CourseReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<CourseReview> reviews) {
        this.reviews = reviews;
    }

    public void addReview(CourseReview review){
        reviews.add(review);
    }

    public double getAvgRating(){
        if (this.reviews.isEmpty()){
            return 0.0;
        }
        else{
            double total = 0.00;
            for (CourseReview cr : this.reviews){
                total += cr.getRating();
            }
            double avg = total / this.reviews.size();
            avg = Math.round(avg * 100.0) / 100.0;
            return avg;
        }
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        double avgRating = getAvgRating();
        return String.format("%s %d: %s \nAvg Rating: %s", subjectNmeumonic, courseNumber, courseTitle, df.format(avgRating));
    }
}
