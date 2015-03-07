package com.joejohn.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.GameStateManager;

public abstract class GameState {
	
	protected GameStateManager gsm;
	protected DualRacer game;
	
	protected SpriteBatch sb;
	protected OrthographicCamera cam;

	protected boolean debug = true;
	
	protected GameState(GameStateManager gsm){
		this.gsm = gsm;
		game = gsm.game();
		sb = game.getSpriteBatch();
		cam = game.getCamera();
	}
	
	public abstract void handleInput();
	public abstract void update(float dt);
	public abstract void render();
	public abstract void dispose();

}
