package com.joejohn.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.Background;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

public class ServerState extends GameState {

    private Background bg;
    private GameButton createBtn, joinBtn;
    private World world;
    private Box2DDebugRenderer b2dRenderer;


    public ServerState(GameStateManager gsm) {
        super(gsm);
        Texture tex;

        // Background
        tex = DualRacer.res.getTexture("server");
        bg = new Background(new TextureRegion(tex), cam, 1f);
        bg.setVector(-20, 0);

        // Creating buttons
        tex = DualRacer.res.getTexture("button");
        createBtn = new GameButton(tex, DualRacer.WIDTH / 4, DualRacer.HEIGHT / 8, cam);
        createBtn.setText("Create");

        joinBtn = new GameButton(tex, (DualRacer.WIDTH / 4) * 3, DualRacer.HEIGHT / 8, cam);
        joinBtn.setText("Join");

        // Creating world
        cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);
        world = new World(new Vector2(0, -9.8f * 5), true);

        // Debug renderer
        b2dRenderer = new Box2DDebugRenderer();

    }


    @Override
    public void handleInput() {
        if(createBtn.isClicked()) {
            DualRacer.res.getSound("play").play();
        }

        if(joinBtn.isClicked()) {
            DualRacer.res.getSound("play").play();
        }

    }

    @Override
    public void update(float dt) {
        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);



    }

    @Override
    public void render() {

    }

    @Override
    public void dispose() {

    }

}
