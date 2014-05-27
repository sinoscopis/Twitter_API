package twitter.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import twitter.db.DbUtil;
import twitter.db.TwitterConnection;
import twitter.to.Tweet;
 
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
    
    public List<Tweet> getUserTweets(int userid) throws SQLException {
        List<Tweet> list = new ArrayList<Tweet>();
        Tweet tweet = null;
        ResultSet rs = null, rs1 = null;
        try {
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM tweets WHERE id_user_sen="+userid;
            rs1 = statement.executeQuery(query);
            boolean val = rs1.next();
          	if (val == false)
           		System.out.print("El usuario no ha twiteado");
          	else{
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
          	}
        } finally {
            DbUtil.close(rs);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return list;
    }
    
    public List<Tweet> getFriendsTweets(int userid) throws SQLException {
        List<Tweet> list = new ArrayList<Tweet>();
        Tweet tweet = null;
        ResultSet rs = null;
        try {
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM tweets WHERE id_user_sen IN (SELECT id_user_acc FROM friendship WHERE id_user_req = "+userid+")";
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
    
    public String insertTweet(int userid, String tweet) throws SQLException, IOException {
    	ResultSet rs = null;
        try {
            connection = TwitterConnection.getConnection();
	        statement = connection.createStatement();
	        String check = "SELECT users.id_user FROM users WHERE users.id_user ="+ userid;
          	rs = statement.executeQuery(check);
          	boolean val = rs.next();
          	if (val == false)
           		return "Imposible insertar, No existe tal usuario";
           	else{
                if (tweet.length() <= 140){
	            	String query = "INSERT INTO Twitter.tweets (id_tweet, id_user_sen, date, tweet) VALUES (NULL, "+ userid +", NULL, '"+ tweet +"');";	            
	            	statement.executeUpdate(query);
	            	return "Tweet insertado correctamente";
                }
                else
                	return "Imposible insertar, el tweet es mayor de 140 caracteres";
           	}
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
    }

	public String insertReTweet(int userid, int id_tweet) throws SQLException, IOException {
		ResultSet rs = null;
        try {
            connection = TwitterConnection.getConnection();
	        statement = connection.createStatement();
	        String check = "SELECT tweets.id_tweet,tweets.tweet FROM tweets WHERE tweets.id_tweet ="+ id_tweet;
          	rs = statement.executeQuery(check);
          	boolean val = rs.next();
          	if (val == false)
           		return "Imposible retweet, No existe tal tweet";
           	else{
           		String tweet = rs.getString("tweet");
           		String query = "INSERT INTO Twitter.tweets (id_tweet, id_user_sen, date, tweet, retweet) VALUES (NULL, "+ userid +", NULL, '"+ tweet +"','" + id_tweet + "');";	            
            	statement.executeUpdate(query);
            	return "ReTweet - (" + tweet + ") - From - [" + userid + "] ";
           	}
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
	}
	

	public String seeTweet(int id_tweet) throws SQLException {
		String query = "SELECT * FROM tweets WHERE id_tweet = "+id_tweet;
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
            }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return tweet.toString();
	}
	
}