package com.joejohn.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.joejohn.connection.*;
import com.joejohn.game.DualRacer;
import com.joejohn.entities.Lobby;
import com.joejohn.handlers.Background;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

import static com.joejohn.handlers.B2DVars.PPM;
import static com.joejohn.connection.LobbyPacket.LobbyAction.*;

public class ServerState extends GameState implements PacketHandler {

    private Client client;
    private Background bg;
    private GameButton createBtn, joinBtn, backBtn, connectBtn, refreshBtn;
    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private String status;
    private BitmapFont font;
    private Array<Lobby> lobbies;


    public ServerState(GameStateManager gsm) {
        super(gsm);
        Texture tex;
        lobbies = new Array<Lobby>();

        // Background
        tex = DualRacer.res.getTexture("server");
        bg = new Background(new TextureRegion(tex), cam, 1f);
        bg.setVector(-20, 0);

        // Creating buttons
        tex = DualRacer.res.getTexture("back");
        backBtn = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 4, DualRacer.HEIGHT / 8, cam);

        tex = DualRacer.res.getTexture("create");
        createBtn = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 2, DualRacer.HEIGHT / 8, cam);

        tex = DualRacer.res.getTexture("connect");
        connectBtn = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 2, DualRacer.HEIGHT / 2, cam);

        tex = DualRacer.res.getTexture("join");
        joinBtn = new GameButton(new TextureRegion(tex), (DualRacer.WIDTH / 4) * 3, DualRacer.HEIGHT / 8, cam);

        tex = DualRacer.res.getTexture("refresh");
        refreshBtn = new GameButton(new TextureRegion(tex),
                DualRacer.WIDTH - tex.getWidth() / 2 - 10,
                DualRacer.HEIGHT - tex.getHeight() / 2 - 10, cam);


        // Creating world
        cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);
        world = new World(new Vector2(0, -9.8f * 5), true);

        // Debug renderer
        b2dRenderer = new Box2DDebugRenderer();

        font = new BitmapFont();
        font.scale(0.05f);

        client = Client.getInstance();
        client.setPacketHandler(this);
        status = null;

        Lobby lobby1 = new Lobby("Lobby 1", 1, 1, DualRacer.WIDTH / 2, DualRacer.HEIGHT - 70, cam);
        lobbies.add(lobby1);

        Lobby lobby2 = new Lobby("Lobby 2", 1, 2, DualRacer.WIDTH / 2, DualRacer.HEIGHT - 110, cam);
        lobbies.add(lobby2);

        Lobby lobby3 = new Lobby("Lobby 3", 1, 3, DualRacer.WIDTH / 2, DualRacer.HEIGHT - 150, cam);
        lobbies.add(lobby3);

        Lobby lobby4 = new Lobby("Lobby 4", 1, 4, DualRacer.WIDTH / 2, DualRacer.HEIGHT - 190, cam);
        lobbies.add(lobby4);

        Lobby lobby5 = new Lobby("Lobby 5", 1, 5, DualRacer.WIDTH / 2, DualRacer.HEIGHT - 230, cam);
        lobbies.add(lobby5);
    }


    @Override
    public void handleInput() {
        if(client.isConnectedServer()) {
            if(createBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
                LobbyPacket packet = new LobbyPacket(CREATE);
                client.send(packet);
            }
            if(joinBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
            }

            if(backBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
                gsm.setState(GameStateManager.MENU);
                client.disconnectServer();
            }
            if(refreshBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
            }
            for(Lobby lobby : lobbies) {
                if(lobby.isClicked()) {
                    DualRacer.res.getSound("btnclick").play();
                    System.out.println("Joining lobby " + lobby.getId());
                    break; // Precaution
                }
            }
        }
        if(!client.isConnectedServer()) {
            if(connectBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
                if(client.connectServer()) {
                    font.setColor(Color.GREEN);
                    status = "Connected.";
                } else {
                    font.setColor(Color.RED);
                    status = "Connection failed.";
                }
            }

            if(backBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
                gsm.setState(GameStateManager.MENU);
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);

        createBtn.update(dt);

        joinBtn.update(dt);

        backBtn.update(dt);

        refreshBtn.update(dt);

        if(!client.isConnectedServer()) {
            connectBtn.update(dt);
        } else {
            for(Lobby lobby : lobbies)
                lobby.update(dt);
        }
    }

    @Override
    public void render() {
        sb.setProjectionMatrix(cam.combined);

        bg.render(sb);

        backBtn.render(sb);

        if(!client.isConnectedServer())
            connectBtn.render(sb);
        else {

            createBtn.render(sb);

            joinBtn.render(sb);

            refreshBtn.render(sb);

            for(Lobby lobby : lobbies) {
                lobby.render(sb);
            }

        }

        if(status != null) {
            sb.begin();
            font.draw(sb, status, 10, DualRacer.HEIGHT - 10);
            sb.end();
        }


        if(debug) {
            cam.setToOrtho(false, DualRacer.WIDTH / PPM, DualRacer.HEIGHT / PPM);
            b2dRenderer.render(world, cam.combined);
            cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        world.dispose();
        b2dRenderer.dispose();
        for(Lobby lobby : lobbies) {
            lobby.dispose();
        }
    }

    @Override
    public void playerPacketHandler(PlayerPacket packet) {
    }

    @Override
    public void lobbyPacketHandler(LobbyPacket packet) {
        if(packet.getLobbyAction() == CREATE) {
            if(packet.getValue() > 0) {
                System.out.println("Joining lobby number " + packet.getValue());
            } else {
                font.setColor(Color.RED);
                status = "Couldn't create lobby.";
            }
        }
    }

    @Override
    public void peerPacketHandler(PeerPacket packet) {
    }

    @Override
    public void clientPacketHandler(ClientPacket packet) {
    }
}
