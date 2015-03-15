package com.joejohn.states;

import static com.joejohn.connection.ClientPacket.ClientAction.*;
import com.badlogic.gdx.utils.Array;
import com.joejohn.connection.*;
import com.joejohn.entities.Player;
import com.joejohn.handlers.GameStateManager;

public class Multiplayer extends Play implements PacketHandler {

    private Client client;
    private Array<Player> opponentPlayers;
    private int id;

    public Multiplayer(GameStateManager gsm) {
        super(gsm);
        client = Client.getInstance();
        opponentPlayers = new Array<Player>();
        client.setPacketHandler(this);
        id = 0;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        PlayerPacket packet = new PlayerPacket(
                player.getPosition(),
                player.getAngle(),
                player.direction,
                id);

        client.sendAll(packet);
    }

    @Override
    public void playerPacketHandler(PlayerPacket packet) {
        Player player = getPlayerById(packet.getId());
        if(player != null) {
            player.setPosition(packet.getVector(), packet.getAngle());
            player.setDirection(packet.getDirection());
        }

    }

    private Player getPlayerById(int id) {
        if(opponentPlayers.size > 0) {
            return opponentPlayers.get(0);
        }
        return null;
    }

    @Override
    public void lobbyPacketHandler(LobbyPacket packet) {
    }

    @Override
    public void clientPacketHandler(ClientPacket packet) {
    }
}
