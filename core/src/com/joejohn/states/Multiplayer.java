package com.joejohn.states;

import com.badlogic.gdx.utils.Array;
import com.joejohn.connection.*;
import com.joejohn.entities.Player;
import com.joejohn.handlers.GameStateManager;

public class Multiplayer extends Play implements PacketHandler {

    private Client client;
    private Array<Player> opponentPlayers;
    private int id;
    private long lastPacketSent;

    public Multiplayer(GameStateManager gsm) {
        super(gsm);
        client = Client.getInstance();
        opponentPlayers = new Array<Player>();
        client.setPacketHandler(this);
        int size = client.getNumberOfConnections();
        for(int i = 0; i < size; i++) {
            Player player = createPlayer();
            player.setOpponent();
            opponentPlayers.add(player);
        }
        id = 0;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        for(Player player : opponentPlayers) {
            player.update(dt);
        }

        if(System.currentTimeMillis() - lastPacketSent > 100) {
            PlayerPacket packet = new PlayerPacket(
                    player.getPosition(),
                    player.getAngle(),
                    player.direction,
                    id);
            client.sendAll(packet);
            lastPacketSent = System.currentTimeMillis();
        }
    }

    @Override
    public void render() {
        super.render();

        sb.setProjectionMatrix(cam.combined);

        for(int i = 0; i < opponentPlayers.size; i++) {
            opponentPlayers.get(i).render(sb);
        }

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