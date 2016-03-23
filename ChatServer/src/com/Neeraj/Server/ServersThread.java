package com.Neeraj.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * An implementation for the Server threads for users
 * 
 * @author neeraj
 * @since 13-Feb-2016
 *
 */
public class ServersThread implements Runnable {
	/*
	 * DataOutput stream to send data to the users
	 */
	private DataOutputStream dataOS;
	/*
	 * DataInput stream to listen from the users
	 */
	private DataInputStream dataIS;
	/*
	 * Array of deck for the groups
	 */
	private ArrayList<ServersThread> clientT = new ArrayList<ServersThread>();
	/*
	 * user name of the user connected to this thread
	 */
	private String clientName;
	/*
	 * user password
	 */
	private String clientPasswd;

	/*
	 * Array of players who can connect to the server
	 */
	private ArrayList<Users> client = new ArrayList<Users>();

	public ServersThread(DataOutputStream dOS, DataInputStream dIS, ArrayList<ServersThread> clientThreads,
			ArrayList<Users> client) {
		this.dataOS = dOS;
		this.dataIS = dIS;
		this.clientT = clientThreads;
		this.client = client;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				
				String message = dataIS.readUTF();
				System.out.println(message);
				String[] tokens = message.split("/");
				if (tokens[0].equals("NAME")) {
					clientName = tokens[1];
					clientPasswd = tokens[3];
					System.out.println(clientName);
					for (int i = 0; i < clientT.size(); i++) {
						if (clientT.get(i) != null) {
							Users user = new Users();
							client.add(user);
							if (user.checkUser(clientName)) {
								if (user.checkUsersID(clientName, clientPasswd)) {
									if (!Server.activeUsers.contains(clientName)) {
										Server.activeUsers.add(clientName);
									} else {
										int index = clientT.indexOf(this);
										clientT.get(index).dataOS.writeUTF("User already Active");
										client.remove(user);
										clientT.remove(index);
										this.dataOS.close();
										this.dataIS.close();
										return;
									}
								} else {
									int index = clientT.indexOf(this);

									clientT.get(index).dataOS.writeUTF("Wrong Password");
									client.remove(user);
									clientT.remove(index);
									this.dataOS.close();
									this.dataIS.close();
									return;
								}

							} else {

								user.addUser(clientName, clientPasswd);
								Server.activeUsers.add(clientName);

							}
							int index = clientT.indexOf(this);
							clientT.get(index).dataOS.writeUTF("Login Successfully");
							String listOfUsers = "";
							for (int j = 0; j < clientT.size(); j++) {
								listOfUsers += "\n" + Server.activeUsers.get(j).toString();
							}
							clientT.get(index).dataOS.writeUTF("Users in session" + listOfUsers);
							for (int j = 0; j < clientT.size(); j++) {
								if (j != index) {
									clientT.get(j).dataOS.writeUTF(clientName + " Logged In");
								}

							}
							break;

						}
					}
				} else if (message.contains("/logout")) {
					int index = clientT.indexOf(this);
					for (int i = 0; i < client.size(); i++) {
						if (i != index) {
							clientT.get(i).dataOS.writeUTF(clientName + " Exited");
						}

					}
					System.out.println(clientT.size());
					clientT.remove(index);
					System.out.println(clientT.size());
					client.remove(index);
					Server.activeUsers.remove(clientName);
					return;
				} else if (message.contains("/users")) {
					String listOfUsers = "";
					for (int i = 0; i < clientT.size(); i++) {
						listOfUsers += "\n" + Server.activeUsers.get(i).toString();
					}
					int index = clientT.indexOf(this);
					clientT.get(index).dataOS.writeUTF("List of Active Users" + listOfUsers);

				} else {
					int index = clientT.indexOf(this);
					for (int i = 0; i < client.size(); i++) {
						if (i != index) {
							clientT.get(i).dataOS.writeUTF(clientName + ": " + message);
						}
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();

				int index = clientT.indexOf(this);
				if (index >= 0) {
					try {
						for (int i = 0; i < clientT.size(); i++) {
							if (i != index)
								clientT.get(i).dataOS.writeUTF(clientName + " Exited");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					clientT.remove(index);

					Server.activeUsers.remove(clientName);

				}
				this.dataOS = null;
				this.dataIS = null;
				return;
			}

		}
	}

}
