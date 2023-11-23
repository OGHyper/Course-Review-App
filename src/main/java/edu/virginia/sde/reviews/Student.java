package edu.virginia.sde.reviews;

import java.util.*;

public class Student {  // Basically the user
    // What we need:
    // Username
    private String username;
    // Password
    private String password;
    // List of reviews
    private List<CourseReview> reviews; // This might need more to instantiate

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<CourseReview> getReviews() {
        return reviews;
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
}
