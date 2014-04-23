package com.twitter.jdbc.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.twitter.jdbc.db.TwitterConnection;
import com.twitter.jdbc.db.DbUtil;
import com.twitter.jdbc.to.User;
 
public class UserDAO {
    private Connection connection;
    private Statement statement;
 
    public UserDAO() { }
 
    public List<User> getUsers() throws SQLException {
        String query = "SELECT * FROM users";
        List<User> list = new ArrayList<User>();
        User user = null;
        ResultSet rs = null;
        try {
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                user = new User();
                /*Retrieve one user details
                and store it in user object*/
                user.setUserId(rs.getInt("id_user"));
                user.setUserName(rs.getString("name_user"));
                user.setLastTweetId(rs.getInt("id_last_tweet"));
 
                //add each user to the list
                list.add(user);
            }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return list;
    }
    
    public void insertUser() throws SQLException, IOException {
        try {
        	InputStreamReader isr = new InputStreamReader(System.in);
        	BufferedReader br = new BufferedReader (isr);
        	System.out.println("Nombre del nuevo usuario:");
        	String new_user = br.readLine();
            String query = "INSERT INTO Twitter.users (id_user, name_user, id_last_tweet) VALUES (NULL, '"+ new_user +"', NULL);";
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
    }
    
    public void insertUser(String new_user) throws SQLException, IOException {
        try {
            String query = "INSERT INTO Twitter.users (id_user, name_user, id_last_tweet) VALUES (NULL, '"+ new_user +"', NULL);";
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
    }
}