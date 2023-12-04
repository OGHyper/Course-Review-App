package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CourseReviewsApplication extends Application {
    private Stage mainStage;
    private DatabaseDriver dbDriver;
    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        this.dbDriver = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());
        dbDriver.connect();
        dbDriver.createTables();

        this.mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("log-in.fxml"));
        Scene logInScene = new Scene(fxmlLoader.load());
        LogInController logInController = fxmlLoader.getController();
        logInController.setApplication(this);
        logInController.setDatabaseDriver(dbDriver);
        stage.setTitle("Course Review - Log in");
        stage.setScene(logInScene);
        stage.show();
    }

    public void switchToCourseSearch(Student loggedInStudent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Scene courseSearchScene = new Scene(fxmlLoader.load());
        CourseSearchController csController = fxmlLoader.getController();
        csController.setApplication(this);
        //csController.setDatabaseDriver(dbDriver);
        csController.setLoggedInStudent(loggedInStudent);
        mainStage.setTitle("Course Review - Course Search");
        mainStage.setScene(courseSearchScene);
        mainStage.show();
    }

    public void switchToLogin() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("log-in.fxml"));
        Scene logInScene = new Scene(fxmlLoader.load());
        LogInController logInController = fxmlLoader.getController();
        logInController.setApplication(this);
        logInController.setDatabaseDriver(dbDriver);
        mainStage.setTitle("Course Review - Log in");
        mainStage.setScene(logInScene);
        mainStage.show();
    }

    public void goToMyReviews(Student student) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-reviews.fxml"));
        Scene myReviewsScene = new Scene(fxmlLoader.load());
        LogInController logInController = fxmlLoader.getController();
        logInController.setApplication(this);
        mainStage.setTitle("Course Review - My Reviews");
        mainStage.setScene(myReviewsScene);
        mainStage.show();
    }
}
