package com.joejohn.connection;

import java.io.Serializable;

public class LobbyPacket implements Serializable {

    public enum LobbyAction {
        JOIN,
        LEAVE,
        START,
        CREATE
    }

    private LobbyAction lobbyAction;
    private int value;

    public LobbyPacket(LobbyAction action, int value) {
        lobbyAction = action;
        this.value = value;
    }

    public LobbyPacket(LobbyAction action) {
        lobbyAction = action;
        value = -1;
    }

    public LobbyAction getLobbyAction() {
        return lobbyAction;
    }

    public int getValue() {
        return value;
    }

}
