package com.joejohn.connection;

import java.io.Serializable;

public class ClientPacket implements Serializable {

   public enum ClientAction implements Serializable {
        TIME;
    }

    private ClientAction action;

    public ClientPacket(ClientAction action) {
        this.action = action;
    }

    public ClientAction getAction() {
        return action;
    }
}
