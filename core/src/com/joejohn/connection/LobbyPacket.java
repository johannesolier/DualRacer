package com.joejohn.connection;

import java.io.Serializable;

public class LobbyPacket implements Serializable {

    public enum LobbyAction {
        JOIN,
        LEAVE,
        START,
        CREATE,
        REFRESH,
        LOBBY,
        READY,
        NOT_READY
    }

    private LobbyAction lobbyAction;
    private int value;
    private int players;

    public LobbyPacket(LobbyAction action, int value) {
        lobbyAction = action;
        this.value = value;
        this.players = -1;
    }

    public LobbyPacket(LobbyAction action) {
        lobbyAction = action;
        value = -1;
        players = -1;
    }

    public LobbyPacket(LobbyAction action, int value, int players) {
        lobbyAction = action;
        this.value = value;
        this.players = players;
    }

    public LobbyAction getLobbyAction() {
        return lobbyAction;
    }

    public int getValue() {
        return value;
    }

    public int getPlayers() {
        return players;
    }

}
