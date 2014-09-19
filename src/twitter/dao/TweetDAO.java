package twitter.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import twitter.db.DbUtil;
import twitter.db.TwitterConnection;
import twitter.to.Tweet;
import twitter.to.User;
 
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
            DbUtil.close(rs1);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return list;
    }
    
    public List<Tweet> getFriendsTweets(int userid) throws SQLException, InterruptedException {
        List<Tweet> list = new ArrayList<Tweet>();
        Tweet tweet = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        int amigos = 0;
        try {
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            String querystop = "SELECT sum(friends) as amigos FROM `followersByCluster` WHERE `user_id`="+userid+";";
            rs = statement.executeQuery(querystop);
            
            while (rs.next()) {
                amigos = rs.getInt("amigos");
            }
            if (amigos < 30)
            	TimeUnit.MILLISECONDS.sleep(30000);
            //Selecciona todos los tweets de los amigos y sean "nuevos" (no consumidos antes)
            String query = "SELECT * FROM tweets WHERE id_user_sen IN (SELECT id_user_req FROM friendship WHERE id_user_acc = "+userid+") AND id_tweet > (select last_tweet from users where id_user = "+userid+")"; // ORDER BY date desc LIMIT 500";
            rs2 = statement.executeQuery(query);
            while (rs2.next()) {
                tweet = new Tweet();
                /*Retrieve one tweet details
                and store it in tweet object*/
                tweet.setTweetId(rs2.getInt("id_tweet"));
                tweet.setTweetSenderId(rs2.getInt("id_user_sen"));
                tweet.setTweetText(rs2.getString("tweet"));
                tweet.setDot(rs2.getDate("date"));
                //add each tweet to the list
                list.add(tweet);
            }
          	
        } finally {
            DbUtil.close(rs);
            DbUtil.close(rs2);
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
        	DbUtil.close(rs);
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
        	DbUtil.close(rs);
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
        	DbUtil.close(rs);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
	}
	
	public String seeTweet(int id_tweet, int user_id_pide) throws SQLException {
		String query = "SELECT * FROM tweets WHERE id_tweet = "+id_tweet;
        Tweet tweet = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        int cache=0;
        int user_creador=0;
        int num_follow_cache=0;
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
            user_creador=tweet.getTweetSenderId();
            String a = tweet.getTweetText();
            if(a.startsWith("http")){
            	String query1 = "UPDATE users SET last_tweet="+tweet.getTweetId()+" WHERE id_user="+user_id_pide;
		        statement.executeUpdate(query1);
            }
            String query1 = "SELECT cache FROM  users WHERE id_user = "+user_id_pide+";";
	        rs2=statement.executeQuery(query1);
	        while (rs2.next()) {
	        	cache=rs2.getInt("cache");
	        }
	        
            String query2 = "SELECT count(*) as num_follow_cache FROM  users WHERE cache="+cache+" and id_user in (select id_user_acc from friendship where id_user_req = "+user_creador+");";
	        rs3 = statement.executeQuery(query2);
	        while (rs3.next()) {
	        	num_follow_cache=rs3.getInt("num_follow_cache");
	        }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(rs2);
            DbUtil.close(rs3);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return tweet.toString()+"("+num_follow_cache+")";
	}
	
}