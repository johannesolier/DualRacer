package com.joejohn.states;

import com.badlogic.gdx.Gdx;
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
    public BitmapFont lobbyFont;
    private Array<Lobby> lobbies;
    private int SELECTED_LOBBY = -1;
    private boolean updateLobbies;
    private long lastClick;

    private boolean joinLobby;


    public ServerState(GameStateManager gsm) {
        super(gsm);
        Texture tex;
        lobbies = new Array<Lobby>();
        updateLobbies = false;


        // Background
        tex = DualRacer.res.getTexture("background");
        bg = new Background(new TextureRegion(tex), cam, 1f);

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

        lobbyFont = new BitmapFont();
        lobbyFont.scale(0.3f);

        client = Client.getInstance();


        client.setPacketHandler(this);
        status = null;

        lastClick = System.currentTimeMillis();

        Gdx.app.log("ServerState","Number of connections:" + client.getNumberOfConnections());

/*      Lobby lobby1 = new Lobby("Lobby 1", 1, 1, cam);
        addLobby(lobby1);

        Lobby lobby2 = new Lobby("Lobby 2", 2, 2, cam);
        addLobby(lobby2);

        Lobby lobby3 = new Lobby("Lobby 3", 2, 3, cam);
        addLobby(lobby3);

        Lobby lobby4 = new Lobby("Lobby 4", 1, 4, cam);
        addLobby(lobby4);

        Lobby lobby5 = new Lobby("Lobby 5", 1, 5, cam);
        addLobby(lobby5);

        Lobby lobby6 = new Lobby("Lobby 6", 0, 6, cam);
        addLobby(lobby6);  */
        if(client.isConnectedServer()) {
            LobbyPacket packet = new LobbyPacket(REFRESH);
            client.send(packet);
        }
    }


    @Override
    public void handleInput() {
        if(client.isConnectedServer()) {
            if(createBtn.isClicked()) {
                Gdx.app.log("ServerState", "CreateButton clicked");
                // CREATE LOBBY
                DualRacer.res.getSound("btnclick").play();
                LobbyPacket packet = new LobbyPacket(CREATE);
                client.send(packet);

                // AND REFRESH
/*              lobbies.clear();
                packet = new LobbyPacket(REFRESH);
                client.send(packet);*/
                try {
                    Thread.sleep(200);
                    SELECTED_LOBBY = setSelected(SELECTED_LOBBY, true);
                } catch(InterruptedException e) {

                }
            }
            if(joinBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
                LobbyPacket packet = new LobbyPacket(JOIN, SELECTED_LOBBY);
                client.send(packet);
            }

            if(backBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
                gsm.setState(GameStateManager.MENU);
                client.disconnectServer();
            }
            if(refreshBtn.isClicked()) {
                DualRacer.res.getSound("btnclick").play();
                lobbies.clear();
                LobbyPacket packet = new LobbyPacket(REFRESH);
                client.send(packet);
            }
            for(Lobby lobby : lobbies) {
                if(lobby.isClicked()) {
                    DualRacer.res.getSound("btnclick").play();
                    setSelected(SELECTED_LOBBY, false);
                    SELECTED_LOBBY = lobby.getId();
                    setSelected(SELECTED_LOBBY, true);
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
                    try{
                        Thread.sleep(100);
                        LobbyPacket packet = new LobbyPacket(REFRESH);
                        client.send(packet);
                    } catch(InterruptedException e) {

                    }
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

        createBtn.update(dt);
        joinBtn.update(dt);
        backBtn.update(dt);
        refreshBtn.update(dt);

        if (!client.isConnectedServer()) {
            connectBtn.update(dt);
        } else {
            for (Lobby lobby : lobbies)
                lobby.update(dt);
        }

        if (joinLobby)
            gsm.setState(GameStateManager.LOBBY);
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

            if(updateLobbies) {
                updateLobbyCordinates();
                updateLobbies = false;
            }

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
        if(packet.getLobbyAction() == LOBBY) {
            Lobby lobby = new Lobby("Lobby " + packet.getValue(), packet.getPlayers(), packet.getValue(), cam, this);
            addLobby(lobby);
        }
        if(packet.getLobbyAction() == JOIN) {
            if(packet.getValue() > 0) {
                joinLobby = true;
                Lobby.setLobby(packet.getValue());
            } else {
                font.setColor(Color.RED);
                status = "Couldn't join lobby";
            }
        }

        if(packet.getLobbyAction() == REFRESH) {
            lobbies.clear();
            client.send(packet);
        }

    }

    private void addLobby(Lobby lobby) {
        //int x = DualRacer.WIDTH / 2;
        //int y = DualRacer.HEIGHT - 60 - (lobbies.size*40);
        //lobby.setPosition(x, y);
        lobbies.add(lobby);
        updateLobbies = true;
    }

    private void updateLobbyCordinates() {
        int x = DualRacer.WIDTH / 2;
        for(int i = 0; i < lobbies.size; i++) {
            int y = DualRacer.HEIGHT - 60 - (i*40);
            lobbies.get(i).setPosition(x, y);
        }
    }

    private int setSelected(int id, boolean b) {
        if(id == -1) return -1;
        for(Lobby lobby : lobbies) {
            if(lobby.getId() == id) {
                lobby.setTexSelected(b);
                return id;
            }
        }
        return -1;
    }


    @Override
    public void clientPacketHandler(ClientPacket packet) {
    }
}
