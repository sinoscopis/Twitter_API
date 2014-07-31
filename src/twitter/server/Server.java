package twitter.server;

import java.net.*;
import java.io.*;

import twitter.db.TwitterConnection;

 
/**
 * Demo Server: Contains a multi-threaded socket server sample code.
 */
public class Server extends Thread
{
	final static int _portNumber = 55555; //Arbitrary port number
	final static int _transferPort = 55055;
	private Socket _socket = null;
	private Socket _transfersocket = null;
	private PrintWriter _out = null;
	private BufferedReader _in = null;
	private ServerSocket serverSocket = null;
	
	/* 			 MATRIZ DE COSTES  €/BYTE
	 * 
	 * 
	 *				US		EU		AS		SAM		OCE		SERVER
	 *		US		0		1		2		3		4		0
	 *  	EU		1		0		2		3		4		1
	 *  	AS		2		2		0		3		3		2
	 * 		SAM		3		3		3		0		4		3						
	 * 		OCE		4		4		3		4		0		4
	 * 		SERVER	0		1		2		3		4		0
	 * 
	 * */
	public static int[][] costs_matrix= { {0,2,3,4,5,1},
										  {2,0,3,4,5,2},
										  {3,3,0,4,4,3},
										  {4,4,4,0,5,4},
										  {5,5,4,5,0,5},
										  {1,2,3,4,5,0} };
	
	public static void main(String[] args) {
		if (args.length > 0) {
		    try {
		    	String Twitter_url = "jdbc:mysql://"+args[0]+":3306/Twitter";
		        TwitterConnection.URL =Twitter_url;
		        TwitterConnection.USER = args[1];
		        TwitterConnection.PASSWORD = args[2];
		    } catch (Exception e) {
		        System.err.println("Server.jar Twiter_IP User Password");
		    }
		}
		else{
			System.err.println("Server.jar Twiter_IP User Password");
			System.exit(1);
		}
		new Server().run();
	}
	public void run() {
		
		try {
			 serverSocket = new ServerSocket(_portNumber);
		
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + _portNumber);
			System.exit(-1);
		}
		
		try {
			while(true){
				
				_socket=serverSocket.accept();
				Runnable connectionRequesthandler = new ConnectionRequestHandler(_socket);
				new Thread(connectionRequesthandler).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally { //In case anything goes wrong we need to close our I/O streams and sockets.
			try {
				_out.close();
				_in.close();
				_socket.close();
				_transfersocket.close();
			} catch(Exception e) { 
				System.out.println("Couldn't close I/O streams");
			}
		}
	}
}