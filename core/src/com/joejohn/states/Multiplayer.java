package com.joejohn.states;

import com.badlogic.gdx.utils.Array;
import com.joejohn.connection.*;
import com.joejohn.entities.Player;
import com.joejohn.handlers.GameStateManager;

public class Multiplayer extends Play implements PacketHandler {

    Client client;
    Array<Player> opponentPlayers;

    public Multiplayer(GameStateManager gsm) {
        super(gsm);
        client = Client.getInstance();
        opponentPlayers = new Array<Player>();
        client.setPacketHandler(this);
    }


    @Override
    public void playerPacketHandler(PlayerPacket packet) {
        Player player = getPlayerById(packet.getId());
        if(player != null)
            player.setPosition(packet.getVector(), packet.getAngle());
    }


    private Player getPlayerById(int id) {
        for(Player player : opponentPlayers) {
            if(true)
                return player;
        }
        return null;
    }




    @Override
    public void lobbyPacketHandler(LobbyPacket packet) {
    }

    @Override
    public void peerPacketHandler(PeerPacket packet) {
    }
}
