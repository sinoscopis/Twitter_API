package com.twitter.jdbc.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.twitter.jdbc.db.TwitterConnection;
import com.twitter.jdbc.db.DbUtil;
import com.twitter.jdbc.to.Tweet;
 
public class TweetDAO {
    private Connection connection;
    private Statement statement;
 
    public TweetDAO() { }
 
    public List<Tweet> getTweets() throws SQLException {
        String query = "SELECT * FROM tweets";
        List<Tweet> list = new ArrayList<Tweet>();
        Tweet tweet = null;
        ResultSet rs = null;
        try {
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                tweet = new Tweet();
                /*Retrieve one tweet details
                and store it in tweet object*/
                tweet.setTweetId(rs.getInt("id_tweet"));
                tweet.setTweetSenderId(rs.getInt("id_user_sen"));
                tweet.setTweetText(rs.getString("tweet"));
                tweet.setDot(rs.getDate("date"));
                //add each tweet to the list
                list.add(tweet);
            }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return list;
    }
    
    public void insertTweet() throws SQLException, IOException {
    	Scanner scn = new Scanner(System.in);
    	String userid = null, tweet = null;
    	ResultSet rs = null;
        try {
        	System.out.print("Enter user Id: ");
            userid = scn.nextLine();
            
            connection = TwitterConnection.getConnection();
	        statement = connection.createStatement();
	        String check = "SELECT users.id_user FROM users WHERE users.id_user ="+ Integer.parseInt(userid);
          	rs = statement.executeQuery(check);
          	boolean val = rs.next();
          	if (val == false)
           		System.out.print("Imposible insertar, No existe tal usuario");
           	else{
           		System.out.print("Enter Tweet (140 caracteres): "); 
           		tweet = scn.nextLine();
                if (tweet.length() <= 140){
	            	String query = "INSERT INTO Twitter.tweets (id_tweet, id_user_sen, date, tweet) VALUES (NULL, "+ Integer.parseInt(userid) +", NULL, '"+ tweet +"');";	            
	            	statement.executeUpdate(query);
                }
                else
                	System.out.print("Imposible insertar, el tweet es mayor de 140 caracteres");
           	}
        } finally {
        	scn.close();
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
    }
}