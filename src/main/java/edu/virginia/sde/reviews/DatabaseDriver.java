package edu.virginia.sde.reviews;

import java.sql.*;
import java.util.*;

public class DatabaseDriver {

    private static DatabaseDriver instance;
    private final String sqliteFilename;
    private Connection connection;

    public DatabaseDriver(String sqlListDatabaseFilename) {
        this.sqliteFilename = sqlListDatabaseFilename;
    }

    public static DatabaseDriver getInstance(String sqliteFilename){
        if (instance == null){
            instance = new DatabaseDriver(sqliteFilename);
        }
        return instance;
    }
    /**
     * Connect to a SQLite Database. This turns out Foreign Key enforcement, and disables auto-commits
     *
     *
     */
    public void connect(){
        try {
            if (connection != null && !connection.isClosed()) {
                throw new IllegalStateException("The connection is already opened");
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFilename);
            //the next line enables foreign key enforcement - do not delete/comment out
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
        //System.out.println("Entered createTables");
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try (Statement statement = connection.createStatement()) {
            String createCoursesTable = "CREATE TABLE IF NOT EXISTS Courses ("
                    + "	ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "	Subject VARCHAR(255) NOT NULL,"
                    + " CourseNumber INTEGER NOT NULL,"
                    + "	Title VARCHAR(255) NOT NULL);";
            statement.executeUpdate(createCoursesTable);
            String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students ("
                    + "	ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "	Username VARCHAR(255) NOT NULL UNIQUE,"
                    + " Password VARCHAR(255) NOT NULL);";
            statement.executeUpdate(createStudentsTable);
            String createReviewsTable = "CREATE TABLE IF NOT EXISTS Reviews ("
                    + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " StudentID INTEGER NOT NULL,"
                    + " StudentName VARCHAR (255) NOT NULL,"
                    + " CourseID INTEGER NOT NULL,"
                    + " CourseSubject VARCHAR(255) NOT NULL,"
                    + " CourseNumber INTEGER NOT NULL,"
                    + " CourseTitle VARCHAR (255) NOT NULL,"
                    + " Rating INTEGER NOT NULL,"
                    + "	Comment VARCHAR(255),"
                    + " Timestamp TIMESTAMP NOT NULL,"
                    + " FOREIGN KEY (StudentID) REFERENCES Students(ID) ON DELETE CASCADE,"
                    + " FOREIGN KEY (CourseID) REFERENCES Courses(ID) ON DELETE CASCADE);";
            statement.executeUpdate(createReviewsTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Insert Database functions here
    public void addCourse(Course course) throws SQLException {
        String command = "INSERT INTO Courses(ID,Subject,CourseNumber,Title) VALUES(null,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.setString(1, course.getSubjectNmeumonic());
        statement.setInt(2, course.getCourseNumber());
        statement.setString(3, course.getCourseTitle());
        statement.executeUpdate();
        statement.close();
    }

    public boolean courseAlreadyExists(String subject, int courseNumber, String title) throws SQLException{
        boolean doesCourseExist = false;
        String query = "SELECT COUNT(*) FROM Courses WHERE Subject = ? AND CourseNumber = ? AND Title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, subject);
            statement.setInt(2, courseNumber);
            statement.setString(3, title);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    doesCourseExist = count > 0;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return doesCourseExist;
    }

    public List<Course> getAllCourses() throws SQLException {
        var courses = new ArrayList<Course>();
        PreparedStatement statement = connection.prepareStatement("SELECT * from Courses");
        ResultSet results = statement.executeQuery();
        while (results.next()) {
            String subject = results.getString("Subject");
            int courseNumber = results.getInt("CourseNumber");
            String title = results.getString("Title");
            Course newCourse = new Course(subject, courseNumber, title);
            courses.add(newCourse);
        }
        statement.close();
        return courses;
    }

    public Optional<Course> getCourseById(int courseID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * from Courses where ID=%s");
        ResultSet results = statement.executeQuery();
        statement.setInt(1, courseID);
        if (results.next()) {
            String subject = results.getString("Subject");
            int courseNumber = results.getInt("CourseNumber");
            String title = results.getString("Title");
            return Optional.of(new Course(subject, courseNumber, title));
        }
        statement.close();
        return Optional.empty();    // Should never reach this
    }

    public int getCourseId(String subject, int number, String title) throws SQLException{
        String preparedStatement = "SELECT * from Courses where Subject=? AND CourseNumber=? AND Title=?";
        PreparedStatement statement = connection.prepareStatement(preparedStatement);
        statement.setString(1, subject);
        statement.setInt(2, number);
        statement.setString(3, title);
        ResultSet results = statement.executeQuery();

        if (results.next()) {
            int id = results.getInt("ID");
            return id;
        }
        statement.close();
        return -1;  // Should never reach this. I hope lol
    }

    public List<Course> getCoursesBySubject(String courseSubject) throws SQLException {
        var courses = new ArrayList<Course>();
        String preparedStatement = "SELECT * from Courses where Subject=?";
        PreparedStatement statement = connection.prepareStatement(preparedStatement);
        statement.setString(1, courseSubject);
        ResultSet results = statement.executeQuery();
        while (results.next()) {
            String subject = results.getString("Subject");
            int courseNumber = results.getInt("CourseNumber");
            String title = results.getString("Title");
            Course newCourse = new Course(subject, courseNumber, title);
            courses.add(newCourse);
        }
        statement.close();
        return courses;
    }

    public List<Course> getCoursesByNumber(int courseNumber) throws SQLException {
        var courses = new ArrayList<Course>();
        String preparedStatement = "SELECT * from Courses where CourseNumber=?";
        PreparedStatement statement = connection.prepareStatement(preparedStatement);
        statement.setInt(1, courseNumber);
        ResultSet results = statement.executeQuery();
        while (results.next()) {
            String subject = results.getString("Subject");
            int number = results.getInt("CourseNumber");
            String title = results.getString("Title");
            Course newCourse = new Course(subject, number, title);
            courses.add(newCourse);
        }
        statement.close();
        return courses;
    }

    public List<Course> getCoursesByTitle(String subString) throws SQLException {
        var courses = new ArrayList<Course>();
        PreparedStatement statement = connection.prepareStatement("SELECT * from Courses where Title LIKE '%'||?||'%'");
        statement.setString(1, subString);
        ResultSet results = statement.executeQuery();
        while (results.next()) {
            String subject = results.getString("Subject");
            int number = results.getInt("CourseNumber");
            String title = results.getString("Title");
            Course newCourse = new Course(subject, number, title);
            courses.add(newCourse);
        }
        statement.close();
        return courses;
    }

    public void addStudent(Student student) throws SQLException{
        try {
            String command = "INSERT INTO Students (Username, Password) VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setString(1, student.getUsername());
            statement.setString(2, student.getPassword());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    public List<Student> getAllStudents(){
        var students = new ArrayList<Student>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * from Students");
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int id = results.getInt("ID");
                String username = results.getString("Username");
                String password = results.getString("Password");
                var newStudent = new Student(username, password);
                students.add(newStudent);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
    }

    public boolean studentExists(String username){
        boolean exists = false;
        String query = "SELECT COUNT(*) FROM Students WHERE Username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = count > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exists;
    }

    public int getStudentId(String username) throws SQLException{
        String preparedStatement = "SELECT * from Students where Username=?";
        PreparedStatement statement = connection.prepareStatement(preparedStatement);
        statement.setString(1, username);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            int id = results.getInt("ID");
            return id;
        }
        statement.close();
        return -1;  // Should never reach this. I hope
    }

    public Optional<Student> getStudentById(int studentID) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            String preparedStatement = String.format("SELECT * from Students where ID=%d", studentID);
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                int id = results.getInt("ID");
                String username = results.getString("Username");
                String password = results.getString("Password");
                return Optional.of(new Student(username, password));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Student> getStudentByUsername(String username) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            String preparedStatement = "SELECT * from Students where username=%s";
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                int id = results.getInt("ID");
                String username2 = results.getString("Username");
                String password = results.getString("Password");
                return Optional.of(new Student(username2, password));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<String> getPasswordForStudent(Student student) throws SQLException{
        String preparedStatement = "SELECT * from Students where Username=?";
        PreparedStatement statement = connection.prepareStatement(preparedStatement);
        statement.setString(1, student.getUsername());
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            String password = results.getString("Password");
            return Optional.of(password);
        }
        statement.close();
        return Optional.empty();
    }

    public List<CourseReview> getAllReviews(){
        var reviews = new ArrayList<CourseReview>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * from Reviews");
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                String studentName = results.getString("StudentName");
                int studentID = getStudentId(studentName);
                String courseSubject = results.getString("CourseSubject");
                int courseNumber = results.getInt("CourseNumber");
                String courseTitle = results.getString("CourseTitle");
                int courseID = getCourseId(courseSubject, courseNumber, courseTitle);
                String comment = results.getString("Comment");
                int rating = results.getInt("Rating");
                Timestamp timestamp = results.getTimestamp("Timestamp");
                if (results.wasNull()) {
                    comment = null;
                }
                var newReview = new CourseReview(studentID, studentName, courseID, courseSubject, courseNumber, courseTitle, rating, comment, timestamp);
                newReview.setTimestamp(timestamp);  // Sets the new timestamp to have recorded timestamp
                reviews.add(newReview);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reviews;
    }

    public void addReview(CourseReview review) throws SQLException {
        String command = "INSERT INTO Reviews(StudentID, StudentName, CourseID, CourseSubject, CourseNumber, CourseTitle, Rating, Comment, Timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(command);
        //System.out.println(review.getStudentID());
        statement.setInt(1, getStudentId(review.getPostingStudentName()));
        statement.setString(2, review.getPostingStudentName());
        statement.setInt(3, getCourseId(review.getCourseSubject(), review.getCourseNumber(), review.getCourseTitle()));
        statement.setString(4, review.getCourseSubject());
        statement.setInt(5, review.getCourseNumber());
        statement.setString(6, review.getCourseTitle());
        statement.setInt(7, review.getRating());
        if (review.getComment() != null) {
            statement.setString(8, review.getComment());
        } else {
            statement.setNull(8, Types.VARCHAR);
        }
        statement.setTimestamp(9, review.getTimestamp());
        statement.executeUpdate();
        statement.close();
    }

    public List<CourseReview> getReviewsFromStudent(Student student) throws SQLException {
        var reviews = new ArrayList<CourseReview>();
        String command = "SELECT * FROM Reviews WHERE StudentID = ?";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.setInt(1, getStudentId(student.getUsername()));
        ResultSet results = statement.executeQuery();
        while(results.next()){
            String courseSubject = results.getString("CourseSubject");
            int courseNumber = results.getInt("CourseNumber");
            String courseTitle = results.getString("CourseTitle");
            int courseID = getCourseId(courseSubject, courseNumber, courseTitle);
            String studentName = results.getString("StudentName");
            int studentID = getStudentId(studentName);
            int rating = results.getInt("Rating");
            String comment = results.getString("Comment");
            Timestamp timestamp = results.getTimestamp("Timestamp");
            CourseReview review = new CourseReview(studentID, studentName, courseID, courseSubject, courseNumber, courseTitle, rating, comment, timestamp);
            reviews.add(review);
        }
        statement.close();
        return reviews;
    }

    public List<CourseReview> getReviewsFromCourse(Course course) throws SQLException {
        var reviews = new ArrayList<CourseReview>();
        String command = "SELECT * FROM Reviews WHERE CourseID = ?";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.setInt(1, getCourseId(course.getSubjectNmeumonic(), course.getCourseNumber(), course.getCourseTitle()));
        ResultSet results = statement.executeQuery();
        while (results.next()){
            String courseSubject = results.getString("CourseSubject");
            int courseNumber = results.getInt("CourseNumber");
            String courseTitle = results.getString("CourseTitle");
            int courseID = getCourseId(courseSubject, courseNumber, courseTitle);
            String studentName = results.getString("StudentName");
            int studentID = getStudentId(studentName);
            int rating = results.getInt("Rating");
            String comment = results.getString("Comment");
            Timestamp timestamp = results.getTimestamp("Timestamp");
            CourseReview review = new CourseReview(studentID, studentName, courseID, courseSubject, courseNumber, courseTitle, rating, comment, timestamp);
            reviews.add(review);
        }
        statement.close();
        return reviews;
    }

    public CourseReview getReviewOfCourseFromStudent(Course course, Student student) throws SQLException {
        CourseReview review = null;
        String query = "SELECT * FROM Reviews WHERE StudentID = ? AND CourseID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, getStudentId(student.getUsername()));
        statement.setInt(2, getCourseId(course.getSubjectNmeumonic(),course.getCourseNumber(),course.getCourseTitle()));
        ResultSet results = statement.executeQuery();
        while (results.next()) {
            String courseSubject = results.getString("CourseSubject");
            int courseNumber = results.getInt("CourseNumber");
            String courseTitle = results.getString("CourseTitle");
            int courseID = getCourseId(courseSubject, courseNumber, courseTitle);
            String studentName = results.getString("StudentName");
            int studentID = getStudentId(studentName);
            int rating = results.getInt("Rating");
            String comment = results.getString("Comment");
            Timestamp timestamp = results.getTimestamp("Timestamp");
            if (results.wasNull()) {
                comment = null;
            }
            review = new CourseReview(studentID, studentName, courseID, courseSubject, courseNumber, courseTitle, rating, comment, timestamp);
        }
        statement.close();
        return review;
    }



    public void updateReview (CourseReview newReview) throws SQLException {
        String updateQuery = "UPDATE Reviews SET Rating = ?, Comment = ?, Timestamp = ? WHERE CourseID = ? AND StudentID = ?";
        PreparedStatement statement = connection.prepareStatement(updateQuery);
        statement.setInt(1, newReview.getRating());
        if (newReview.getComment() != null) {
            statement.setString(2, newReview.getComment());
        } else {
            statement.setNull(2, Types.VARCHAR);
        }
        statement.setTimestamp(3, newReview.getTimestamp());
        // New review should be updating the old review, so courseID and PostingStudentName should be the same
        statement.setInt(4, getCourseId(newReview.getCourseSubject(), newReview.getCourseNumber(), newReview.getCourseTitle()));
        statement.setInt(5, getStudentId(newReview.getPostingStudentName()));
        statement.executeUpdate();
        statement.close();
    }

    public void deleteStudentReview(Course course, Student student) throws SQLException {
        String command = "DELETE FROM Reviews WHERE CourseID = ? AND StudentID = ?";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.setInt(1, getCourseId(course.getSubjectNmeumonic(), course.getCourseNumber(), course.getCourseTitle()));
        statement.setInt(2, getStudentId(student.getUsername()));
        statement.executeUpdate();
        statement.close();
    }
}