package com.neeraj.client;

import java.io.DataInputStream;

public class ClientThread implements Runnable {

	/*
	 * DataInput stream to listen from the server
	 */
	private DataInputStream dataIS;

	/*
	 * Name of the User
	 */
	public static String id;

	/*
	 * Initializing the different element of the thread
	 */
	public ClientThread(DataInputStream dIS, String name) {
		this.dataIS = dIS;

	}

	/*
	 * 
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		while (true) {

			try {
				String message = dataIS.readUTF();
				System.out.println(message);
				if (message.contains("Wrong Password")) {
					dataIS.close();
					System.exit(0);
				} else if (message.contains("User already Active")) {
					dataIS.close();
					System.exit(0);
				}

			} catch (Exception e) {
				this.dataIS = null;
			}
		}
	}
}
