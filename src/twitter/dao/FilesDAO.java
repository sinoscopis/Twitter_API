package twitter.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import twitter.db.DbUtil;
import twitter.db.TwitterConnection;
 
public class FilesDAO {
    private Connection connection;
    private Statement statement;
 
    public FilesDAO() { }
    
    public void insertFile(String fileName,int cache) throws SQLException, IOException {
    	String query = null;
        try {
            connection = TwitterConnection.getConnection();
	        statement = connection.createStatement();
	        if (cache == 1){
	        	query = "INSERT INTO Twitter.filesDistribution (fileName, cache_1, cache_2, cache_3, cache_4, cache_5) VALUES ('"+ fileName +"', TRUE, NULL, NULL, NULL, NULL);";
	        }
	        else if (cache == 2){
	        	query = "INSERT INTO Twitter.filesDistribution (fileName, cache_1, cache_2, cache_3, cache_4, cache_5) VALUES ('"+ fileName +"', NULL, TRUE, NULL, NULL, NULL);";
	        }
	        else if (cache == 3){
	        	query = "INSERT INTO Twitter.filesDistribution (fileName, cache_1, cache_2, cache_3, cache_4, cache_5) VALUES ('"+ fileName +"', NULL, NULL, TRUE, NULL, NULL);";
	        }
	        else if (cache == 4){
	        	query = "INSERT INTO Twitter.filesDistribution (fileName, cache_1, cache_2, cache_3, cache_4, cache_5) VALUES ('"+ fileName +"', NULL, NULL, NULL, TRUE, NULL);";
	        }
	        else if (cache == 5){
	        	query = "INSERT INTO Twitter.filesDistribution (fileName, cache_1, cache_2, cache_3, cache_4, cache_5) VALUES ('"+ fileName +"', NULL, NULL, NULL, NULL, TRUE);";
	        }
			statement.executeUpdate(query);
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
	}
}