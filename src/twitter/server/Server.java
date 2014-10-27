/*
 * Arranca el servidor y abre el socket a la espera de conexiones
 */

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
	final static int _transferPort = 44444;
	public static int umbral_push;
	private Socket _socket = null;
	private Socket _transfersocket = null;
	private PrintWriter _out = null;
	private BufferedReader _in = null;
	private ServerSocket serverSocket = null;
	
	/* 			 MATRIZ DE COSTES  €/BYTE
	 * 
	 * 
	 *		US		EU		AS		SAM		OCE		SERVER
	 *	US	{0,		6401,	10879,	7677,	15993,	1	 },		US = NEW YORK
	 *  EU	{6401,	0,		8945,	9890,	16090,	6401 },		EU = BERLIN
	 *  AS	{10879,	8945,	0,		18494,	7790,	10879},		AS = TOKYO
	 *  SAM	{7677,	9890,	18494,	0,		13629,	7677 },		SAM = RIO DE JANEIRO
	 *  OCE	{15993,	16090,	7790,	13629,	0,		15993},		OCE = SYDNEY
	 *  SRV {1,		6401,	10879,	7677,	15993,	0	 } };	SRV = NEW YORK
	 * 
	 * */
	public static int[][] costs_matrix= { {0,		6401,	10879,	7677,	15993,	1	 },
										  {6401,	0,		8945,	9890,	16090,	6401 },
										  {10879,	8945,	0,		18494,	7790,	10879},
										  {7677,	9890,	18494,	0,		13629,	7677 },
										  {15993,	16090,	7790,	13629,	0,		15993},
										  {1,		6401,	10879,	7677,	15993,	0	 } };
	
	public static void main(String[] args) {
		if (args.length > 0) {
		    try {
		    	String Twitter_url = "jdbc:mysql://"+args[0]+":3306/Twitter";
		        TwitterConnection.URL =Twitter_url;
		        TwitterConnection.USER = args[1];
		        TwitterConnection.PASSWORD = args[2];
		        Server.umbral_push = Integer.parseInt(args[3]);
		        
		    } catch (Exception e) {
		        System.err.println("Server.jar Twiter_IP User Password Push_threshold");
		    }
		}
		else{
			System.err.println("Server.jar Twiter_IP User Password Push_threshold");
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