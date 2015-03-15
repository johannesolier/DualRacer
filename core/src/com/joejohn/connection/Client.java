package com.joejohn.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import static com.joejohn.connection.ServerPacket.ServerAction.*;
import static com.joejohn.connection.ClientPacket.ClientAction.*;


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
		return clients.size();
	}
	
	/**
	 * Sends object to server
	 * @param obj Object to be sent to server.
	 */
	public void send(Object obj) {
		if(server != null)
			server.send(obj);
	}
	
	/**
	 * Sends object to all other clients.
	 * Should be used during game.
	 * @param obj Object to be sent.
	 */
	public void sendAll(Object obj) {
		System.out.println("Sending information to: " + clients.size());
		for(Connection connection : clients) {
			connection.send(obj);
		}
	}
	
	/**
	 * Objects which is received are handled here.
	 * @param obj
	 */
	protected void receive(Object obj) {
		if(packetHandler == null) return;
		else if(obj instanceof ClientPacket)
			packetHandler.clientPacketHandler((ClientPacket) obj);
		else if(obj instanceof PlayerPacket)
			packetHandler.playerPacketHandler((PlayerPacket) obj);
		else if(obj instanceof LobbyPacket)
			packetHandler.lobbyPacketHandler((LobbyPacket) obj);
		else if(obj instanceof PeerPacket)
			connectClient((PeerPacket) obj);

	}
	
	/**
	 * Adds connection to connection list.
	 * @param client Connection to client.
	 */
	protected void addConnection(Connection client) {
		System.out.println("Connected to a player at " + client.getInetAddress());
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
		
		ServerConnection(Socket connection, Client client) {
			this.connection = connection;
			this.client = client;
		}
		
		/**
		 * Stop this thread.
		 */

		public void run() {
			try {
				// Fetches InputStream from connection
				InputStream serverInputStream = this.connection.getInputStream();
				// Fetches OutputStream from connect.
				OutputStream serverOutputStream = this.connection.getOutputStream();
				// Create ObjectOutputStream. Used to output objects
				this.oos = new ObjectOutputStream(serverOutputStream);
				//Create InputObjectStream. Used to get input objects.
				this.ois = new ObjectInputStream(serverInputStream);

				System.out.println("Connected to server on " + this.connection.getRemoteSocketAddress());
				// While-loop to ensure continuation of reading in-coming messages
				while (this.connection.isConnected()) {
					try {
						//Receive object from server
						Object obj = this.ois.readObject();
						if(obj instanceof ServerPacket) {
							ServerPacket packet = (ServerPacket)obj;
							if(packet.getAction() == CLOSE)
								break;
						}
						this.client.receive(obj);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					// Close all buffers and socket
					System.out.println("Closing connection to server.");
					this.ois.close();
					this.oos.close();
					connection.close();
					client.serverDisconnected();
				} catch(IOException e) {
					System.out.println("Client caught an exception trying to close server connection.");
				}
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
			if(server != null) return false;
			System.out.println("Trying to connect to server.");
			Socket serverConnection = new Socket();
			serverConnection.connect(new InetSocketAddress(Config.SERVERIP, Config.SERVERPORT), Config.TIMEOUT);

			server = new ServerConnection(serverConnection, this);
			this.serverConnection = serverConnection;
			server.start();
			return true;
		} catch(IOException e) {
			System.out.println("Couldn't connect to the server.");
			server = null;
			serverConnection = null;
			return false;
		}
	}

	public void connectClient(PeerPacket packet) {
		Connection peer = new Connection(packet.getInetAddress(), this);
		peer.start();
		addConnection(peer);
	}

	public void disconnectServer() {
		ServerPacket packet = new ServerPacket(CLOSE);
		this.send(packet);
	}

	protected void serverDisconnected() {
		server = null;
		serverConnection = null;
		System.out.println("Disconnected from server");
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
