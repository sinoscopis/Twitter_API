package com.twitter.jdbc.to;

public class User {
    private int userId;
    private String userName;
    private int lastTweetId;
 
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getLastTweetId() {
        return lastTweetId;
    }
    public void setLastTweetId(int lastTweetId) {
        this.lastTweetId = lastTweetId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String toString() {
    	return " [" + userId + "," + userName + "," + lastTweetId + "]";
    }
}