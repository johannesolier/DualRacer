package com.joejohn.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.joejohn.connection.Client;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.Background;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

import static com.joejohn.handlers.B2DVars.PPM;

public class ServerState extends GameState {

    private Client client;
    private Background bg;
    private GameButton createBtn, joinBtn, backBtn, connectBtn;
    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private String status;
    private BitmapFont font;


    public ServerState(GameStateManager gsm) {
        super(gsm);
        Texture tex;

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

        // Creating world
        cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);
        world = new World(new Vector2(0, -9.8f * 5), true);

        // Debug renderer
        b2dRenderer = new Box2DDebugRenderer();

        font = new BitmapFont();
        font.scale(0.05f);

        client = Client.getInstance();
        status = null;

    }


    @Override
    public void handleInput() {
        if(createBtn.isClicked()) {
            DualRacer.res.getSound("btnclick").play();
        }

        if(joinBtn.isClicked()) {
            DualRacer.res.getSound("btnclick").play();
        }

        if(backBtn.isClicked()) {
            DualRacer.res.getSound("btnclick").play();
            client.disconnectServer();
            gsm.setState(GameStateManager.MENU);
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

        if(!client.isConnectedServer()) {
            connectBtn.update(dt);
        } else {
        }


    }

    @Override
    public void render() {
        sb.setProjectionMatrix(cam.combined);

        bg.render(sb);

        createBtn.render(sb);

        joinBtn.render(sb);

        backBtn.render(sb);

        if(!client.isConnectedServer())
            connectBtn.render(sb);

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
    }

}
