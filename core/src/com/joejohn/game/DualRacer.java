package com.joejohn.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joejohn.handlers.Content;
import com.joejohn.handlers.GameStateManager;

public class DualRacer extends ApplicationAdapter {

	public static final String TITLE = "DUAL RACER";
	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;
	public static final int SCALE = 2;

	public static final float STEP = 1 / 60f;

	private SpriteBatch sb;
	private OrthographicCamera camera, hudCamera;
	private GameStateManager gsm;

	public static Content res;
	private Music menu, theme;

	@Override
	public void create() {
		res = new Content();

		// MENUS AND BUTTONS
		res.loadTexture("res/images/playBtn.png", "play");
		res.loadTexture("res/images/createBtn.png", "create");
		res.loadTexture("res/images/joinBtn.png", "join");
		res.loadTexture("res/images/onlineBtn.png", "online");
		res.loadTexture("res/images/backBtn.png", "back");
		res.loadTexture("res/images/connectBtn.png", "connect");
		res.loadTexture("res/images/refreshBtn.png", "refresh");
		res.loadTexture("res/images/lobby.png", "lobby");
		res.loadTexture("res/images/lobbySelected.png", "lobbySelected");
		res.loadTexture("res/images/readyBtn.png", "ready");
		res.loadTexture("res/images/notReadyBtn.png", "notReady");
		res.loadTexture("res/images/movebutton.png");
		res.loadTexture("res/images/hud.png");
		
		//IMAGES
		res.loadTexture("res/images/background.png");
		res.loadTexture("res/images/clouds.png");
		res.loadTexture("res/images/mountains.png");
		res.loadTexture("res/images/logo.png");
		res.loadTexture("res/images/levels.png");
		
		// PLAYER SKIN
		res.loadTexture("res/sprites/player.png");
		res.loadTexture("res/sprites/playerleft.png");
		res.loadTexture("res/sprites/player2.png");
		res.loadTexture("res/sprites/player2left.png");

		// MUSIC
		res.loadMusic("res/music/menu.ogg");

		menu = res.getMusic("menu");
		menu.setLooping(true);
		menu.setVolume(0.5f);
		menu.play();

		DualRacer.res.loadMusic("res/music/Theme.ogg");

		theme = DualRacer.res.getMusic("Theme");
		theme.setLooping(true);
		theme.setVolume(0.5f);

		// SOUNDS
		res.loadSound("res/sfx/jump.wav");
		res.loadSound("res/sfx/btnclick.wav");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, WIDTH, HEIGHT);
		sb = new SpriteBatch();
		gsm = new GameStateManager(this);
	}

	@Override
	public void render() {
		Gdx.graphics.setTitle(TITLE + " -- FPS: " + Gdx.graphics.getFramesPerSecond());

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();
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

	public OrthographicCamera getHudCamera(){
		return hudCamera;
	}
}