package com.joejohn.connection;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Establishes connection to another clients by listening on
 * an ServerScokcet.
 *
 */
public class ClientPeer extends Thread {
	
	private Client client;
	private ServerSocket peerSocket;

	public ClientPeer(ServerSocket peerSocket, Client client) {
		this.client = client;
		this.peerSocket = peerSocket;
	}
	
	@Override
	public void run() {
		try {
			// Listen to ServerSocket and accept in-coming connections.
			Socket clientPeer;
			Gdx.app.log("ClientPeer", "Listening on " +
					peerSocket.getInetAddress().getHostAddress()
					+ ":" + getPort());
			while (true) {
				clientPeer = peerSocket.accept();
				Gdx.app.log("ClientPeer", "Incoming connection.");
				Connection clientConnection = new Connection(clientPeer, this.client);
				
				clientConnection.start();
				this.client.addConnection(clientConnection);
			}
		} catch (IOException e) {
			Gdx.app.log("ClientPeer", "IOException", e);
		}
	}
	
	
	/**
	 * Get InetAddress of ServerSocket.
	 * @return
	 */
	public InetAddress getInetAddress() {
		return this.peerSocket.getInetAddress();
	}
	
	/**
	 * Get port number of ServerSocket.
	 * @return
	 */
	public int getPort() {
		return this.peerSocket.getLocalPort();
	}

}
