package com.joejohn.connection;

import java.io.Serializable;
import java.util.ArrayList;

public class GameLobby implements Serializable {

	public final int MAX_PLAYERS = 2;

	private Server.ClientConnection host;
	private Server server;
	private ArrayList<Server.ClientConnection> players;
	private int id;

	public GameLobby(Server.ClientConnection host, Server server, int id) {
		this.host = host;
		this.server = server;
		this.id = id;
		players.add(host);
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public boolean addPlayer(Server.ClientConnection player) {
		if(getNumberOfPlayers() < MAX_PLAYERS) {
			players.add(player);
			return true;
		}
		return false;
	}

	public void removePlayer(Server.ClientConnection player) {
		if(player.equals(host)) {
			if(getNumberOfPlayers() > 1) {
				for(Server.ClientConnection other : players) {
					if(!other.equals(player)) {
						host = other;
						players.remove(player);
					}
				}
			} else {
				closeLobby();
			}
		}
	}

	private void closeLobby() {
		server.removeGameLobby(this);
	}

	public int getID() {
		return id;
	}

	protected boolean verifyHost(Server.ClientConnection player) {
		return host.equals(player);
	}

	protected void startGame() {

	}

}
