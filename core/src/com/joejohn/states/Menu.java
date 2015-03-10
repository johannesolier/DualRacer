package com.joejohn.states;

import static com.joejohn.handlers.B2DVars.PPM;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.Background;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

public class Menu extends GameState {

    private Background bg;
    private GameButton playButton;
    private World world;
    private Box2DDebugRenderer b2dRenderer;


    public Menu(GameStateManager gsm) {
        super(gsm);

        // Background
        Texture tex = DualRacer.res.getTexture("menu");
        bg = new Background(new TextureRegion(tex), cam, 1f);
        bg.setVector(-20, 0);



        // Button
        tex = DualRacer.res.getTexture("play");
        playButton = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 2, DualRacer.HEIGHT / 2, cam);

        // World
        cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);

        world = new World(new Vector2(0, -9.8f * 5), true);

        b2dRenderer = new Box2DDebugRenderer();

    }

    @Override
    public void handleInput() {
        if(playButton.isClicked()) {
            DualRacer.res.getSound("play").play();
            gsm.setState(GameStateManager.PLAY);
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);

        playButton.update(dt);
    }

    @Override
    public void render() {
        sb.setProjectionMatrix(cam.combined);

        // Draw Background
        bg.render(sb);

        // Draw Button
        playButton.render(sb);

        // debug draw box2d
        if(debug) {
            cam.setToOrtho(false, DualRacer.WIDTH / PPM, DualRacer.HEIGHT / PPM);
            b2dRenderer.render(world, cam.combined);
            cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);
        }

        // Draw Title Here Somehow

    }

    @Override
    public void dispose() {}
}
