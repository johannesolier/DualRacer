package com.joejohn.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joejohn.handlers.Content;
import com.joejohn.handlers.GameStateManager;
import com.joejohn.handlers.MyInput;
import com.joejohn.handlers.MyInputProcessor;

public class DualRacer extends ApplicationAdapter {
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;
	public static final int SCALE = 2;
	
	public static final float STEP = 1 / 60f;
	private float accum;

	private SpriteBatch sb;
	private OrthographicCamera camera;
	private GameStateManager gsm;
	
	public static Content res;
	private Music theme;
	private Sound jump;

	@Override
	public void create() {
		Gdx.input.setInputProcessor(new MyInputProcessor());
	
		res = new Content();

		res.loadTexture("res/images/menu.png", "menu");
		res.loadTexture("res/images/play.png", "play");
		
		res.loadMusic("res/music/Theme.ogg");
		theme = res.getMusic("Theme");
		theme.setLooping(true);
		theme.setVolume(0.5f);
		theme.play();
		
		res.loadSound("res/sfx/jump.wav");
		res.loadSound("res/sfx/jump.wav", "play"); // Placeholder sound

		
		sb = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		gsm = new GameStateManager(this);
	}

	@Override
	public void render() {
		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP){
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
		}
	}

	public void dispose() {
		res.removeAll();
	}

	public SpriteBatch getSpriteBatch() {
		return sb;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}
}
