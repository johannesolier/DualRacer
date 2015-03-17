package com.joejohn.connection;

import java.io.Serializable;

public class ServerPacket implements Serializable {

    protected enum ServerAction implements Serializable {
        CLOSE, CHECK;
    }

    private ServerAction action;

    public ServerPacket(ServerAction action) {
        this.action = action;
    }

    public ServerAction getAction() {
        return action;
    }
}
