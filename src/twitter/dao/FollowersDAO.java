package twitter.dao;

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

		public List<Follower> followersbycache(int user) throws SQLException {
			String query = "SELECT * FROM followersByCluster WHERE user_id="+user;
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
	            	follower.setUserId(rs.getInt("user_id"));
	            	follower.setCacheNum(rs.getInt("cache"));
	            	follower.setFollowersNum((rs.getInt("friends")));
	 
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
	
}