package com.joejohn.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;


public class Client extends Thread {

	private static Client client;

	private ServerConnection server;
	private ArrayList<Connection> clients;
	private ClientPeer peerConnection;
	private Socket serverConnection;
	private PacketHandler packetHandler;

	private boolean inLobby = false;
	private int lobbyID = 0;

	private Client() {
		clients = new ArrayList<Connection>();
	}

	public static Client getInstance() {
		if(client == null) {
			client = new Client();
			client.start();
		}
		return client;
	}


	public void run() {
		System.out.println("Client thread started.");
		try {
			String ip = Config.getDottedDecimalIP(Config.getLocalIPAddress());
			ServerSocket peerSocket = new ServerSocket(Config.PORT, 50, InetAddress.getByName(ip));
			this.peerConnection = new ClientPeer(peerSocket, this);
			this.peerConnection.start();
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
		if(packetHandler == null) return;
		else if(obj instanceof PlayerPacket)
			packetHandler.playerPacketHandler((PlayerPacket) obj);
		else if(obj instanceof LobbyPacket)
			packetHandler.lobbyPacketHandler((LobbyPacket) obj);
		else if(obj instanceof PeerPacket)
			packetHandler.peerPacketHandler((PeerPacket) obj);

	}
	
	/**
	 * Adds connection to connection list.
	 * @param client Connection to client.
	 */
	protected void addConnection(Connection client) {
		this.clients.add(client);
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

	public boolean connectServer() {
		try {
			System.out.println("Trying to connect to server.");
			Socket serverConnection = new Socket();
			serverConnection.connect(new InetSocketAddress(Config.SERVERIP, Config.SERVERPORT), Config.TIMEOUT);

			this.server = new ServerConnection(serverConnection, this);
			this.server.start();
			this.serverConnection = serverConnection;
			return true;
		} catch(IOException e) {
			System.out.println("Couldn't connect to the server.");
			this.server = null;
			this.serverConnection = null;
			return false;
		}
	}

	public void disconnectServer() {
		server.closeConnection();
		server = null;
		serverConnection = null;
	}

	public boolean isConnectedServer() {
		if(serverConnection != null) {
			return serverConnection.isConnected();
		}
		return false;
	}

/*	private void lobbyPacketHandler(LobbyPacket packet) {
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
	}*/

	public void setPacketHandler(PacketHandler handler) {
		this.packetHandler = handler;
	}

	public static void main(String[] args) {
		new Client().start();
	}
}
