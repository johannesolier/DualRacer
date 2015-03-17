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
		if(getNumberOfPlayers() > 0) {
			for(Server.ClientConnection c : players) {
				if(!c.getReady())
					return false;
			}
			return true;
		}

		return false;
	}

	private void closeLobby() {
		server.removeGameLobby(this);
	}

	public int getID() {
		return id;
	}

	private void sendAll(Object obj) {
		for(Server.ClientConnection c : players) {
			c.send(obj);
		}
	}

	protected void startGame() {
		System.out.println("Starting game on lobby " + String.valueOf(id));

		System.out.println("Number of players in lobby: " + getNumberOfPlayers());

		int numOfPlayers = getNumberOfPlayers();

/*		for(int i = 0; i < numOfPlayers - 1; i++) {
			PeerPacket packet = new PeerPacket(players.get(i).getInetAddress(), Config.PORT);
			int j = i + 1;
			while(j < numOfPlayers) {
				players.get(j).send(packet);
				j++;
			}
		}*/



		if(numOfPlayers == 2) {
			Server.ClientConnection player1 = players.get(0);
			Server.ClientConnection player2 = players.get(1);
			if(player1 == null || player2 == null) {
				System.out.println("BUG");
				return;
			}

			PeerPacket packet = new PeerPacket(player1.getInetAddress(), Config.PORT);
			player2.send(packet);
		}

		/*
		for(int i = 0; i < numOfPlayers; i++) {
			if(i == numOfPlayers - 1) {
				break;
			}
			for(int j = i + 1; j < numOfPlayers; j++) {
				PeerPacket packet = new PeerPacket(players.get(i).getInetAddress(), Config.PORT);
				players.get(j).send(packet);
				j++;
			}
		}
		*/

		int[] levelSelections = new int[numOfPlayers];
		for(int i = 0; i < numOfPlayers; i++) {
			levelSelections[i] = players.get(i).getLevelSelection();
		}

		int levelSelected = levelSelections[server.rand.nextInt(numOfPlayers)];

		System.out.println("Level Selected: " + levelSelected);

		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
		}

		LobbyPacket packet = new LobbyPacket(LobbyPacket.LobbyAction.START, levelSelected);
		sendAll(packet);

		server.removeGameLobby(this);
		/*
		try {
			Thread.sleep(3000);
		} catch(InterruptedException e) {

		}
		ServerPacket pkt = new ServerPacket(ServerPacket.ServerAction.CLOSE);
		sendAll(pkt);

		try {
			Thread.sleep(6000);
		} catch(InterruptedException e) {

		}

		for(Server.ClientConnection player : players) {
			server.removeClientConnection2(player);
		}

		*/

	}

}
