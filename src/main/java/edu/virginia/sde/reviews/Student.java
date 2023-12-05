package edu.virginia.sde.reviews;

import java.util.*;

public class Student {  // Basically the user
    // id
    private int id;
    // Username
    private String username;
    // Password
    private String password;
    // List of reviews
    private List<CourseReview> reviews; // This might need more to instantiate

    public Student(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<CourseReview> getReviews() {
        return reviews;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setReviews(List<CourseReview> reviews) {
        this.reviews = reviews;
    }

    public void addReview(CourseReview review){
        this.reviews.add(review);
    }

    public String toString(){
        return getUsername();
    }
}
