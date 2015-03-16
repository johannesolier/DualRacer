package com.joejohn.connection;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import static com.joejohn.connection.ClientPacket.ClientAction.*;

/**
 * Connection class:
 * 		Class used to continue communication to another client.
 */
public class Connection extends Thread {
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Client client;
	private boolean isRunning = true;
	
	Connection(Socket connection, Client client) {
		this.client = client;
		this.socket = connection;
	}
	
	Connection(InetAddress inet, Client client) {
		this.client = client;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(inet, Config.PORT), Config.TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			// Fetches InputStream from connection
			InputStream serverInputStream = this.socket.getInputStream();
			// Fetches OutputStream from connect
			OutputStream serverOutputStream = this.socket.getOutputStream();
			// Create ObjectOutputStream
			this.oos = new ObjectOutputStream(serverOutputStream);
			// Create InputObjectStream
			this.ois = new ObjectInputStream(serverInputStream);
			System.out.println("Peer-Peer Connection: Ready");
			// While-loop to ensure continuation of reading in-coming messages
			while (this.socket.isConnected() && isRunning) {
				try {
					//Receive object from client
					Object obj = this.ois.readObject();
					if(obj instanceof ClientPacket){
						ClientPacket packet = (ClientPacket)obj;
						if(packet.getAction() == CLOSE) {
							send(packet);
							break;
						}
					}
					this.client.receive(obj);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			// Close buffers and socket
			this.ois.close();
			this.oos.close();
			this.socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			client.removeConnection(this);
		}
	}
	
	/**
	 * Used to send given object parameter to the connection established.
	 * @param obj Object to be sent through socket connection.
	 */
	protected void send(Object obj) {
		try {
			this.oos.writeObject(obj);
			this.oos.flush();
		} catch (IOException e) {
			// Close buffers and socket
			Gdx.app.log("Connection", "IOException", e);
			try {
				this.ois.close();
				this.oos.close();
				this.socket.close();
			} catch (IOException e1) {
			} finally {
				client.removeConnection(this);
			}
		}
	}
	
	/**
	 * Get IP of socket established.
	 * @return
	 */
	public InetAddress getInetAddress() {
		return this.socket.getInetAddress();
	}

	/**
	 * Close established connection.
	 */
	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
