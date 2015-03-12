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
    private GameButton backBtn, refreshBtn;
    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private String status;
    private BitmapFont font;
    private int lobbyId;



    public LobbyState(GameStateManager gsm) {
        super(gsm);
        lobbyId = Lobby.getLobby();

        Texture tex;

        // Background
        tex = DualRacer.res.getTexture("server");
        bg = new Background(new TextureRegion(tex), cam, 1f);
        bg.setVector(-20, 0);

        // Creating buttons

        // Creating buttons
        tex = DualRacer.res.getTexture("back");
        backBtn = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 4, DualRacer.HEIGHT / 8, cam);

        tex = DualRacer.res.getTexture("refresh");
        refreshBtn = new GameButton(new TextureRegion(tex),
                DualRacer.WIDTH - tex.getWidth() / 2 - 10,
                DualRacer.HEIGHT - tex.getHeight() / 2 - 10, cam);


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
            gsm.setState(GameStateManager.SERVER);
        }

    }

    @Override
    public void update(float dt) {
        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);

        backBtn.update(dt);

        refreshBtn.update(dt);

    }

    @Override
    public void render() {
        sb.setProjectionMatrix(cam.combined);

        bg.render(sb);

        backBtn.render(sb);

        refreshBtn.render(sb);

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

    }

    @Override
    public void peerPacketHandler(PeerPacket packet) {

    }

    @Override
    public void clientPacketHandler(ClientPacket packet) {

    }
}
