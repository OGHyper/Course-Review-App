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
            //the next line disables auto-commit - do not delete/comment out
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
                    + " CourseID INTEGER NOT NULL,"
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

    public Optional<Course> getCourseById(int courseID){
        try{
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
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
        String preparedStatement = String.format("SELECT * from Students where Username=%s", username);
        PreparedStatement statement = connection.prepareStatement(preparedStatement);
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
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            String preparedStatement = "SELECT * from Students where username=?";
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            statement.setString(1, student.getUsername());
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                String password = results.getString("Password");
                return Optional.of(password);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<CourseReview> getAllReviews(){
        var reviews = new ArrayList<CourseReview>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * from Reviews");
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int studentID = results.getInt("StudentID");
                int courseid = results.getInt("CourseID");
                String comment = results.getString("Comment");
                int rating = results.getInt("Rating");
                Timestamp timestamp = results.getTimestamp("Timestamp");
                if (results.wasNull()) {
                    comment = null;
                }
                var newReview = new CourseReview(courseid, studentID, rating, comment);
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
        String command = "INSERT INTO Reviews(StudentID, CourseID, Rating, Comment, Timestamp) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.setInt(1, review.getPostingStudentID());
        statement.setInt(2, review.getCourseID());
        statement.setInt(3, review.getRating());
        statement.setTimestamp(5, review.getTimestamp());
        // Set Comment if it's not null, otherwise setNull to indicate a NULL value
        if (review.getComment() != null) {
            statement.setString(4, review.getComment());
        } else {
            statement.setNull(4, Types.VARCHAR);
        }
        statement.executeUpdate();
        statement.close();
    }
}