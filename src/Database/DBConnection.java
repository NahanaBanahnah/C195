package Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/** Main Database Connection */
public abstract class DBConnection {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";

    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone=SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference

    private static final String userName = "sqlUser"; // Username
    final private static String password = "Passw0rd!"; // Password
    public static Connection connection = null;  // Connection Interface

    public static Connection startConnection() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e) {
            //System.out.println("Error:" + e.getMessage());
        }
    }

}
