package com.twitter.jdbc.server;

import java.net.*;
import java.sql.SQLException;
import java.util.List;
import java.io.*;

import com.twitter.jdbc.dao.*;
import com.twitter.jdbc.to.*;
 
/**
 * Demo Server: Contains a multi-threaded socket server sample code.
 */
public class Server extends Thread
{
	final static int _portNumber = 5559; //Arbitrary port number
 
	public static void main(String[] args) 
	{
		try {
			new Server().startServer();
		} catch (Exception e) {
			System.out.println("I/O failure: " + e.getMessage());
			e.printStackTrace();
		}
 
	}
 
	public void startServer() throws Exception {
		ServerSocket serverSocket = null;
		boolean listening = true;
 
		try {
			serverSocket = new ServerSocket(_portNumber);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + _portNumber);
			System.exit(-1);
		}
 
		while (listening) {
			handleClientRequest(serverSocket);
		}
 
		serverSocket.close();
	}
 
	private void handleClientRequest(ServerSocket serverSocket) {
		try {
			new ConnectionRequestHandler(serverSocket.accept()).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	/**
	 * Handles client connection requests. 
	 */
	public class ConnectionRequestHandler implements Runnable{
		private Socket _socket = null;
		private PrintWriter _out = null;
		private BufferedReader _in = null;
 
		public ConnectionRequestHandler(Socket socket) {
			_socket = socket;
		}
 
		public void run() {
			System.out.println("Client connected to socket: " + _socket.toString());
 
			try {
				_out = new PrintWriter(_socket.getOutputStream(), true);
				_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
 
				String inputLine, outputLine;
				BusinessLogic businessLogic = new BusinessLogic();
				outputLine = businessLogic.processInput(null);
				_out.println(outputLine);
 
				//Read from socket and write back the response to client. 
				while ((inputLine = _in.readLine()) != null) {
					outputLine = businessLogic.processInput(inputLine);
					if(outputLine != null) {
						_out.println(outputLine);
						if (outputLine.equals("exit")) {
							System.out.println("Server is closing socket for client:" + _socket.getLocalSocketAddress());
							break;
						}
					} else {
						System.out.println("OutputLine is null!!!");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally { //In case anything goes wrong we need to close our I/O streams and sockets.
				try {
					_out.close();
					_in.close();
					_socket.close();
				} catch(Exception e) { 
					System.out.println("Couldn't close I/O streams");
				}
			}
		}
 
	}
 
	/**
	 * Handles business logic of application.
	 */
	public static class BusinessLogic {
 
		public String processInput(String clientRequest) {
			String reply = null;
			String peticion[];
			try {
				if(clientRequest != null && clientRequest.equalsIgnoreCase("exit")) {
					return "exit";
				}
				
				if(clientRequest != null && clientRequest.equalsIgnoreCase("show,users")) {
					reply = getUsersServer();
				}
				if(clientRequest != null && clientRequest.equalsIgnoreCase("countusers")) {
					reply = Integer.toString(getUsersCountServer());
				}
				else if(clientRequest != null && clientRequest.startsWith("showid,")) {
					peticion = clientRequest.split(",", 2);
					reply = getUsrTweetsServer(Integer.parseInt(peticion[1]));
				}
				else if(clientRequest != null && clientRequest.equalsIgnoreCase("show,tweets")) {
					reply = getTweetsServer();
				}
				else if(clientRequest != null && clientRequest.equalsIgnoreCase("show,friends")) {
					reply = getFriendshipsServer();
				}
				else if(clientRequest != null && clientRequest.startsWith("insertuser,")) {
					peticion = clientRequest.split(",", 2);
					reply = addUserServer(peticion[1]);
				}
				else if(clientRequest != null && clientRequest.startsWith("inserttweet,")) {
					peticion = clientRequest.split(",", 3);
					reply = addTweetServer(Integer.parseInt(peticion[1]),peticion[2]);
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
	
	private static String getUsrTweetsServer(int user_id) {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
        String res = "";
        try {
            tweets = tweetDao.getUserTweets(user_id);
            for (Tweet tweet : tweets) {
            	res = res + tweet;
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
	private static String getTweetsServer(int user_id) {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
        String res = "";
        try {
            tweets = tweetDao.getFriendsTweets(user_id);	
            for (Tweet tweet : tweets) {
            	res = res + tweet;
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
	private static String addUserServer(String new_user) throws IOException {
        UserDAO userDao = new UserDAO();     
        try {
        		userDao.insertUser(new_user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "inserted";
    }
    
    private static String addTweetServer(int user, String tweet) throws IOException {
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