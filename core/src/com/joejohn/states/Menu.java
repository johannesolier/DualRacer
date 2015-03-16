package com.joejohn.states;

import static com.joejohn.handlers.B2DVars.PPM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.Background;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

public class Menu extends GameState {

    private Background bg, clouds, mountains;
    private GameButton playButton, onlineButton;
    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private Image logo;


    public Menu(GameStateManager gsm) {
        super(gsm);

        // Background
        Texture tex = DualRacer.res.getTexture("background");
        Texture tex2 = DualRacer.res.getTexture("clouds");
        Texture tex3 = DualRacer.res.getTexture("mountains");
        bg = new Background(new TextureRegion(tex), cam, 1f);
        clouds = new Background(new TextureRegion(tex2), cam, 0.5f);
        mountains = new Background(new TextureRegion(tex3), cam, 0.3f);
        clouds.setVector(25, 0);
        mountains.setVector(-35f, 0);
        
        logo = new Image(DualRacer.res.getTexture("logo"));

        // Button
        tex = DualRacer.res.getTexture("play");
        playButton = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 2, DualRacer.HEIGHT / 2 + 32, cam);

        tex = DualRacer.res.getTexture("online");
        onlineButton = new GameButton(new TextureRegion(tex), DualRacer.WIDTH / 2, DualRacer.HEIGHT / 2 - 32, cam);

        // World
        cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);

        world = new World(new Vector2(0, -9.8f * 5), true);

        b2dRenderer = new Box2DDebugRenderer();

    }

    @Override
    public void handleInput() {
        if(playButton.isClicked()) {
            DualRacer.res.getSound("btnclick").play();
            gsm.setState(GameStateManager.LEVEL_SELECT);
        }
        if(onlineButton.isClicked()) {
            DualRacer.res.getSound("btnclick").play();
            gsm.setState(GameStateManager.SERVER);

        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);
        clouds.update(dt);
        mountains.update(dt);
        
        playButton.update(dt);

        onlineButton.update(dt);

    }

    @Override
    public void render() {
        sb.setProjectionMatrix(cam.combined);

        // Draw Background
        bg.render(sb);
        mountains.render(sb);
        clouds.render(sb);
        
        sb.begin();
        logo.draw(sb, 1);
        sb.end();
        
        // Draw Button
        playButton.render(sb);

        onlineButton.render(sb);

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
