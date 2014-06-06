package twitter.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import twitter.db.DbUtil;
import twitter.db.TwitterConnection;
import twitter.to.Follower;

public class FollowersDAO {

	 private Connection connection;
	    private Statement statement;
	 
	    public FollowersDAO() { }
	 
	    public List<Follower> getUsers() throws SQLException {
	        String query = "SELECT * FROM followers";
	        List<Follower> list = new ArrayList<Follower>();
	        Follower follower = null;
	        ResultSet rs = null;
	        try {
	            connection = TwitterConnection.getConnection();
	            statement = connection.createStatement();
	            rs = statement.executeQuery(query);
	            while (rs.next()) {
	            	follower = new Follower();
	                /*Retrieve one user details
	                and store it in user object*/
	            	follower.setUserId(rs.getInt("id_user"));
	            	follower.setCacheNum(rs.getInt("cache"));
	            	follower.setFollowersNum((rs.getInt("followers")));
	 
	                //add each user to the list
	                list.add(follower);
	            }
	        } finally {
	            DbUtil.close(rs);
	            DbUtil.close(statement);
	            DbUtil.close(connection);
	        }
	        return list;
	    }
	    
	    public void insertOnCluster(int user,int friends, int cache) throws SQLException, IOException {
	        try {
	            String query = "INSERT INTO Twitter.followersByCluster (user_id, cache_num,friends) VALUES ("+ user +", "+ cache +","+ friends+");";
	            connection = TwitterConnection.getConnection();
	            statement = connection.createStatement();
	            statement.executeUpdate(query);
	        } finally {
	            DbUtil.close(statement);
	            DbUtil.close(connection);
	        }
	    }
	
	
}
