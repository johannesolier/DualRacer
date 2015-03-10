package com.joejohn.connection;

import static com.joejohn.connection.ServerPacket.ServerAction.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private int id = 0;
	private ArrayList<ClientConnection> clients;
	private ArrayList<GameLobby> lobbies;

	public Server() {
		clients = new ArrayList<ClientConnection>();
		lobbies = new ArrayList<GameLobby>();
	}

	/**
	 * Starts the server
	 */
	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(Config.SERVERPORT, 50,
					InetAddress.getByName(Config.SERVERIP));
			// Printing IP:Port for the server
			System.out.println("Waiting for connections on " + Config.SERVERIP
					+ " : " + Config.SERVERPORT);
			Socket newConnectionSocket;
			while (true) {
				// Accepts a in-coming connection
				newConnectionSocket = serverSocket.accept();;
				ClientConnection client = new ClientConnection(
						newConnectionSocket, this);
				client.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adding the given ClientConnection to the list of connections.
	 * @param client ClientConnection to be removed.
	 */
	public void addClientConnection(ClientConnection client) {
		System.out.println("Player connected to the server.");
		clients.add(client);
		System.out.println("Number of players on server: " + getNumberOfPlayers());
	}

	/**
	 * Removing the given ClientConnection from list of connections
	 * if it is present.
	 * @param client ClientConnection to be removed.
	 */
	public void removeClientConnection(ClientConnection client) {
		System.out.println("Player disconnected from the server.");
		clients.remove(client);
		System.out.println("Number of players on server: " + getNumberOfPlayers());
	}
	
	/**
	 * Get the number of players present at server.
	 * @return
	 */
	private int getNumberOfPlayers() {
		return clients.size();
	}

	/**
	 * Sends object to all present connections.
	 * @param obj Object sent to all connections.
	 */
	public void sendAll(Object obj) {
		for(ClientConnection client : clients) {
			if(client != null)
				client.send(obj);
		}
	}
	
	/**
	 * Sends object to the connection at given index.
	 * @param obj Object to be sent to specified connection.
	 * @param index Index to specified connection.
	 */
	public void send(Object obj, int index) {
		ClientConnection client = clients.get(index);
		if(client != null)
			client.send(obj);
	}

	/**
	 * Sends object to the given ClientConnection.
	 * @param obj Object to be sent.
	 * @param client ClientConnection object shall be sent to.
	 */
	public void send(Object obj, ClientConnection client) {
		client.send(obj);
	}

	/**
	 * This method receives object from whatever socket and
	 * handles it properly here.
	 * @param obj Object received through sockets.
	 * @param client ClientConnection it received the obj from.
	 */
	protected void receive(Object obj, ClientConnection client) {
		if(obj instanceof LobbyPacket)
			lobbyPacketHandler((LobbyPacket)obj, client);
	}

	/**
	 *  Thread class for handling further connection with client when connection
	 *  is established
	 */
	class ClientConnection extends Thread {
		private Socket connection;
		private Server server;
		public ObjectOutputStream oos;
		private ObjectInputStream ois;

		ClientConnection(Socket connection, Server server) {
			this.connection = connection;
			this.server = server;
		}

		/**
		 * Sends object through the socket connection.
		 * @param obj
		 */
		private void send(Object obj) {
			try {
				oos.writeObject(obj);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			System.out.println("Connected to client on "
					+ this.connection.getRemoteSocketAddress());
			InputStream clientInputStream;
			try {
				// Fetches InputStream from connection
				clientInputStream = this.connection.getInputStream();
				// Fetches OutputStream from connect
				OutputStream clientOutputStream = this.connection
						.getOutputStream();

				// Create ObjectOutputStream
				oos = new ObjectOutputStream(clientOutputStream);
				// Create InputObjectStream
				ois = new ObjectInputStream(clientInputStream);
				// Adding newly created connection to server list.
				this.server.addClientConnection(this);
				while (this.connection.isConnected()) {
					try {
						// Receive object from client
						Object obj = this.ois.readObject();
						if(obj instanceof ServerPacket) {
							ServerPacket packet = (ServerPacket)obj;
							if(packet.getAction() == CLOSE) {
								this.send(packet);
								break;
							}
						}
						this.server.receive(obj, this);
					} catch (IOException e) {
						break;
					} catch (ClassNotFoundException e) {
						break;
					}
				}
			} catch (IOException e1) {
				System.out.println("A player disconnected from the server.");
			} finally {
				try {
					ois.close();
					oos.close();
					connection.close();
					removeClientConnection(this);
				} catch(IOException e) {

				}
			}

		}
	}

	public void lobbyPacketHandler(LobbyPacket packet, ClientConnection client) {
		switch(packet.getLobbyAction()) {
			case JOIN:
				if(joinGameLobby(packet.getValue(), client))
					nothing();
				else
					nothing();
				break;
			case LEAVE:
				leaveGameLobby(packet.getValue(), client);
				break;
			case START:
				startGameLobby(packet.getValue(), client);
				break;
			case CREATE:
				int id = createGameLobby(client);
				LobbyPacket returnPacket = new LobbyPacket(LobbyAction.CREATE, id);
				client.send(returnPacket);
				break;
			default:
				break;
		}
	}

	private boolean joinGameLobby(int id, ClientConnection player) {
		GameLobby lobby = getGameLobbyById(id);
		return lobby.addPlayer(player);
	}

	private GameLobby getGameLobbyById(int id) {
		for(GameLobby lobby : lobbies) {
			if(lobby.getID() == id)
				return lobby;
		}
		return null;
	}

	private void startGameLobby(int id, ClientConnection client) {
		GameLobby lobby = getGameLobbyById(id);
		if(lobby != null) {
			if(lobby.verifyHost(client))
				lobby.startGame();
		}
	}

	private void leaveGameLobby(int id, ClientConnection client) {
		GameLobby lobby = getGameLobbyById(id);
		lobby.removePlayer(client);
	}

	protected void removeGameLobby(GameLobby lobby) {
		lobbies.remove(lobby);
	}

	private int createGameLobby(ClientConnection host) {
		GameLobby lobby = new GameLobby(host, this, ++id);
		lobbies.add(lobby);
		return id;
	}

	public static void main(String[] args) {
		new Server().startServer();
	}

	private void nothing() {

	}

}
