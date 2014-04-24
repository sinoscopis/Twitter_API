package com.twitter.jdbc.main;

import java.io.*;
import java.util.*;
 
public class ClientLauncher {
	public static void main(String[] args) throws IOException {
	
		int clientes = 15;
		
		for(int i=1; i<=clientes; i++){
			String name = randomIdentifier();
			System.out.println(name);
			Client cliente = new Client();
			cliente.startRandomClient(name);
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
}