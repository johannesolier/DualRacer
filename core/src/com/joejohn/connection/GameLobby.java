package com.joejohn.connection;

import java.io.Serializable;
import java.util.ArrayList;

public class GameLobby implements Serializable {

	private Server server;
	private ArrayList<Server.ClientConnection> players;
	private ArrayList<Boolean> isReady;
	private int id;

	public GameLobby(Server server, int id) {
		this.server = server;
		this.id = id;
		players = new ArrayList<Server.ClientConnection>();
		isReady = new ArrayList<Boolean>();
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public boolean addPlayer(Server.ClientConnection player) {
		if(getNumberOfPlayers() < Config.MAX_PLAYERS) {
			players.add(player);
			isReady.add(false);
			return true;
		}
		return false;
	}

	public void removePlayer(Server.ClientConnection player) {
		if(getNumberOfPlayers() > 1) {
			for(int i = 0; i < players.size(); i++) {
				if(players.get(i).equals(player)) {
					players.remove(i);
					isReady.remove(i);
				}
			}
		} else {
			if(players.get(0).equals(player)) {
				players.remove(0);
				isReady.remove(0);
				closeLobby();
			}
		}
	}

	public boolean isReady() {
		if(players.size() > Config.MAX_PLAYERS / 2)
		for(Boolean b : isReady) {
			if(!b)
				return false;
		}
		return true;
	}

	private void closeLobby() {
		server.removeGameLobby(this);
	}

	public int getID() {
		return id;
	}

	protected void startGame() {

	}

}
