package twitter.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import twitter.db.DbUtil;
import twitter.db.TwitterConnection;
import twitter.to.User;
 
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
            String query = "INSERT INTO Twitter.users (id_user, name_user) VALUES (NULL, '"+ new_user +"');";
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
    }
    
    public int countUsers() throws SQLException, IOException {
	    String query = "SELECT count(*) FROM users";
	    int count = 0;
	    ResultSet rs = null;
	    try {
	        connection = TwitterConnection.getConnection();
	        statement = connection.createStatement();
	        rs = statement.executeQuery(query);
	        while (rs.next()) {
	            count = rs.getInt("count(*)");
	        }
	    } finally {
	        DbUtil.close(rs);
	        DbUtil.close(statement);
	        DbUtil.close(connection);
	    }
	    return count;
    }
    
    public void insertUser(String new_user,int cache) throws SQLException, IOException {
        try {
            String query = "INSERT INTO Twitter.users (id_user, name_user,cache,conectado) VALUES (NULL, '"+ new_user +"','" + cache +"',FALSE);";
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
    }

	public String conectUser(int cache) throws SQLException, IOException {
		 List<User> list;
		 int range;
		 try {
			 	list = getUsersInCache(cache);
			 	double randNumber = Math.random();
				double d = randNumber * list.size();
				range = (int)d;
		        String query = "UPDATE users SET conectado=TRUE WHERE (cache="+ cache +" and conectado=FALSE and id_user="+ list.get(range).getUserId() +")";
		        connection = TwitterConnection.getConnection();
		        statement = connection.createStatement();
		        statement.executeUpdate(query);
		    } finally {
		        DbUtil.close(statement);
		        DbUtil.close(connection);
		    }
		 return "conectado,"+ list.get(range).getUserId();
	}
	
	public List<User> getUsersInCache(int cache) throws SQLException {
        String query = "SELECT * FROM users WHERE cache="+cache;
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
	

	public void iniciarUser(int id_user) throws SQLException {
		connection = TwitterConnection.getConnection();
        statement = connection.createStatement();
        
        String query3 = "INSERT INTO Twitter.followersByCluster (user_id,cache,friends) VALUES ('"+ id_user +"',1,0);";
        String query4 = "INSERT INTO Twitter.followersByCluster (user_id,cache,friends) VALUES ('"+ id_user +"',2,0);";
        String query5 = "INSERT INTO Twitter.followersByCluster (user_id,cache,friends) VALUES ('"+ id_user +"',3,0);";
        String query6 = "INSERT INTO Twitter.followersByCluster (user_id,cache,friends) VALUES ('"+ id_user +"',4,0);";
        String query7 = "INSERT INTO Twitter.followersByCluster (user_id,cache,friends) VALUES ('"+ id_user +"',5,0);";
        statement.executeUpdate(query3);
        statement.executeUpdate(query4);
        statement.executeUpdate(query5);
        statement.executeUpdate(query6);
        statement.executeUpdate(query7);
	}
}