package com.joejohn.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.joejohn.connection.*;
import com.joejohn.entities.Player;
import com.joejohn.handlers.GameStateManager;

public class Multiplayer extends Play implements PacketHandler {

    private Client client;
    private Array<Player> opponentPlayers;
    private int id;
    private long lastPacketSent;
    private Array<PlayerPacket> playerPackets;

    public Multiplayer(GameStateManager gsm) {
        super(gsm);
        client = Client.getInstance();
        opponentPlayers = new Array<Player>();
        playerPackets = new Array<PlayerPacket>();
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

        if(playerPackets.size > 0) {
            PlayerPacket pkt = playerPackets.peek();
            Player opp = opponentPlayers.get(0);
            if(pkt != null || opp != null) {
                opp.setPosition(pkt.getPosition(), pkt.getAngle());
                opp.setVelocity(pkt.getVelocity());
                opp.setDirection(pkt.getDirection());
            }
            playerPackets.clear();
        }

        for(Player player : opponentPlayers) {
            player.update(dt);
        }

        if(System.currentTimeMillis() - lastPacketSent > 20) {
            Vector2 vec = new Vector2(player.getPosition().x, player.getPosition().y);
            Vector2 velocity = new Vector2(player.getVelocity().x, player.getVelocity().y);
            float angle = player.getAngle();
            PlayerPacket packet = new PlayerPacket(
                    vec,
                    velocity,
                    angle,
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
        playerPackets.add(packet);
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