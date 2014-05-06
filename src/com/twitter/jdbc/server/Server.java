package com.twitter.jdbc.server;

import java.net.*;
import java.io.*;

 
/**
 * Demo Server: Contains a multi-threaded socket server sample code.
 */
public class Server extends Thread
{
	final static int _portNumber = 55555; //Arbitrary port number
	private Socket _socket = null;
	private PrintWriter _out = null;
	private BufferedReader _in = null;
	private ServerSocket serverSocket = null;

	public static void main(String[] args) {
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
			} catch(Exception e) { 
				System.out.println("Couldn't close I/O streams");
			}
		}
	}
}