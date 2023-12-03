package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CourseReviewsApplication extends Application {
    private DatabaseDriver dbDriver;
    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        this.dbDriver = DatabaseDriver.getInstance(new Configuration().getDatabaseFilename());
        dbDriver.connect();
        dbDriver.createTables();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("log-in.fxml"));
        Scene logInScene = new Scene(fxmlLoader.load());
        LogInController logInController = fxmlLoader.getController();
        logInController.setApplication(this);
        logInController.setDatabaseDriver(dbDriver);
        stage.setTitle("Course Review - Log in");
        stage.setScene(logInScene);
        stage.show();
    }
}
