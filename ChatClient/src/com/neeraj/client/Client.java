package com.neeraj.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.net.ssl.internal.ssl.Provider;
public class Client {
	
	/*
	 * User Socket Connections
	 */

	private SSLSocketFactory sslsocketfactory;
	private SSLSocket sslSocket;
	/*
	 * DataOutput stream to send data to the server
	 */
	private DataOutputStream dOS;
	/*
	 * DataInput stream to listen from the server
	 */
	private DataInputStream dIS;
	/*
	 * IP address of the server
	 */
	private String ip = "127.0.0.1";
	
	Scanner scanner;
	/*
	 * User name
	 */
	
	private String userName;
	/*
	 * User's password
	 */
	
	private String passwd;
	/*
	 * message
	 */
	
	private String message;
	/*
	 * Initializing the constructor to start the game with getting ip and username from the user
	 */
	public Client() {
		scanner = new Scanner(System.in);
		while (ip.isEmpty() || userName == null) {
			System.out.print("Enter the IP of the Server:");
			ip=scanner.nextLine();
			System.out.print("Enter your UserName:");
			userName=scanner.nextLine();
			System.out.print("Enter your Password:");
			passwd=scanner.nextLine();
		}
		while (!ip.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+") && !ip.equals("localhost") && !ip.equals("lh")) {
			System.out.println("Wrong IP Format\n Try Again");
			System.out.print("Enter the IP of the Server:");
			ip=scanner.nextLine();
		}while(userName.matches(".*\\s+.*") || userName.equals("")){
			System.out.println("Name Cannot contain spaces");
			System.out.print("Enter your UserName:");
			userName=scanner.nextLine();
		}while(passwd.matches(".*\\s+.*") || passwd.equals("")){
			System.out.println("Password Cannot contain spaces");
			System.out.print("Enter your Password:");
			passwd=scanner.nextLine();
		}
		if(ip.equals("localhost") || ip.equals("lh")){
			ip="127.0.0.1";
		}
		connect();
	}
	/*
	 * Connects to the server with the ip and start the thread for conversation
	 */
	private void connect() {
		try {
			Security.addProvider(new Provider());
			System.setProperty("javax.net.ssl.trustStore","client.ks");
		//	System.setProperty("javax.net.debug","all");
			 sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			 sslSocket = (SSLSocket)sslsocketfactory.createSocket(ip,4443);

			dOS = new DataOutputStream(sslSocket.getOutputStream());
			dIS = new DataInputStream(sslSocket.getInputStream());
			ClientThread ct = new ClientThread(dIS, userName);
			Thread thread = new Thread(ct);
			thread.start();
			dOS.writeUTF("NAME/" + userName + "/PASSWORD/"+passwd);

			while(true){
				message=scanner.nextLine();
				while(message.length()>=100){
					System.out.println("Message Cannot be greater than 100 characters");
					System.out.println("Enter the message");
					message=scanner.nextLine();

				}
				
				if(!message.equals("") && !message.equals("/logout"))
					dOS.writeUTF(message);
				if(message.equals("/logout")){
					System.out.println("Logged out Successfully");
					dOS.writeUTF(message);
					sslSocket.close();
					System.exit(0);
				}
				

			}
		} catch (IOException e) {
		//	e.printStackTrace();

			this.dIS = null;
			this.dOS = null;
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Client jackClient = new Client();
	}

}

