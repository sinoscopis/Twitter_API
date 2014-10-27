/*
 * Interpreta los mensajes del cliente y realiza las acciones pertinentes para enviarles respuesta
 */

package twitter.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;



import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import twitter.dao.CacheDAO;
import twitter.dao.FilesDAO;
import twitter.dao.FollowersDAO;
import twitter.dao.FriendshipDAO;
import twitter.dao.TweetDAO;
import twitter.dao.UserDAO;
import twitter.to.Follower;
import twitter.to.Friendship;
import twitter.to.Tweet;
import twitter.to.User;
import twitter.to.Cache;

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
			else if(clientRequest != null && clientRequest.startsWith("fileInPushLRUCache,")) {
				peticion = clientRequest.split(",", 3);
				reply = registrarFile(peticion[1],Integer.parseInt(peticion[2]),"LRU_PUSH");
			}
			else if(clientRequest != null && clientRequest.startsWith("fileInPushECOCache,")) {
				peticion = clientRequest.split(",", 3);
				reply = registrarFile(peticion[1],Integer.parseInt(peticion[2]),"eCO_PUSH");
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
			else if(clientRequest != null && clientRequest.startsWith("New_Cache,")) {
				peticion = clientRequest.split(",", 4);
				reply = registrarCache(Integer.parseInt(peticion[1]),peticion[2],Integer.parseInt(peticion[3]));
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

	private String registrarCache(int cache_num, String cache_ip, int users) {
		CacheDAO cacheDao = new CacheDAO();
       	try {
       		cacheDao.insertCache(cache_num, "127.0.0.1", users);
       	} catch (SQLException e) {
       		e.printStackTrace();
       	}
		return "exit";
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

	private static String consumirTweet(int id_tweet, int user_id) {
		TweetDAO tweetDao = new TweetDAO(); 
        String reply = "";
        try {
        	reply = tweetDao.seeTweet(id_tweet, user_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Consumir -"+reply;
	}

	private static String addReTweetServer(int user, int id_tweet) throws IOException {
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
		if (prob>=1 && prob <25){
			tweet=randomFile();
			push(tweet, user);
		}
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
	
	private void push(String tweet, int user) throws IOException {
		CacheDAO cacheDao = new CacheDAO();
       	FollowersDAO followersDao = new FollowersDAO();
       	List<Follower> followers;
       	List<Cache> caches;
        try {
        	followers = followersDao.followersbycache(user);
            caches = cacheDao.getCaches();          		
            for (Cache cache : caches) {
            	for (Follower follower: followers){
            		int umbral_cache = (int) (cache.getCacheUsers() * Server.umbral_push)/100;
        			if (follower.getFollowersNum() > umbral_cache)
        				Enviar(tweet, cache, true);
        			else
        				Enviar(tweet, cache, false);
        		}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	private void Enviar(String tweet, Cache cache, boolean push) throws IOException {
		Socket socket = new Socket(cache.getCacheIp(), 60000);
		 
		//PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		//BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		String fromServer;
		String fromCache = null;
		
		while ((fromCache = in.readUTF()) != null) {
			if (fromCache.equals("exit"))
				break;
			else {
				if (push)
					fromServer = "Push," + tweet +",true";
				else
					fromServer = "Push," + tweet +",false";;
				if (fromServer != null) {
					//System.out.println("Client - " + fromUser);
					synchronized (socket){
						out.writeUTF(fromServer);
					}
				}
			}
		}
		
		out.close();
		in.close();
		socket.close();
		
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
	
	static String getTweetsServer(int user_id) throws InterruptedException, IOException {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
        String res = "";
        String reply = "continua";
        try {
            tweets = tweetDao.getFriendsTweets(user_id);	
            for (Tweet tweet : tweets) {
            	res = res + tweet;
            }
            
            Hashtable twts=new Hashtable();
            StringTokenizer tokens=new StringTokenizer(res, "(|)");
            int i=0;
            //Guardo la lista de tweets de todos mis amigos
            while(tokens.hasMoreTokens()){
                String a = tokens.nextToken();
                try  
                  {  
                     if(Integer.parseInt(a)>0){
                    	 i++;
                    	 twts.put(i, Integer.parseInt(a));
                     }
                  }  
                  catch(NumberFormatException nfe)  
                  {  
                  }
            }
            if (i>0) {
	            //Select random twit
				double rt = Math.random();
				double drt = rt * i;
				int rand_tweet = (int)drt +1;
				
				//Anado probabilidad de consumir el twit 50 50
				double randNumber = Math.random();
				double d1 = randNumber * 100;
				int range = (int)d1;
				if (range>=1 && range <50){
					//CONSUME el Twit
					int id_tweet=(int) twts.get(rand_tweet);
					reply = consumirTweet(id_tweet,user_id);
				}
				else {
					//No consume el twit
					//anado probabilidad para que lo retweetee
					double randNumber4 = Math.random();
					double d4 = randNumber4 * 100;
					int range4 = (int)d4;
					if (range4>=1 && range4 <20){
						//retweet
						int id_tweet=(int) twts.get(rand_tweet);
						reply = addReTweetServer(user_id,id_tweet);
						/*posted=posted+1;
						System.out.println("user - " + client_id + "  posted: "+ posted );*/
					}
					else {
						// no retweet
						reply = "continua";
					}
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return reply;
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