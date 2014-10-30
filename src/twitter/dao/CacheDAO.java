/*
 * Realiza las llamadas a la base de datos relacionadas con la gestion de caches
 */

package twitter.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import twitter.db.DbUtil;
import twitter.db.TwitterConnection;
import twitter.to.Cache;
 
public class CacheDAO {
    private Connection connection;
    private Statement statement;
 
    public CacheDAO() { }
 

	public void insertCache(int cache_num, String cache_ip, int users) throws SQLException {
		try {
            String query = "INSERT INTO Twitter.caches (cache_num, ip, users) VALUES ("+ cache_num +",'" + cache_ip +"',"+users+");";
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
	}


	public List<Cache> getCaches() throws SQLException {
		String query = "SELECT * FROM caches";
        List<Cache> list = new ArrayList<Cache>();
        Cache cache = null;
        ResultSet rs = null;
        try {
            connection = TwitterConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                cache = new Cache();
                /*Retrieve one user details
                and store it in user object*/
                cache.setCacheNum(rs.getInt("cache_num"));
                cache.setCacheIp(rs.getString("ip"));
                cache.setCacheUsers(rs.getInt("users"));
 
                //add each user to the list
                list.add(cache);
            }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
        return list;
	}
}