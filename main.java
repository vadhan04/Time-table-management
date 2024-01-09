import java.sql.*;
import java.util.Scanner;

abstract class User {
    abstract void menu();

    void viewTimetable() {
        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String query = "SELECT * FROM Schedule";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("Timetable:");
                while (resultSet.next()) {
                    System.out.println("Day: " + resultSet.getString("DayOfWeek") +
                            ", Time: " + resultSet.getTime("StartTime") +
                            " - " + resultSet.getTime("EndTime") +
                            ", CourseID: " + resultSet.getInt("CourseID") +
                            ", InstructorID: " + resultSet.getInt("InstructorID"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error viewing timetable: " + e.getMessage());
        }
    }
}

class Admin extends User {
    @Override
    void menu() {
        while (true) {
            System.out.println("Admin Functions:");
            System.out.println("1. View Students Timetable");
            System.out.println("2. Edit Students Timetable");
            System.out.println("3. Create students Timetable");
            System.out.println("4. View Faculty Timetable");
            System.out.println("5. Edit Faculty Timetable");
            System.out.println("6. Create faculty Timetable");
            System.out.println("7. Back to the main menu");

            int option = new Scanner(System.in).nextInt();

            switch (option) {
                case 1:
                    viewTimetable();
                    break;
                case 2:
                    editStudentTimetable();
                    break;
                case 3:
                    createTimetable();
                    break;
                case 4:
                    viewFacultyTimetable();
                    break;
                case 5:
                    createFacultyTimetable();
                    break;
                case 6:
                    editFacultyTimetable();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option! Please enter a valid option.");
            }
        }
    }

    private void editStudentTimetable() {
        System.out.println("Enter the ScheduleID of the entry to edit:");
        int scheduleID = new Scanner(System.in).nextInt();

        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM Schedule WHERE ScheduleID = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, scheduleID);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Existing Timetable Entry:");
                        System.out.println("Day: " + resultSet.getString("DayOfWeek") +
                                ", Time: " + resultSet.getTime("StartTime") +
                                " - " + resultSet.getTime("EndTime") +
                                ", CourseID: " + resultSet.getInt("CourseID") +
                                ", InstructorID: " + resultSet.getInt("InstructorID"));

                        System.out.println("Enter new day:");
                        String newDay = new Scanner(System.in).next();
                        System.out.println("Enter new start time (format: HH:mm:ss):");
                        String newStartTime = new Scanner(System.in).next();
                        System.out.println("Enter new end time (format: HH:mm:ss):");
                        String newEndTime = new Scanner(System.in).next();

                        String updateQuery = "UPDATE Schedule SET DayOfWeek = ?, StartTime = ?, EndTime = ? WHERE ScheduleID = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newDay);
                            updateStatement.setString(2, newStartTime);
                            updateStatement.setString(3, newEndTime);
                            updateStatement.setInt(4, scheduleID);

                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Timetable entry updated successfully!");
                            } else {
                                System.out.println("Failed to update timetable entry.");
                            }
                        }
                    } else {
                        System.out.println("Timetable entry not found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error editing timetable entry: " + e.getMessage());
        }
    }

    private void createTimetable() {
        System.out.println("Enter the ScheduleID for the new entry:");
        int scheduleID = new Scanner(System.in).nextInt();

        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM Schedule WHERE ScheduleID = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, scheduleID);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Timetable entry with ScheduleID " + scheduleID + " already exists. Use the Edit Timetable option to modify it.");
                    } else {
                        System.out.println("Enter day:");
                        String day = new Scanner(System.in).next();
                        System.out.println("Enter start time (format: HH:mm:ss):");
                        String startTime = new Scanner(System.in).next();
                        System.out.println("Enter end time (format: HH:mm:ss):");
                        String endTime = new Scanner(System.in).next();
                        System.out.println("Enter course ID:");
                        int courseID = new Scanner(System.in).nextInt();
                        System.out.println("Enter instructor ID:");
                        int instructorID = new Scanner(System.in).nextInt();

                        String insertQuery = "INSERT INTO Schedule (ScheduleID, DayOfWeek, StartTime, EndTime, CourseID, InstructorID) VALUES (?, ?, ?, ?, ?, ?)";

                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                            insertStatement.setInt(1, scheduleID);
                            insertStatement.setString(2, day);
                            insertStatement.setString(3, startTime);
                            insertStatement.setString(4, endTime);
                            insertStatement.setInt(5, courseID);
                            insertStatement.setInt(6, instructorID);

                            int rowsAffected = insertStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Timetable entry created successfully!");
                            } else {
                                System.out.println("Failed to create timetable entry.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating timetable entry: " + e.getMessage());
        }
    }

    private void viewFacultyTimetable() {
        System.out.println("Enter faculty's instructor ID to view timetable:");
        int facultyID = new Scanner(System.in).nextInt();

        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String query = "SELECT * FROM FacultyTimetable WHERE InstructorID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, facultyID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("Faculty Timetable for Instructor " + facultyID + ":");
                    while (resultSet.next()) {
                        System.out.println("Day: " + resultSet.getString("DayOfWeek") +
                                ", Time: " + resultSet.getTime("StartTime") +
                                " - " + resultSet.getTime("EndTime") +
                                ", CourseID: " + resultSet.getInt("CourseID") +
                                ", InstructorID: " + resultSet.getInt("InstructorID"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error viewing faculty timetable: " + e.getMessage());
        }
    }


    private void editFacultyTimetable() {
        System.out.println("Enter the ScheduleID of the entry to edit:");
        int scheduleID = new Scanner(System.in).nextInt();

        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM FacultyTimetable WHERE ScheduleID = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, scheduleID);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Existing Faculty Timetable Entry:");
                        System.out.println("Day: " + resultSet.getString("DayOfWeek") +
                                ", Time: " + resultSet.getTime("StartTime") +
                                " - " + resultSet.getTime("EndTime") +
                                ", CourseID: " + resultSet.getInt("CourseID") +
                                ", InstructorID: " + resultSet.getInt("InstructorID"));

                        System.out.println("Enter new day:");
                        String newDay = new Scanner(System.in).next();
                        System.out.println("Enter new start time (format: HH:mm:ss):");
                        String newStartTime = new Scanner(System.in).next();
                        System.out.println("Enter new end time (format: HH:mm:ss):");
                        String newEndTime = new Scanner(System.in).next();

                        String updateQuery = "UPDATE FacultyTimetable SET DayOfWeek = ?, StartTime = ?, EndTime = ? WHERE ScheduleID = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newDay);
                            updateStatement.setString(2, newStartTime);
                            updateStatement.setString(3, newEndTime);
                            updateStatement.setInt(4, scheduleID);

                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Faculty timetable entry updated successfully!");
                            } else {
                                System.out.println("Failed to update faculty timetable entry.");
                            }
                        }
                    } else {
                        System.out.println("Faculty timetable entry not found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error editing faculty timetable entry: " + e.getMessage());
        }
    }
    private void createFacultyTimetable() {
        System.out.println("Enter the ScheduleID for the new faculty timetable entry:");
        int scheduleID = new Scanner(System.in).nextInt();

        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM FacultyTimetable WHERE ScheduleID = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, scheduleID);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Faculty timetable entry with ScheduleID " + scheduleID + " already exists. Use the Edit Faculty Timetable option to modify it.");
                    } else {

                        System.out.println("Enter day:");
                        String day = new Scanner(System.in).next();
                        System.out.println("Enter start time (format: HH:mm:ss):");
                        String startTime = new Scanner(System.in).next();
                        System.out.println("Enter end time (format: HH:mm:ss):");
                        String endTime = new Scanner(System.in).next();
                        System.out.println("Enter course ID:");
                        int courseID = new Scanner(System.in).nextInt();
                        System.out.println("Enter instructor ID:");
                        int instructorID = new Scanner(System.in).nextInt();


                        String insertQuery = "INSERT INTO FacultyTimetable (ScheduleID, DayOfWeek, StartTime, EndTime, CourseID, InstructorID) VALUES (?, ?, ?, ?, ?, ?)";

                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                            insertStatement.setInt(1, scheduleID);
                            insertStatement.setString(2, day);
                            insertStatement.setString(3, startTime);
                            insertStatement.setString(4, endTime);
                            insertStatement.setInt(5, courseID);
                            insertStatement.setInt(6, instructorID);

                            int rowsAffected = insertStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Faculty timetable entry created successfully!");
                            } else {
                                System.out.println("Failed to create faculty timetable entry.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating faculty timetable entry: " + e.getMessage());
        }
    }


}




