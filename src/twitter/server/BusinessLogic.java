package twitter.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter.dao.FilesDAO;
import twitter.dao.FollowersDAO;
import twitter.dao.FriendshipDAO;
import twitter.dao.TweetDAO;
import twitter.dao.UserDAO;
import twitter.to.Follower;
import twitter.to.Friendship;
import twitter.to.Tweet;
import twitter.to.User;

/**
 * Handles business logic of application.
 */
public class BusinessLogic {

	public String processInput(String clientRequest) {
		String reply = null;
		String peticion[];
		try {
			if(clientRequest != null && clientRequest.equalsIgnoreCase("exit")) {
				return "exit";
			}
			if(clientRequest != null && clientRequest.equalsIgnoreCase("Retweet analizado")) {
				return "continua";
			}
			if(clientRequest != null && clientRequest.equalsIgnoreCase("No retweeteo")) {
				return "continua";
			}
			if(clientRequest != null && clientRequest.equalsIgnoreCase("continua")) {
				return "continua";
			}
			if(clientRequest != null && clientRequest.equalsIgnoreCase("show,users")) {
				reply = getUsersServer();
			}
			else if(clientRequest != null && clientRequest.startsWith("insertuser,")) {
				peticion = clientRequest.split(",", 3);
				reply = addUserServer(peticion[1],Integer.parseInt(peticion[2]));
			}
			else if(clientRequest != null && clientRequest.startsWith("insertfriendship,")) {
				peticion = clientRequest.split(",", 3);
				if (Integer.parseInt(peticion[1]) != Integer.parseInt(peticion[2]))
					reply = addFriendshipServer(Integer.parseInt(peticion[1]),Integer.parseInt(peticion[2]));
				else
					reply = "NO puedes hacerte amigo de ti mismo";
			}
			else if(clientRequest != null && clientRequest.equalsIgnoreCase("countusers")) {
				reply = Integer.toString(getUsersCountServer());
			}
			else if(clientRequest != null && clientRequest.startsWith("conectUser,")) {
				peticion = clientRequest.split(",", 2);
				reply = ConectUser(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.startsWith("conectado_almacenado,")) {
				peticion = clientRequest.split(",", 2);
				reply = friendsincaches(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.startsWith("inserttweet,")) {
				peticion = clientRequest.split(",", 3);
				reply = addTweetServer(Integer.parseInt(peticion[1]),peticion[2]);
			}
			else if(clientRequest != null && clientRequest.startsWith("insertrandomtweet,")) {
				peticion = clientRequest.split(",", 2);
				reply = addRandomTweetServer(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.startsWith("friendstweets,")) {
				peticion = clientRequest.split(",", 2);
				reply = getTweetsServer(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.startsWith("retweet,")) {
				peticion = clientRequest.split(",", 3);
				reply = addReTweetServer(Integer.parseInt(peticion[1]),Integer.parseInt(peticion[2]));
			}
			else if(clientRequest != null && clientRequest.startsWith("Consumo,")) {
				peticion = clientRequest.split(",", 3);
				reply = consumirTweet(Integer.parseInt(peticion[1]),Integer.parseInt(peticion[2]));
			}
			else if(clientRequest != null && clientRequest.startsWith("fileInLRUCache,")) {
				peticion = clientRequest.split(",", 3);
				reply = registrarFile(peticion[1],Integer.parseInt(peticion[2]),"LRU");
			}
			else if(clientRequest != null && clientRequest.startsWith("fileInECOCache,")) {
				peticion = clientRequest.split(",", 3);
				reply = registrarFile(peticion[1],Integer.parseInt(peticion[2]),"eCOUSIN");
			}
			else if(clientRequest != null && clientRequest.startsWith("costeLRU,")) {
				peticion = clientRequest.split(",", 3);
				reply = calcularcoste(peticion[1],Integer.parseInt(peticion[2]),"LRU");
			}
			else if(clientRequest != null && clientRequest.startsWith("costeECO,")) {
				peticion = clientRequest.split(",", 3);
				reply = calcularcoste(peticion[1],Integer.parseInt(peticion[2]),"eCOUSIN");
			}
			else if(clientRequest != null && clientRequest.startsWith("costeLRUwithUPD,")) {
				peticion = clientRequest.split(",", 4);
				reply = calcularcostewithUPD(peticion[1],Integer.parseInt(peticion[2]),"LRU",peticion[3]);
			}
			else if(clientRequest != null && clientRequest.startsWith("costeECOwithUPD,")) {
				peticion = clientRequest.split(",", 4);
				reply = calcularcostewithUPD(peticion[1],Integer.parseInt(peticion[2]),"eCOUSIN",peticion[3]);
			}
			else {
				reply = "......";
			}
		} catch(Exception e) {
			System.out.println("input process failed: " + e.getMessage());
			e.printStackTrace();
			return "exit";
		}

		return reply;
	}

	private String calcularcostewithUPD(String file, int cache_num, String cache_type, String first) throws IOException {
		int reply = 0;
		int[] caches = null;
		FilesDAO filesDao = new FilesDAO();
       	try {
	   		caches = filesDao.cost(file,cache_type);
	   		filesDao.updateDeletedFile(first, cache_num, cache_type);
	   		if (caches.length == 1){
	   			reply=Server.costs_matrix[5][cache_num-1];
	   		}
	   		else{
	   			int minimal = 10;
	   			for(int i=0; i<caches.length; i++){
	   				if(caches[i]==1){
	   					if (Server.costs_matrix[i][cache_num-1]<minimal)
	   						minimal=Server.costs_matrix[i][cache_num-1];
	   				}
	   			}
	   			reply=minimal;
	   			if (reply==0){
	   				reply=Server.costs_matrix[5][cache_num-1];
	   			}
	   		}
       	} catch (SQLException e) {
       		e.printStackTrace();
       	}
		return "menorcoste,"+Integer.toString(reply);
	}

	private String calcularcoste(String file, int cache_num, String cache_type) throws IOException {
		int reply = 0;
		int[] caches = null;
		FilesDAO filesDao = new FilesDAO();
       	try {
	   		caches = filesDao.cost(file,cache_type);
	   		if (caches.length == 1){
	   			reply=Server.costs_matrix[5][cache_num-1];
	   		}
	   		else{
	   			int minimal = 10;
	   			for(int i=0; i<caches.length; i++){
	   				if(caches[i]==1){
	   					if (Server.costs_matrix[i][cache_num-1]<minimal)
	   						minimal=Server.costs_matrix[i][cache_num-1];
	   				}
	   			}
	   			reply=minimal;
	   			if (reply==0){
	   				reply=Server.costs_matrix[5][cache_num-1];
	   			}
	   		}
       	} catch (SQLException e) {
       		e.printStackTrace();
       	}
		return "menorcoste,"+Integer.toString(reply);
	}

	private String registrarFile(String file, int cache_num, String cache_type) throws IOException {
		FilesDAO filesDao = new FilesDAO();
       	try {
	   		filesDao.insertFile(file, cache_num,cache_type);
       	} catch (SQLException e) {
       		e.printStackTrace();
       	}
		return "exit";
	}
	
	private String ConectUser(int cache) throws IOException {
		UserDAO userDao = new UserDAO();   
		String reply = "";
        try {
        	reply = userDao.conectUser(cache);
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return reply;
	}

	private String friendsincaches(int user) throws IOException {
		FollowersDAO followersDao = new FollowersDAO();
		String reply = "Conectedwith-";
		List<Follower> following;
        try {
        	following = followersDao.followersbycache(user);
        	for (Follower follower : following) {
            	reply = reply + follower.getCacheNum()+","+follower.getFollowersNum()+",";
            }
        	//reply = reply.substring(0, reply.length() - 1) + ']';
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reply;
	}

	private String consumirTweet(int id_tweet, int user_id) {
		TweetDAO tweetDao = new TweetDAO(); 
        String reply = "";
        try {
        	reply = tweetDao.seeTweet(id_tweet, user_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Consumir -"+reply;
	}

	private String addReTweetServer(int user, int id_tweet) throws IOException {
		TweetDAO tweetDao = new TweetDAO(); 
           String reply = "";
           try {
           		reply = tweetDao.insertReTweet(user,id_tweet);
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return reply;
	}

	private String addRandomTweetServer(int user) throws IOException {
		TweetDAO tweetDao = new TweetDAO();
		double randNumber = Math.random();
		double d1 = randNumber * 100;
		int prob = (int)d1;
		String tweet;
		if (prob>=1 && prob <25)
			tweet=randomFile();
		else
			tweet=randomIdentifier(140);
       	String reply = "";
       	try {
	   		reply = tweetDao.insertTweet(user,tweet);
       	} catch (SQLException e) {
       		e.printStackTrace();
       	}
       	return reply;
	}
	
	public static String randomFile() {
		 String sSistemaOperativo = System.getProperty("os.name");
		 String file_path = null;
		 if(sSistemaOperativo.startsWith("Win")){
			 file_path = "C:\\Users\\Alberto\\Desktop\\Server_Content";
			 //file_path = ".\\Server_Content";
		 }
		 else {
			 file_path = "./Server_Content";
		 }
		 File folder = new File(file_path);
		 File[] listOfFiles = folder.listFiles();
		
		double randNumber = Math.random();
		double d1 = randNumber * listOfFiles.length;
		int prob = (int)d1;
		
		return "http://localhost/" + listOfFiles[prob].getName();
	}

	public static String randomIdentifier(int max_length) {
		final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
		final java.util.Random rand = new java.util.Random();
		// consider using a Map<String,Boolean> to say whether the identifier is being used or not 
		final Set<String> identifiers = new HashSet<String>();
			StringBuilder builder = new StringBuilder();
		    while(builder.toString().length() == 0) {
		    	double randNumber = Math.random();
				double d1 = randNumber * max_length;
				int length = (int)d1;
		        for(int i = 0; i < length; i++)
		            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
		        if(identifiers.contains(builder.toString())) 
		            builder = new StringBuilder();
		    }
		    return builder.toString();
	}

	private String addFriendshipServer(int usr_req, int usr_acc) {
		FriendshipDAO friendshipDao = new FriendshipDAO();     
        try {
        		friendshipDao.befriends(usr_req,usr_acc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "friends";
	}

	private int getUsersCountServer() throws IOException {		
		UserDAO userDao = new UserDAO();
    	int res = 0;
        try {
            res = userDao.countUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
	}

	public static String getUsersServer() {
        UserDAO userDao = new UserDAO();
        List<User> users;
    	String res = "";
        try {
            users = userDao.getUsers();
            for (User user : users) {
            	res = res + user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
	public static String getTweetsServer() {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
    	String res = "";
        try {
            tweets = tweetDao.getTweets();          		
            for (Tweet tweet : tweets) {
            	res = res + tweet;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
	public static String getFriendshipsServer() {
        FriendshipDAO friendshipDao = new FriendshipDAO();
        List<Friendship> friendships;
    	String res = "";
        try {
        	friendships = friendshipDao.getFriendships();
            for (Friendship friendship : friendships) {
            	res = res + friendship;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
	static String getUsrTweetsServer(int user_id) {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
        String res = "";
        try {
            tweets = tweetDao.getUserTweets(user_id);
            for (Tweet tweet : tweets) {
            	res = res + tweet;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
	static String getTweetsServer(int user_id) throws InterruptedException {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
        String res = "";
        try {
            tweets = tweetDao.getFriendsTweets(user_id);	
            for (Tweet tweet : tweets) {
            	res = res + tweet;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
	static String addUserServer(String new_user,int cache) throws IOException {
        UserDAO userDao = new UserDAO();     
        try {
        		userDao.insertUser(new_user,cache);
        		int id_user= userDao.countUsers();
        		userDao.iniciarUser(id_user);
        		String usr = Integer.toString(id_user);
        		String reply="inserted,"+ usr;
        		return reply;
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return "not inserted";

    }
    
    static String addTweetServer(int user, String tweet) throws IOException {
           TweetDAO tweetDao = new TweetDAO();     
           String reply = "";
           try {
           		reply = tweetDao.insertTweet(user,tweet);
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return reply;
    }
}