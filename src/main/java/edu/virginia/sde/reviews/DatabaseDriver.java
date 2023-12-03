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
    public void addCourse(Course course) throws SQLException{
        try {
            if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
            String command = String.format("INSERT INTO Courses(ID,Subject,CourseNumber,Title) VALUES(null,%s,%d,%s)",
                                            course.getSubjectNmeumonic(),
                                            course.getCourseNumber(),
                                            course.getCourseTitle());
            PreparedStatement statement = connection.prepareStatement(command);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    public boolean courseAlreadyExists(String subject, int courseNumber, String title) throws SQLException{
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is not open");
        }
        boolean exists = false;
        String query = String.format("SELECT COUNT(*) FROM Courses WHERE Subject = %s AND CourseNumber = %d AND Title = %s",
                                    subject,
                                    courseNumber,
                                    title);

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = count > 0;
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return exists;
    }

    public List<Course> getAllCourses() throws SQLException {
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        var courses = new ArrayList<Course>();
        try{
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }

    public Optional<Course> getCourseById(int courseID) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            PreparedStatement statement = connection.prepareStatement(String.format("SELECT * from Courses where ID=%s", courseID));
            ResultSet results = statement.executeQuery();

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
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            String preparedStatement = String.format("SELECT * from Courses where Subject=%s AND CourseNumber=%d AND Title=%s", subject, number, title);
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                int id = results.getInt("ID");
                return id;
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new SQLException("No course with subject, number, and title found.");
    }

    public List<Course> getCoursesBySubject(String courseSubject) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        var courses = new ArrayList<Course>();
        try{
            String preparedStatment = String.format("SELECT * from Courses where Subject=%s", courseSubject);
            PreparedStatement statement = connection.prepareStatement(preparedStatment);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int id = results.getInt("ID");
                String subject = results.getString("Subject");
                int courseNumber = results.getInt("CourseNumber");
                String title = results.getString("Title");
                var newCourse = new Course(subject, courseNumber, title);
                courses.add(newCourse);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }

    public List<Course> getCoursesByNumber(int courseNumber) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        var courses = new ArrayList<Course>();
        try{
            String preparedStatement = String.format("SELECT * from Courses where CourseNumber=%d", courseNumber);
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int id = results.getInt("ID");
                String subject = results.getString("Subject");
                int number = results.getInt("CourseNumber");
                String title = results.getString("Title");
                var newCourse = new Course(subject, number, title);
                courses.add(newCourse);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }

    public List<Course> getCoursesByTitle(String subString) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        var courses = new ArrayList<Course>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * from Courses where Title LIKE '%'||?||'%'");
            statement.setString(1, subString);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int id = results.getInt("ID");
                String subject = results.getString("Subject");
                int number = results.getInt("CourseNumber");
                String title = results.getString("Title");
                var newCourse = new Course(subject, number, title);
                courses.add(newCourse);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }

    public void addStudent(Student student) throws SQLException{
        try {
            if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
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

    public List<Student> getAllStudents() throws SQLException {
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
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

    public boolean studentExists(String username)throws SQLException{
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is not open");
        }
        boolean exists = false;
        String query = "SELECT COUNT(*) FROM Students WHERE Username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = count > 0;
                }
            } catch (SQLException e){
                throw new RuntimeException();
            }
        } catch (SQLException e){
            throw new RuntimeException();
        }
        return exists;
    }

    public int getStudentId(String username) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            String preparedStatement = String.format("SELECT * from Students where Username=%s", username);
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                int id = results.getInt("ID");
                return id;
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new SQLException("No student with username: " + username + "found.");
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

    public void addReview(CourseReview review) throws SQLException{
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("Connection is not open");
            }
            String command = "INSERT INTO Reviews(StudentID, CourseID, Rating, Comment, Timestamp) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setInt(1, review.getPostingStudentID());
            statement.setInt(2, review.getCourseID());
            statement.setInt(3, review.getRating());
            statement.setTimestamp(4, review.getTimestamp());
            // Set Comment if it's not null, otherwise setNull to indicate a NULL value
            if (review.getComment() != null) {
                statement.setString(5, review.getComment());
            } else {
                statement.setNull(5, Types.VARCHAR);
            }
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    public void editReview(CourseReview oldReview, CourseReview newReview) throws SQLException{
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is not open");
        }

        try {
            String updateQuery = "UPDATE Reviews SET Rating = ?, Timestamp = ?, Comment = ? WHERE CourseID = ? AND StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setInt(1, newReview.getRating());
            statement.setTimestamp(2, newReview.getTimestamp());
            if (newReview.getComment() != null) {
                statement.setString(3, newReview.getComment());
            } else {
                statement.setNull(3, Types.VARCHAR);
            }
            statement.setInt(4, oldReview.getCourseID());
            statement.setInt(5, oldReview.getPostingStudentID());
            int rowsAffected = statement.executeUpdate();
            statement.close();
            if (rowsAffected == 0) {
                throw new SQLException("No review found for Course ID: " + oldReview.getCourseID() + " and Student ID: " + oldReview.getPostingStudentID());
            }
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    public void removeReview(Course course, Student student) throws SQLException{
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is not open");
        }
        try {
            String deleteQuery = "DELETE FROM Reviews WHERE CourseID = ? AND StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setInt(1, getCourseId(course.getSubjectNmeumonic(), course.getCourseNumber(), course.getCourseTitle()));
            statement.setInt(2, getStudentId(student.getUsername()));
            int rowsAffected = statement.executeUpdate();
            statement.close();
            if (rowsAffected == 0) {
                throw new SQLException("No review found for Course ID: " + course.getId() + " and Student ID: " + student.getId());
            }
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    public List<CourseReview> getAllReviews() throws SQLException {
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        var reviews = new ArrayList<CourseReview>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * from Reviews");
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                int id = results.getInt("ID");
                int studentID = results.getInt("StudentID");
                int courseid = results.getInt("CourseID");
                int rating = results.getInt("Rating");
                String comment = results.getString("Comment");
                Timestamp timestamp = results.getTimestamp("Timestamp");
                if (results.wasNull()) {
                    comment = null;
                }
                var newReview = new CourseReview(id, courseid, studentID, rating, comment);
                newReview.setTimestamp(timestamp);  // Sets the new timestamp to have recorded timestamp
                reviews.add(newReview);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reviews;
    }

    public Optional<CourseReview> getReviewById(int reviewID) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * from Reviews where ID=?");
            statement.setInt(1, reviewID);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                int id = results.getInt("ID");
                int userid = results.getInt("StudentID");
                int courseid = results.getInt("CourseID");
                int rating = results.getInt("Rating");
                String comment = results.getString("Comment");
                Timestamp timestamp = results.getTimestamp("Timestamp");
                if (results.wasNull()) {
                    comment = null;
                }
                CourseReview newCR = new CourseReview(id, courseid, userid, rating, comment);
                newCR.setTimestamp(timestamp);
                return Optional.of(newCR);
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}