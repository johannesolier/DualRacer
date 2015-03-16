package com.joejohn.handlers;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.joejohn.game.DualRacer;
import com.joejohn.states.GameState;
import com.joejohn.states.LevelSelect;
import com.joejohn.states.LobbyState;
import com.joejohn.states.Menu;
import com.joejohn.states.Multiplayer;
import com.joejohn.states.Play;
import com.joejohn.states.ServerState;

public class GameStateManager {
	
	private DualRacer game;
	
	private Stack<GameState> gameStates;
	private int currentState;
	private Controls c;
	
	public static final int PLAY = 0;
	public static final int MENU = 1;
	public static final int LOBBY = 2;
	public static final int SERVER = 3;
	public static final int MULTIPLAYER = 4;
	public static final int LEVEL_SELECT = 5;
	
	public GameStateManager(DualRacer game){
		c = new Controls();
		Gdx.input.setInputProcessor(c);
		this.game = game;
		gameStates = new Stack<GameState>();
		pushState(MENU);
	}
	
	public DualRacer game(){
		return game;
	}
	
	public void update(float dt){
		gameStates.peek().update(dt);
	}

	public void render(){
		gameStates.peek().render(); 
	}
	
	private GameState getState(int state){
		if(state == PLAY){
			Play p = new Play(this);
			c.setGameState(p);
			currentState = PLAY;
			return p;
		}
		if(state == MENU) {
			currentState = MENU;
			return new Menu(this);
		}
		if(state == LOBBY) {
			currentState = LOBBY;
			return new LobbyState(this);
		}
		if(state == SERVER) {
			currentState = SERVER;
			return new ServerState(this);
		}
		if(state == MULTIPLAYER) {
			Multiplayer mp = new Multiplayer(this);
			c.setGameState(mp);
			currentState = MULTIPLAYER;
			return mp;
		}
		if(state == LEVEL_SELECT) {
			LevelSelect ls = new LevelSelect(this, currentState);
			currentState = LEVEL_SELECT;
			return ls;
		}
		return null;
	}
	
	public void setState(int state){
		popState();
		pushState(state);	
	}
	
	public void pushState(int state){
		gameStates.push(getState(state));
	}
	
	public void popState(){
		GameState g = gameStates.pop();
		g.dispose();
	}
}
