package com.Neeraj.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.Security;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.sun.net.ssl.internal.ssl.Provider;

/**
 * An implementation for the Server Creation
 * 
 * @author neeraj
 * @since 11-Feb-2016
 *
 */
public class Server {

	/*
	 * DataOutput stream to send data to the users
	 */
	private DataOutputStream dOS;
	/*
	 * DataInput stream to listen from the users
	 */
	private DataInputStream dIS;
	/*
	 * Server socket for the server
	 */
	private SSLServerSocketFactory sslServerSocketfactory;
	private SSLServerSocket sslServerSocket;
	private SSLSocket sslSocket;
	/*
	 * Server's port no
	 */
	private int intSSLport = 4443;
	int maxClient = 10;

	/*
	 * Thread for every player who will join the game Upto ten players can join
	 * a game
	 */
	private ArrayList<ServersThread> clientThreads = new ArrayList<ServersThread>();
	/*
	 * Array of players who can connect to the server
	 */
	private ArrayList<Users> client = new ArrayList<Users>();

	static public ArrayList<String> activeUsers = new ArrayList<String>();

	public Server() {
		Security.addProvider(new Provider());

		// Specifying the Keystore details
		System.setProperty("javax.net.ssl.keyStore", "testkeystore.ks");
		System.setProperty("javax.net.ssl.keyStorePassword", "testpwd");
//		System.setProperty("javax.net.debug", "all");
		initializeServer();
	}

	/*
	 * Listen to the port provided and on every connection Create a socket for
	 * the users and give them threads
	 */
	private void initializeServer() {
		try {
			// Registering the JSSE provider

			// Initialize the Server Socket
			sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(intSSLport);
			// Create Input / Output Streams for communication with the client

			// Enable debugging to view the handshake and communication which
			// happens between the SSLClient and the SSLServer
			// System.setProperty("javax.net.debug","all");
			while (true) {
				if (clientThreads.size() < maxClient) {
					sslSocket = (SSLSocket) sslServerSocket.accept();
					// socket = serverSocket.accept();
					System.out.println("Socket address : " + sslSocket.getInetAddress());
					dOS = new DataOutputStream(sslSocket.getOutputStream());
					dIS = new DataInputStream(sslSocket.getInputStream());
					clientThreads.add(new ServersThread(dOS, dIS, clientThreads, client));
					Thread thread = new Thread(clientThreads.get(clientThreads.size() - 1));
					thread.start();
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();

			this.dIS = null;
			this.dOS = null;
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Server ds = new Server();
	}
}
