package edu.virginia.sde.reviews;
import java.sql.*;
import java.util.*;

public class DatabaseDriver {

    private final String sqliteFilename;
    private Connection connection;

    public DatabaseDriver(String sqlListDatabaseFilename) {
        this.sqliteFilename = sqlListDatabaseFilename;
    }

    /**
     * Connect to a SQLite Database. This turns out Foreign Key enforcement, and disables auto-commits
     *
     * @throws SQLException
     */
    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFilename);
        //the next line enables foreign key enforcement - do not delete/comment out
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        //the next line disables auto-commit - do not delete/comment out
        connection.setAutoCommit(false);
    }

    /**
     * Commit all changes since the connection was opened OR since the last commit/rollback
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Rollback to the last commit, or when the connection was opened
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Ends the connection to the database
     */
    public void disconnect() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");

        String createCoursesTable = "CREATE TABLE IF NOT EXISTS Courses ("
                + "	ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "	Subject VARCHAR(255) NOT NULL,"
                + " CourseNumber INTEGER NOT NULL,"
                + "	Title VARCHAR(255) NOT NULL"
                + ");";
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "	ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "	Username VARCHAR(255) NOT NULL UNIQUE,"
                + " Password VARCHAR(255) NOT NULL"
                + ");";
        String createReviewsTable = "CREATE TABLE IF NOT EXISTS Reviews ("
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " UserID INTEGER NOT NULL,"
                + " CourseID INTEGER NOT NULL,"
                + " Rating INTEGER NOT NULL,"
                + " EntryTime TIMESTAMP NOT NULL,"
                + "	Comment VARCHAR(255),"
                + " FOREIGN KEY (UserID) REFERENCES Users(ID) ON DELETE CASCADE,"
                + " FOREIGN KEY (CourseID) REFERENCES Courses(ID) ON DELETE CASCADE"
                + " UNIQUE (UserID, CourseID)"
                + ");";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(createCoursesTable);
            statement.executeUpdate(createUsersTable);
            statement.executeUpdate(createReviewsTable);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Insert Database functions here
}
