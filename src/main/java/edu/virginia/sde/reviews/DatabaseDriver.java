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
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students ("
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
            statement.executeUpdate(createStudentsTable);
            statement.executeUpdate(createReviewsTable);
            statement.close();
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
        }catch (SQLException e) {
            throw new RuntimeException(e);
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
                int id = results.getInt("ID");
                String subject = results.getString("Subject");
                int courseNumber = results.getInt("CourseNumber");
                String title = results.getString("Title");
                Course newCourse = new Course(id, subject, courseNumber, title);
                courses.add(newCourse);
            }
            statement.close();
        }catch (SQLException e) {
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
                int id = results.getInt("ID");
                String subject = results.getString("Subject");
                int courseNumber = results.getInt("CourseNumber");
                String title = results.getString("Title");
                return Optional.of(new Course(id, subject, courseNumber, title));
            }
            statement.close();
        }catch (SQLException e) {
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
        }catch (SQLException e) {
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
                var newCourse = new Course(id, subject, courseNumber, title);
                courses.add(newCourse);
            }
            statement.close();
        }catch (SQLException e) {
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
                var newCourse = new Course(id, subject, number, title);
                courses.add(newCourse);
            }
            statement.close();
        }catch (SQLException e) {
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
                var newCourse = new Course(id, subject, number, title);
                courses.add(newCourse);
            }
            statement.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }

    public void addStudent(Student student) throws SQLException{
        try {
            if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
            String command = String.format("INSERT INTO Students(ID,Username,Password) VALUES(null,%s,%s)", student.getUsername(), student.getPassword());
            PreparedStatement statement = connection.prepareStatement(command);
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
                var newStudent = new Student(id, username, password);
                students.add(newStudent);
            }
            statement.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
    }

    public boolean studentExists(String username)throws SQLException{
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is not open");
        }
        boolean exists = false;
        String query = String.format("SELECT COUNT(*) FROM Students WHERE Username = %s", username);

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = count > 0;
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
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
        }catch (SQLException e) {
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
                return Optional.of(new Student(id, username, password));
            }
            statement.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Student> getStudentByUsername(String username) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            String preparedStatement = String.format("SELECT * from Students where username=%s", username);
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                int id = results.getInt("ID");
                String username2 = results.getString("Username");
                String password = results.getString("Password");
                return Optional.of(new Student(id, username2, password));
            }
            statement.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<String> getPasswordForStudent(Student student) throws SQLException{
        if (connection.isClosed()) throw new IllegalStateException("Connection is not open");
        try{
            String preparedStatement = String.format("SELECT * from Users where username=%s", student.getUsername());
            PreparedStatement statement = connection.prepareStatement(preparedStatement);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                String password = results.getString("Password");
                return Optional.of(password);
            }
            statement.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


}
