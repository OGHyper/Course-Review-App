package edu.virginia.sde.reviews;

import java.util.List;

public class Course {
    private String subjectNmeumonic;
    private int courseNumber;
    private String courseTitle;
    private List<CourseReview> reviews;

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

    @Override
    public String toString() {
        return String.format("%s %d: %s", subjectNmeumonic, courseNumber, courseTitle);
    }
}
