package twitter.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import twitter.db.DbUtil;
import twitter.db.TwitterConnection;
 
public class FilesDAO {
    private Connection connection;
    private Statement statement;
 
    public FilesDAO() { }
    
    public void insertFile(String fileName,int cache, String cache_type) throws SQLException, IOException {
    	ResultSet rs = null;
    	String query = null;
        try {
        	if (cache_type=="LRU"){
        		 connection = TwitterConnection.getConnection();
     	        statement = connection.createStatement();
     	        String check = "SELECT filesDistributionLRU.file_id FROM Twitter.filesDistributionLRU WHERE fileName ='"+ fileName+"';";
               	rs = statement.executeQuery(check);
               	boolean val = rs.next();
               	if (val == false){
               		connection = TwitterConnection.getConnection();
			        statement = connection.createStatement();
			        if (cache == 1){
			        	query = "INSERT INTO Twitter.filesDistributionLRU (fileName, cache_1) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 2){
			        	query = "INSERT INTO Twitter.filesDistributionLRU (fileName, cache_2) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 3){
			        	query = "INSERT INTO Twitter.filesDistributionLRU (fileName, cache_3) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 4){
			        	query = "INSERT INTO Twitter.filesDistributionLRU (fileName, cache_4) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 5){
			        	query = "INSERT INTO Twitter.filesDistributionLRU (fileName, cache_5) VALUES ('"+ fileName +"', TRUE);";
			        }
					statement.executeUpdate(query);
               	}
                else{
                	connection = TwitterConnection.getConnection();
    		        statement = connection.createStatement();
    		        if (cache == 1){
    		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_1=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 2){
    		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_2=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 3){
    		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_3=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 4){
    		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_4=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 5){
    		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_5=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    				statement.executeUpdate(query);    
                }
	        }
			else if (cache_type=="eCOUSIN"){
				connection = TwitterConnection.getConnection();
     	        statement = connection.createStatement();
     	        String check = "SELECT filesDistributionECO.file_id FROM Twitter.filesDistributionECO WHERE fileName ='"+ fileName+"';";
               	rs = statement.executeQuery(check);
               	boolean val = rs.next();
               	if (val == false){
               		connection = TwitterConnection.getConnection();
			        statement = connection.createStatement();
			        if (cache == 1){
			        	query = "INSERT INTO Twitter.filesDistributionECO (fileName, cache_1) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 2){
			        	query = "INSERT INTO Twitter.filesDistributionECO (fileName, cache_2) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 3){
			        	query = "INSERT INTO Twitter.filesDistributionECO (fileName, cache_3) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 4){
			        	query = "INSERT INTO Twitter.filesDistributionECO (fileName, cache_4) VALUES ('"+ fileName +"', TRUE);";
			        }
			        else if (cache == 5){
			        	query = "INSERT INTO Twitter.filesDistributionECO (fileName, cache_5) VALUES ('"+ fileName +"', TRUE);";
			        }
					statement.executeUpdate(query);
               	}
                else{
                	connection = TwitterConnection.getConnection();
    		        statement = connection.createStatement();
    		        if (cache == 1){
    		        	query = "UPDATE Twitter.filesDistributionECO SET cache_1=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 2){
    		        	query = "UPDATE Twitter.filesDistributionECO SET cache_2=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 3){
    		        	query = "UPDATE Twitter.filesDistributionECO SET cache_3=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 4){
    		        	query = "UPDATE Twitter.filesDistributionECO SET cache_4=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    		        else if (cache == 5){
    		        	query = "UPDATE Twitter.filesDistributionECO SET cache_5=TRUE WHERE fileName='"+ fileName +"';";
    		        }
    				statement.executeUpdate(query);    
                }
			}
			
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
	}
    
    public void updateDeletedFile(String fileName,int cache, String cache_type) throws SQLException, IOException {
    	String query = null;
        try {
        	if (cache_type=="LRU"){
        		connection = TwitterConnection.getConnection();
		        statement = connection.createStatement();
		        if (cache == 1){
		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_1=FALSE WHERE fileName='"+ fileName +"';";
		        }
		        else if (cache == 2){
		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_2=FALSE WHERE fileName='"+ fileName +"';";
		        }
		        else if (cache == 3){
		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_3=FALSE WHERE fileName='"+ fileName +"';";
		       	}
		        else if (cache == 4){
		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_4=FALSE WHERE fileName='"+ fileName +"';";
		        }
		        else if (cache == 5){
		        	query = "UPDATE Twitter.filesDistributionLRU SET cache_5=FALSE WHERE fileName='"+ fileName +"';";
		        }
				statement.executeUpdate(query); 
	        }
			else if (cache_type=="eCOUSIN"){
				connection = TwitterConnection.getConnection();
		        statement = connection.createStatement();
		        if (cache == 1){
		        	query = "UPDATE Twitter.filesDistributionECO SET cache_1=FALSE WHERE fileName='"+ fileName +"';";
		        }
		        else if (cache == 2){
		        	query = "UPDATE Twitter.filesDistributionECO SET cache_2=FALSE WHERE fileName='"+ fileName +"';";
		        }
		        else if (cache == 3){
		        	query = "UPDATE Twitter.filesDistributionECO SET cache_3=FALSE WHERE fileName='"+ fileName +"';";
		       	}
		        else if (cache == 4){
		        	query = "UPDATE Twitter.filesDistributionECO SET cache_4=FALSE WHERE fileName='"+ fileName +"';";
		        }
		        else if (cache == 5){
		        	query = "UPDATE Twitter.filesDistributionECO SET cache_5=FALSE WHERE fileName='"+ fileName +"';";
		        }
				statement.executeUpdate(query);
			}
			
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
	}
    
    
    

	public int[] cost(String fileName, int cache, String cache_type) throws SQLException, IOException {
		ResultSet rs = null;
    	int[] caches = null;
        try {
        	if (cache_type=="LRU"){
        		connection = TwitterConnection.getConnection();
     	        statement = connection.createStatement();
     	        String check = "SELECT * FROM Twitter.filesDistributionLRU WHERE fileName ='"+ fileName+"';";
               	rs = statement.executeQuery(check);
               	boolean val = rs.next();
               	if (val == false){
               		caches = new int[1];
               		caches[0]=0;
               	}
                else{
                	caches = new int[5];
                    if (rs.getBoolean("cache_1"))
                    	caches[0]= 1;
                    else
                    	caches[0]= 0;
                    if (rs.getBoolean("cache_2"))
                    	caches[1]= 1;
                    else
                    	caches[1]= 0;
                    if (rs.getBoolean("cache_3"))
                    	caches[2]= 1;
                    else
                    	caches[2]= 0;
                    if (rs.getBoolean("cache_4"))
                    	caches[3]= 1;
                    else
                    	caches[3]= 0;
                    if (rs.getBoolean("cache_5"))
                    	caches[4]= 1;
                    else
                    	caches[4]= 0;
                }
	        }
			else if (cache_type=="eCOUSIN"){
				connection = TwitterConnection.getConnection();
     	        statement = connection.createStatement();
     	        String check = "SELECT * FROM Twitter.filesDistributionECO WHERE fileName ='"+ fileName+"';";
               	rs = statement.executeQuery(check);
               	boolean val = rs.next();
               	if (val == false){
               		caches = new int[1];
               		caches[0]=0;
               	}
                else{
                	caches = new int[5];
                    if (rs.getBoolean("cache_1"))
                    	caches[0]= 1;
                    else
                    	caches[0]= 0;
                    if (rs.getBoolean("cache_2"))
                    	caches[0]= 1;
                    else
                    	caches[0]= 0;
                    if (rs.getBoolean("cache_3"))
                    	caches[0]= 1;
                    else
                    	caches[0]= 0;
                    if (rs.getBoolean("cache_4"))
                    	caches[0]= 1;
                    else
                    	caches[0]= 0;
                    if (rs.getBoolean("cache_5"))
                    	caches[0]= 1;
                    else
                    	caches[0]= 0;
                }
			}
			
        } finally {
            DbUtil.close(statement);
            DbUtil.close(connection);
        }
		return caches;
	}
}