package com.twitter.jdbc.main;

import java.io.IOException;
import java.sql.SQLException;

import com.twitter.jdbc.dao.UserDAO;
import com.twitter.jdbc.dao.TweetDAO;
import com.twitter.jdbc.dao.FriendshipDAO;
import com.twitter.jdbc.to.Tweet;
import com.twitter.jdbc.to.User;
import com.twitter.jdbc.to.Friendship;

import java.util.List;
import java.util.Scanner;
 
public class Test {
    public static void main(String[] args) {
    	System.out.println("*********************");
    	System.out.println("* 1- SHOW table     *");
    	System.out.println("* 2- INSERT         *");
    	System.out.println("*********************");
    	
    	Scanner keyboard = new Scanner(System.in);
        int dob = keyboard.nextInt();
        switch(dob)
        {
        case 1:
        	
        	System.out.println("*********************");
        	System.out.println("* 1- SHOW USERS     *");
        	System.out.println("* 2- SHOW TWEETS    *");
        	System.out.println("* 3- SHOW FRIENDS   *");
        	System.out.println("*********************");
        	Scanner keyboard3 = new Scanner(System.in);
            int dob3 = keyboard3.nextInt();
            switch(dob3)
            {
            case 1:
            	getUsers();
                break;
            case 2:
            	 getTweets();
            	break;
            case 3:
            	getFriendships();
            	break; 
            }
            break;
        case 2:
        	System.out.println("*********************");
        	System.out.println("* 1- INSERT TWEET   *");
        	System.out.println("* 2- INSERT USER    *");
        	System.out.println("*********************");
        	Scanner keyboard2 = new Scanner(System.in);
            int dob2 = keyboard2.nextInt();
            switch(dob2)
            {
            case 1:
            	try {
                	addTweet();
                	keyboard2.close();
                    keyboard.close();
        		} catch (IOException e) {
        			System.out.println("No se ha realizado el INSERT");
        			e.printStackTrace();
        		}
                break;
            case 2:
            	try {
        			addUser();
                	keyboard2.close();
                    keyboard.close();
        		} catch (IOException e) {
        			System.out.println("No se ha realizado el INSERT");
        			e.printStackTrace();
        		}
            	break; 
            }
            break;
        } 
    }
 
    private static void getUsers() {
        UserDAO userDao = new UserDAO();
        List<User> users;
        try {
        	System.out.println("-- users table --");
            users = userDao.getUsers();
            for (User user : users) {
                //displayUser(user);
                System.out.println(user);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*private static void displayUser(User user) {
        System.out.println("User ID:" + user.getUserId());
        System.out.println("User Name:" + user.getUserName());
        System.out.println("Last Tweet ID:" + user.getLastTweetId());
        System.out.println();
    }*/
    
    private static void getTweets() {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
        try {
        	System.out.println("-- tweets table --");
            tweets = tweetDao.getTweets();
            for (Tweet tweet : tweets) {
            	//displayTweet(tweet);
                System.out.println(tweet);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*private static void displayTweet(Tweet tweet) {
        System.out.println("Tweet ID:" + tweet.getTweetId());
        System.out.println("Tweet Sender ID:" + tweet.getTweetSenderId());
        System.out.println("Tweet:" + tweet.getTweetText());
        System.out.println("Date:" + tweet.getDot());
        System.out.println();
    }*/
    
    private static void getFriendships() {
        FriendshipDAO friendshipDao = new FriendshipDAO();
        List<Friendship> friendships;
        try {
        	System.out.println("-- friendhip table --");
            friendships = friendshipDao.getFriendships();
            for (Friendship friendship : friendships) {
            	//displayFriendship(friendship);
                System.out.println(friendship);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*private static void displayFriendship(Friendship friendship) {
        System.out.println("User request ID:" + friendship.getReqUserId());
        System.out.println("User accept ID:" + friendship.getAccUserId());
        System.out.println("Date:" + friendship.getDof());
        System.out.println();
    }*/
    
	private static void addUser() throws IOException {
        UserDAO userDao = new UserDAO();     
        try {
        		userDao.insertUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void addTweet() throws IOException {
           TweetDAO tweetDao = new TweetDAO();     
           try {
           		tweetDao.insertTweet();
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
}