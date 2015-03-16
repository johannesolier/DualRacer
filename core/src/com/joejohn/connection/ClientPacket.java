package com.joejohn.connection;

import java.io.Serializable;

public class ClientPacket implements Serializable {

   public enum ClientAction implements Serializable {
        WON;
    }

    private ClientAction action;
    private float value;

    public ClientPacket(ClientAction action) {
        this.action = action;
    }

    public ClientPacket(ClientAction action, float value) {
        this.action = action;
        this.value = value;
    }

    public ClientAction getAction() {
        return action;
    }
    public float getValue() { return value; }

}
