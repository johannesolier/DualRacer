package com.joejohn.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.joejohn.connection.*;
import com.joejohn.entities.Player;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.GameStateManager;

public class Multiplayer extends Play implements PacketHandler {

	private Client client;
	private Array<Player> opponentPlayers;
	private int id;
	private long lastPacketSent;
	private Array<PlayerPacket> playerPackets;
	private float playerTime, opponentTime, winnerTime;
	private BitmapFont font;
	private boolean gameover = false;
	private boolean won = false;

	public Multiplayer(GameStateManager gsm) {
		super(gsm);
		client = Client.getInstance();
		opponentPlayers = new Array<Player>();
		playerPackets = new Array<PlayerPacket>();
		client.setPacketHandler(this);
		int size = client.getNumberOfConnections();
		for (int i = 0; i < size; i++) {
			Player player = createPlayer();
			player.setOpponent();
			opponentPlayers.add(player);
		}
		id = 0;
		font = new BitmapFont();
		font.setScale(2f);
		font.setColor(Color.BLACK);
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		while (playerPackets.size > 0) {
			PlayerPacket pkt = playerPackets.pop();
			Player opp = opponentPlayers.get(0);
			if (pkt != null || opp != null) {
				opp.setPosition(pkt.getPosition(), pkt.getAngle());
				opp.setVelocity(pkt.getVelocity());
				opp.setDirection(pkt.getDirection());
			}
		}

		for (Player player : opponentPlayers) {
			player.update(dt);
		}

		if (System.currentTimeMillis() - lastPacketSent > 5) {
			Vector2 vec = new Vector2(player.getPosition().x, player.getPosition().y);
			Vector2 velocity = new Vector2(player.getVelocity().x, player.getVelocity().y);
			float angle = player.getAngle();
			PlayerPacket packet = new PlayerPacket(vec, velocity, angle, player.direction, id);
			client.sendAll(packet);
			lastPacketSent = System.currentTimeMillis();
		}

		playerTime = getPlayTime();
	}

	@Override
	public void finish() {
		if (!gameover) {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
		}
		// -------------------------
		if (opponentTime > 0) {
			if (playerTime > opponentTime) {
				won = true;
			}
		}
		gameover = true;
		gsm.setState(GameStateManager.LOBBY);
		// -------------------------
	}

	@Override
	public void render() {
		super.render();

		sb.setProjectionMatrix(cam.combined);

		for (int i = 0; i < opponentPlayers.size; i++) {
			opponentPlayers.get(i).render(sb);
		}
		
		sb.setProjectionMatrix(hudCam.combined);
		
		if (gameover && won) {
			sb.begin();
			font.draw(sb, "YOU WON!\nYour time was: " + playerTime, DualRacer.WIDTH / 3, DualRacer.HEIGHT - 50);
			sb.end();
		} else if (gameover && !won) {
			System.out.println("HELLO");
			sb.begin();
			font.draw(sb, "YOU LOSE..", 600, DualRacer.HEIGHT - 50);
			sb.end();
		}
	}

	@Override
	public void playerPacketHandler(PlayerPacket packet) {
		playerPackets.add(packet);
	}

	private Player getPlayerById(int id) {
		if (opponentPlayers.size > 0) {
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