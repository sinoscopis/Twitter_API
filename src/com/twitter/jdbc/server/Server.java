package com.twitter.jdbc.server;

import java.net.*;
import java.io.*;
import java.sql.SQLException;

import com.twitter.jdbc.dao.*;
import com.twitter.jdbc.to.*;

import java.util.List;
import java.util.Scanner;
 
/**
 * Contains a multi-threaded socket server  code.
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
						System.out.println(outputLine);
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
			try {
				if(clientRequest != null && clientRequest.equalsIgnoreCase("exit")) {
					return "exit";
				}
				if(clientRequest != null && clientRequest.matches("show,users")) {
					reply = getUsersServer();
				}
				/*if(clientRequest != null && clientRequest.matches("show,tweets")) {
					return getTweets();
				}
				if(clientRequest != null && clientRequest.matches("show,friendships")) {
					return getFriendships();
				}
				if(clientRequest != null && clientRequest.matches("show,id")) {
					return getUsrTweets();
				}*/
				
				/*if(clientRequest != null && clientRequest.matches("insert")) {
		        	Scanner keyboard2 = new Scanner(System.in);
		            int dob2 = keyboard2.nextInt();
		            switch(dob2)
		            {
		            case 1:
		            	try {
		                	addTweet();
		                	keyboard2.close();
		        		} catch (IOException e) {
		        			System.out.println("No se ha realizado el INSERT");
		        			e.printStackTrace();
		        		}
		                break;
		            case 2:
		            	try {
		        			addUser();
		                	keyboard2.close();
		        		} catch (IOException e) {
		        			System.out.println("No se ha realizado el INSERT");
		        			e.printStackTrace();
		        		}
		            	break; 
		            }
		        }*/
			} catch(Exception e) {
				System.out.println("input process falied: " + e.getMessage());
				return "exit";
			}
 
			return reply;
		}
	}
	
	private static void getTweets() {
        TweetDAO tweetDao = new TweetDAO();
        List<Tweet> tweets;
        try {
        	System.out.println("-- tweets table --");
            tweets = tweetDao.getTweets();
            for (Tweet tweet : tweets) {
            	//displayTweet(tweet);
                System.out.println(tweet);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void getUsrTweets() {
        TweetDAO tweetDao = new TweetDAO();
        System.out.println("Introduzca el id del usuario del que quiera listar sus tweets: ");
        List<Tweet> tweets;
        Scanner keyboard5 = new Scanner(System.in);
        int user_id = keyboard5.nextInt();
        try {
        	System.out.println("-- tweets table for user id: " + user_id + "--");
            tweets = tweetDao.getUserTweets(user_id);
            for (Tweet tweet : tweets) {
            	//displayTweet(tweet);
                System.out.println(tweet);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        keyboard5.close();
    }
    
    private static void getFriendships() {
        FriendshipDAO friendshipDao = new FriendshipDAO();
        List<Friendship> friendships;
        try {
        	System.out.println("-- friendhip table --");
            friendships = friendshipDao.getFriendships();
            for (Friendship friendship : friendships) {
            	//displayFriendship(friendship);
                System.out.println(friendship);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static String getUsersServer() {
        UserDAO userDao = new UserDAO();
        List<User> users;
    	String res = "";
        try {
            users = userDao.getUsers();
            for (User user : users) {
                //displayUser(user);
            	res = res + user;
                //System.out.println(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    private static void addUser() throws IOException {
        UserDAO userDao = new UserDAO();     
        try {
        		userDao.insertUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void addTweet() throws IOException {
           TweetDAO tweetDao = new TweetDAO();     
           try {
           		tweetDao.insertTweet();
           } catch (SQLException e) {
               e.printStackTrace();
           }
    }
}