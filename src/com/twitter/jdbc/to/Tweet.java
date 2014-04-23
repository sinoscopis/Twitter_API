package com.twitter.jdbc.to;

import java.util.Date;

public class Tweet {
	private int tweetId;
	private int tweetsenderId;
    private String tweetText;
    private Date dot;
 
    public int getTweetId() {
        return tweetId;
    }
    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }
    public int getTweetSenderId() {
        return tweetsenderId;
    }
    public void setTweetSenderId(int tweetsenderId) {
        this.tweetsenderId = tweetsenderId;
    }
    public String getTweetText() {
        return tweetText;
    }
    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }
    public Date getDot() {
        return dot;
    }
    public void setDot(Date dot) {
        this.dot = dot;
    }
    public String toString() {
    	return "[" + tweetId + "," + tweetsenderId + "," + dot + "," + tweetText + "]";
    }
}
