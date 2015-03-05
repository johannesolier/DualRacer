package com.joejohn.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Client extends Thread {
	/** Peer-to-peer client **/
	private ServerConnection server;
	private ArrayList<Connection> clients;
	private ClientPeer peerConnection;
	private Socket serverConnection;
	private boolean inLobby = false;
	private int lobbyID = 0;

	public Client() {
		clients = new ArrayList<Connection>();
	}


	public void run() {
		System.out.println("Trying to connect to server!");
		Socket serverConnection;
		try {
			String androidIp = Config.ANDROIDIP;
			serverConnection = new Socket(Config.SERVERIP, Config.SERVERPORT);
			ServerSocket peerSocket = new ServerSocket(Config.ANDROIDPORT, 50, InetAddress.getByName(androidIp));
			this.peerConnection = new ClientPeer(peerSocket, this);
			this.peerConnection.start();
			this.server = new ServerConnection(serverConnection, this);
			this.server.start();
			this.serverConnection = serverConnection;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public int getNumberOfConnections(){
		return this.clients.size();
	}
	
	/**
	 * Sends object to server
	 * @param obj Object to be sent to server.
	 */
	protected void send(Object obj) {
		this.server.send(obj);
	}
	
	/**
	 * Sends object to all other clients.
	 * Should be used during game.
	 * @param obj Object to be sent.
	 */
	protected void sendAll(Object obj) {
		System.out.println("Sending information to: " +this.clients.size());
		for(Connection connection : this.clients) {
			connection.send(obj);
		}
	}
	
	/**
	 * Objects which is received are handled here.
	 * @param obj
	 */
	protected void receive(Object obj) {
		if(obj instanceof LobbyPacket)
			lobbyPacketHandler((LobbyPacket)obj);

	}
	
	/**
	 * Adds connection to connection list.
	 * @param client Connection to client.
	 */
	protected void addConnection(Connection client) {
		this.clients.add(client);
	}

	/**
	 * Close server connection.
	 */
	public void closeServerConnection() {
		server.closeConnection();
		server = null;
	}

	/**
	 * Connection with the server
	 */
	class ServerConnection extends Thread {

		private Socket connection;
		private Client client;
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		boolean isRunning = true;
		
		ServerConnection(Socket connection, Client client) {
			this.connection = connection;
			this.client = client;
		}
		
		/**
		 * Stop this thread.
		 */
		protected void closeConnection() {
			isRunning = false;
		}


		public void run() {
			System.out.println("Connected to server on " + this.connection.getRemoteSocketAddress());
			try {
				// Fetches InputStream from connection
				InputStream serverInputStream = this.connection.getInputStream();
				// Fetches OutputStream from connect.
				OutputStream serverOutputStream = this.connection.getOutputStream();
				// Create ObjectOutputStream. Used to output objects
				this.oos = new ObjectOutputStream(serverOutputStream);
				//Create InputObjectStream. Used to get input objects.
				this.ois = new ObjectInputStream(serverInputStream);

				System.out.println("ServerConnection: Ready");
				// While-loop to ensure continuation of reading in-coming messages
				while (this.connection.isConnected() && isRunning) {
					try {
						//Receive object from server
						Object obj = this.ois.readObject();
						this.client.receive(obj);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

				// Close all buffers and socket
				this.ois.close();
				this.oos.close();
				serverOutputStream.close();
				serverInputStream.close();
				connection.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		/**
		 * Sending given object to server
		 * @param obj Object to be sent.
		 */
		protected void send(Object obj) {
			try {
				oos.writeObject(obj);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void lobbyPacketHandler(LobbyPacket packet) {
		switch(packet.getLobbyAction()) {
			case CREATE:
				if(packet.getValue() > 0) {
					inLobby = true;
					lobbyID = packet.getValue();
				}
				break;
			case JOIN:
				if(packet.getValue() > 0) {
					inLobby =  true;
					lobbyID = packet.getValue();
				}
				break;
			case LEAVE:
				break;
			case START:
				break;
			default:
				break;
		}
	}

	protected void requestGameLobby() {
		if(serverConnection.isConnected() && !inLobby) {
			LobbyPacket packet = new LobbyPacket(LobbyAction.CREATE);
			send(packet);
		}
	}


	public static void main(String[] args) {
		new Client().start();
	}
}
