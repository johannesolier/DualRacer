package com.joejohn.connection;

import java.io.Serializable;
import java.util.ArrayList;

public class GameLobby implements Serializable {

	private Server server;
	private ArrayList<Server.ClientConnection> players;
	private int id;

	public GameLobby(Server server, int id) {
		this.server = server;
		this.id = id;
		players = new ArrayList<Server.ClientConnection>();
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public boolean addPlayer(Server.ClientConnection player) {
		if(getNumberOfPlayers() < Config.MAX_PLAYERS) {
			players.add(player);
			return true;
		}
		return false;
	}

	public void removePlayer(Server.ClientConnection player) {
		if(getNumberOfPlayers() > 1) {
			for(int i = 0; i < players.size(); i++) {
				if(players.get(i).equals(player)) {
					players.remove(i);
				}
			}
		} else {
			if(players.get(0).equals(player)) {
				players.remove(0);
				closeLobby();
			}
		}
	}

	public boolean isReady() {
		if(players.size() > Config.MAX_PLAYERS / 2)
		for(Server.ClientConnection c : players) {
			if(!c.getReady())
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

	private void send(Object obj) {
		for(Server.ClientConnection c : players) {
			c.send(obj);
		}
	}

	protected void startGame() {
		System.out.println("Starting game on lobby " + String.valueOf(id));

		for(int i = 0; i < players.size() - 1; i++) {
			PeerPacket packet = new PeerPacket(players.get(i).getInetAddress(), Config.PORT);
			int j = i + 1;
			while(j < players.size()) {
				players.get(j).send(packet);
				j++;
			}
		}

		try {
			Thread.sleep(2000);
		} catch(InterruptedException e) {
		}

		LobbyPacket packet = new LobbyPacket(LobbyPacket.LobbyAction.START);
		send(packet);

	}

}
