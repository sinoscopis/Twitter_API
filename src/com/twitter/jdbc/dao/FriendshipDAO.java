package com.twitter.jdbc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.twitter.jdbc.db.TwitterConnection;
import com.twitter.jdbc.db.DbUtil;
import com.twitter.jdbc.to.Friendship;
 
public class FriendshipDAO {
    private Connection connection;
    private Statement statement;
 
    public FriendshipDAO() { }
 
    public List<Friendship> getFriendships() throws SQLException {
        String query = "SELECT * FROM friendship";
        List<Friendship> list = new ArrayList<Friendship>();
        Friendship friendship = null;
        ResultSet rs = null;
        try {
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                friendship = new Friendship();
                /*Retrieve one friendship details
                and store it in friendship object*/
                friendship.setReqUserId(rs.getInt("id_user_req"));
                friendship.setAccUserId(rs.getInt("id_user_acc"));
                friendship.setDof(rs.getDate("date"));
                //add each friendship to the list
                list.add(friendship);
            }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return list;
    }

	public void befriends(int usr_req, int usr_acc) throws SQLException {
        try {
            connection = TwitterConnection.getConnection();
	        statement = connection.createStatement();
	        String query = "INSERT INTO friendship (id_user_req,id_user_acc) VALUES ("+ usr_req +"," + usr_acc + ");";
          	statement.executeUpdate(query);
        } catch (SQLException e){
        	System.out.println("Adding friendship failed:"+ e.getSQLState());
        }
        finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
	}
}