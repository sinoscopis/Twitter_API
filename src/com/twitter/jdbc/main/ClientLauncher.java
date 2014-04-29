package com.twitter.jdbc.main;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
 
public class ClientLauncher {
	public static void main(String[] args) throws IOException {
	
		int clientes = 15;
		int usr_num;
		
		for(int i=1; i<=clientes; i++){
			String name = randomIdentifier();
			System.out.println(name);
			Client cliente = new Client();
			cliente.startRandomClient(name);
		}
		usr_num = usernumber();
		
		randomizeFriendships(usr_num);
		
		while (true){
			
		}
	}
	

	private static void friend2(int usr_req, int usr_acc) throws IOException {
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		InetAddress host = null;
 
		try {
			host = InetAddress.getLocalHost();
			socket = new Socket(host.getHostName(), 5559);
 
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
			String fromServer;
			String fromUser = null;
 
			//Read from socket and write back the response to server. 
			while ((fromServer = in.readLine()) != null) {
				System.out.println("Server - " + fromServer);
				if (fromServer.equals("exit"))
					break;
				if (fromServer.equals("friends")){
					break;
				}
				fromUser = "insertfriendship,"+usr_req+","+ usr_acc;
				if (fromUser != null) {
					System.out.println("Client - " + fromUser);
					out.println(fromUser);
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + host.getHostName());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't read/write from the connection: " + e.getMessage());
			System.exit(1);
		} finally { //Make sure we always clean up
			out.close();
			in.close();
			socket.close();
		}
	}

	public static String randomIdentifier(){
		final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
		final java.util.Random rand = new java.util.Random();
		// consider using a Map<String,Boolean> to say whether the identifier is being used or not 
		final Set<String> identifiers = new HashSet<String>();
			StringBuilder builder = new StringBuilder();
		    while(builder.toString().length() == 0) {
		        int length = rand.nextInt(5)+5;
		        for(int i = 0; i < length; i++)
		            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
		        if(identifiers.contains(builder.toString())) 
		            builder = new StringBuilder();
		    }
		    return builder.toString();
	}
	
	public static int usernumber() throws IOException {
		 
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		InetAddress host = null;
		int usersnumber = 0;
 
		try {
			host = InetAddress.getLocalHost();
			socket = new Socket(host.getHostName(), 5559);
 
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
			String fromServer;
			String fromUser = null;
 
			//Read from socket and write back the response to server. 
			while ((fromServer = in.readLine()) != null) {
				System.out.println("Server - " + fromServer);
				if (fromServer.equals("......")){
					fromUser = "countusers";
					if (fromUser != null) {
						System.out.println("Client - " + fromUser);
						out.println(fromUser);
					}
				}
				else if (Integer.valueOf(fromServer) > 0){
					usersnumber =  Integer.valueOf(fromServer);
					break;
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + host.getHostName());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't read/write from the connection: " + e.getMessage());
			System.exit(1);
		} finally { //Make sure we always clean up
			out.close();
			in.close();
			socket.close();
		}
		return usersnumber;
	}

	private static void randomizeFriendships(int usr_num) throws IOException{
		for(int i=1; i<=(usr_num/2); i++){
			
			double randNumber1 = Math.random();
			double d1 = randNumber1 * usr_num;
			int randomUsr1 = (int)d1;
			
			double randNumber2 = Math.random();
			double d2 = randNumber2 * usr_num;
			int randomUsr2 = (int)d2;
			
			if (randomUsr1 != randomUsr2)
				friend2(randomUsr1,randomUsr2);
		}
	}
}