package com.joejohn.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.joejohn.connection.*;
import com.joejohn.entities.Lobby;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.Background;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;
import static com.joejohn.connection.LobbyPacket.LobbyAction.*;
import static com.joejohn.handlers.B2DVars.PPM;

public class LobbyState extends GameState implements PacketHandler {

    private Client client;
    private Background bg;
    private GameButton backBtn, readyBtn, notReadyBtn;
    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private String status;
    private BitmapFont font;
    private int lobbyId;
    private boolean isReady;



    public LobbyState(GameStateManager gsm) {
        super(gsm);
        lobbyId = Lobby.getLobby();
        isReady = false;

        Texture tex;

        // Background
        tex = DualRacer.res.getTexture("server");
        bg = new Background(new TextureRegion(tex), cam, 1f);
        bg.setVector(-20, 0);

        // Creating buttons

        // Creating buttons
        tex = DualRacer.res.getTexture("back");
        backBtn = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 4, DualRacer.HEIGHT / 8, cam);

        tex = DualRacer.res.getTexture("ready");
        readyBtn = new GameButton(new TextureRegion(tex),
                (DualRacer.WIDTH / 4) * 3,
                DualRacer.HEIGHT / 8, cam);

        tex = DualRacer.res.getTexture("notReady");
        notReadyBtn = new GameButton(new TextureRegion(tex),
                (DualRacer.WIDTH / 4) * 3,
                DualRacer.HEIGHT / 8, cam);

        notReadyBtn.enable(false);


        // Creating world
        cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);
        world = new World(new Vector2(0, -9.8f * 5), true);

        // Debug renderer
        b2dRenderer = new Box2DDebugRenderer();

        client = Client.getInstance();
        client.setPacketHandler(this);
        status = "";

        font = new BitmapFont();
        font.scale(0.05f);

    }

    @Override
    public void handleInput() {
        if(backBtn.isClicked()) {
            LobbyPacket packet = new LobbyPacket(LEAVE, lobbyId);
            client.send(packet);
            Lobby.setLobby(-1);
            DualRacer.res.getSound("btnclick").play();
            gsm.setState(GameStateManager.SERVER);
        }
        if(readyBtn.isClicked()) {
            isReady = true;
            DualRacer.res.getSound("btnclick").play();
            readyBtn.enable(false);
            notReadyBtn.enable(true);
            LobbyPacket packet = new LobbyPacket(READY, lobbyId);
            client.send(packet);
        }
        if(notReadyBtn.isClicked()) {
            isReady = false;
            DualRacer.res.getSound("btnclick").play();
            notReadyBtn.enable(false);
            readyBtn.enable(true);
            LobbyPacket packet = new LobbyPacket(NOT_READY, lobbyId);
            client.send(packet);
        }


    }

    @Override
    public void update(float dt) {

        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);

        backBtn.update(dt);

        notReadyBtn.update(dt);

        readyBtn.update(dt);

    }

    @Override
    public void render() {
        sb.setProjectionMatrix(cam.combined);

        bg.render(sb);

        backBtn.render(sb);

        notReadyBtn.render(sb);

        readyBtn.render(sb);


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
        if(packet.getLobbyAction() == START) {
            gsm.setState(GameStateManager.MULTIPLAYER);
        }
    }

    @Override
    public void clientPacketHandler(ClientPacket packet) {

    }
}
