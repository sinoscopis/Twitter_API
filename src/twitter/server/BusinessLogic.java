package twitter.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter.dao.FollowersDAO;
import twitter.dao.FriendshipDAO;
import twitter.dao.TweetDAO;
import twitter.dao.UserDAO;
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
			if(clientRequest != null && clientRequest.equalsIgnoreCase("show,users")) {
				reply = getUsersServer();
			}
			else if(clientRequest != null && clientRequest.equalsIgnoreCase("conectado_almacenado")) {
				return "continua";
			}
			else if(clientRequest != null && clientRequest.equalsIgnoreCase("countusers")) {
				reply = Integer.toString(getUsersCountServer());
			}
			else if(clientRequest != null && clientRequest.startsWith("conectUser,")) {
				peticion = clientRequest.split(",", 2);
				reply = ConectUser(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.startsWith("showid,")) {
				peticion = clientRequest.split(",", 2);
				reply = getUsrTweetsServer(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.startsWith("Consumo,")) {
				peticion = clientRequest.split(",", 2);
				reply = consumirTweet(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.equalsIgnoreCase("show,tweets")) {
				reply = getTweetsServer();
			}
			else if(clientRequest != null && clientRequest.equalsIgnoreCase("show,friends")) {
				reply = getFriendshipsServer();
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
			else if(clientRequest != null && clientRequest.startsWith("inserttweet,")) {
				peticion = clientRequest.split(",", 3);
				reply = addTweetServer(Integer.parseInt(peticion[1]),peticion[2]);
			}
			else if(clientRequest != null && clientRequest.startsWith("insertrandomtweet,")) {
				peticion = clientRequest.split(",", 2);
				reply = addRandomTweetServer(Integer.parseInt(peticion[1]));
			}
			else if(clientRequest != null && clientRequest.startsWith("retweet,")) {
				peticion = clientRequest.split(",", 3);
				reply = addReTweetServer(Integer.parseInt(peticion[1]),Integer.parseInt(peticion[2]));
			}
			else if(clientRequest != null && clientRequest.startsWith("insertfollowersbycluster,")) {
				peticion = clientRequest.split(",", 4);
				reply = addFollowersByCluster(Integer.parseInt(peticion[1]),Integer.parseInt(peticion[2]),Integer.parseInt(peticion[3]));
			}
			else if(clientRequest != null && clientRequest.startsWith("friendstweets,")) {
				peticion = clientRequest.split(",", 2);
				reply = getTweetsServer(Integer.parseInt(peticion[1]));
			}
			else {
				reply = "......";
			}
		} catch(Exception e) {
			System.out.println("input process falied: " + e.getMessage());
			return "exit";
		}

		return reply;
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

	private String addFollowersByCluster(int user, int num_friends,int cluster) throws IOException {
		FollowersDAO followersDao = new FollowersDAO();     
        try {
        	followersDao.insertOnCluster(user,num_friends,cluster);
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return "Followers_set";
	}

	private String consumirTweet(int id_tweet) {
		TweetDAO tweetDao = new TweetDAO(); 
        String reply = "";
        try {
        	reply = tweetDao.seeTweet(id_tweet);
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
		double d1 = randNumber * 10;
		int prob = (int)d1+1;
		String tweet;
		if (prob == 1)
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
		 String path = null;
		 if(sSistemaOperativo.startsWith("Win")){
			 path = "C:\\Users\\Alberto\\Desktop\\Server_Content";
			 //path = ".\\Server_Content";
		 }
		 else {
			 path = "./Server_Content";
		 }
		 File folder = new File(path);
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
	
	static String getTweetsServer(int user_id) {
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