class Faculty extends User {
    @Override
    void menu() {
        while (true) {
            System.out.println("Faculty Functions:");
            System.out.println("1. View Timetable");
            System.out.println("2. Back to the main menu");

            int option = new Scanner(System.in).nextInt();

            switch (option) {
                case 1:
                    viewTimetable();
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid option! Please enter a valid option.");
            }
        }
    }

    public void viewTimetable() {
        System.out.println("Enter your instructor ID:");
        int instructorID = new Scanner(System.in).nextInt();

        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String query = "SELECT * FROM FacultyTimetable WHERE InstructorID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, instructorID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("Timetable for Instructor " + instructorID + ":");
                    while (resultSet.next()) {
                        System.out.println("Day: " + resultSet.getString("DayOfWeek") +
                                ", Time: " + resultSet.getTime("StartTime") +
                                " - " + resultSet.getTime("EndTime") +
                                ", CourseID: " + resultSet.getInt("CourseID") +
                                ", InstructorID: " + resultSet.getInt("InstructorID"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error viewing faculty timetable: " + e.getMessage());
        }
    }

}




class Student extends User {
    @Override
    void menu() {
        while (true) {
            System.out.println("Student Functions:");
            System.out.println("1. View Timetable");
            System.out.println("2. Back to the main menu");

            int option = new Scanner(System.in).nextInt();

            switch (option) {
                case 1:
                    viewTimetable();
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid option! Please enter a valid option.");
            }
        }
    }
}

class Course {
    static void viewCourses() {
        try (Connection connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD)) {
            String query = "SELECT * FROM Courses";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("Courses:");
                while (resultSet.next()) {
                    System.out.println("Course ID: " + resultSet.getInt("CourseID") +
                            ", Course Name: " + resultSet.getString("CourseName") +
                            ", Course Code: " + resultSet.getString("CourseCode"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error viewing courses: " + e.getMessage());
        }
    }
}

public class Main {
    static final String DB_URL = "jdbc:mysql://localhost:3306/oops";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "//Your password here";

    public static void main(String[] args) {
        System.out.println("Welcome to the Timetable Management System!");




        while (true) {
            System.out.println("Select user type (admin/faculty/student/exit):");
            String userType = new Scanner(System.in).nextLine().toLowerCase();

            if (userType.equals("exit")) {
                System.out.println("Exiting the Timetable Management System. Goodbye!");
                break;
            }

            User user;
            switch (userType) {
                case "admin":
                    user = new Admin();
                    break;
                case "faculty":
                    user = new Faculty();
                    break;
                case "student":
                    user = new Student();
                    break;
                default:
                    System.out.println("Invalid user type! Please enter a valid user type.");
                    continue;
            }

            if (login()) {

                Thread userThread = new Thread(() -> {
                    user.menu();
                });
                userThread.start();

                try {
                    userThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Login failed. Please try again.");
            }
        }
    }

    private static boolean login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to login or sigun up? (yes to login /no to siginup):");
        String loginChoice = scanner.nextLine().toLowerCase();

        if (loginChoice.equals("yes")) {
            System.out.println("Enter your username:");
            String username = scanner.nextLine();

            System.out.println("Enter your password:");
            String password = scanner.nextLine();

            System.out.println("Enter your user type (admin/faculty/student):");
            String userType = scanner.nextLine().toLowerCase();

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT * FROM Users WHERE Username = ? AND Password = ? AND UserType = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, username);
                    statement.setString(2, password);
                    statement.setString(3, userType);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        return resultSet.next();
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error during login: " + e.getMessage());
            }
        } else if (loginChoice.equals("no")) {
            signUp();
        } else {
            System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
        }

        return false;
    }

    private static void signUp() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a new username:");
        String newUsername = scanner.nextLine();

        System.out.println("Enter a new password:");
        String newPassword = scanner.nextLine();

        System.out.println("Enter user type (admin/faculty/student):");
        String userType = scanner.nextLine().toLowerCase();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO Users (Username, Password, UserType) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newUsername);
                statement.setString(2, newPassword);
                statement.setString(3, userType);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Signup successful! You can now log in.");
                } else {
                    System.out.println("Failed to sign up. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during signup: " + e.getMessage());
        }
    }


}