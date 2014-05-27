package twitter.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class TwitterConnection {
    //static reference to itself
    private static TwitterConnection instance = new TwitterConnection();
    public static final String URL = "jdbc:mysql://192.168.10.37:3306/Twitter";
    public static final String USER = "alberto";
    public static final String PASSWORD = "alberto";
    //public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
 
    //private constructor
    private TwitterConnection() {
        try {
            //Class.forName(DRIVER_CLASS);
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
 
    public Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }   
 
    public static Connection getConnection() {
        return instance.createConnection();
    }
}