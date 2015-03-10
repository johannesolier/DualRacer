package com.joejohn.handlers;

import java.util.Stack;

import com.joejohn.game.DualRacer;
import com.joejohn.states.*;

public class GameStateManager {
	
	private DualRacer game;
	
	private Stack<GameState> gameStates;
	
	public static final int PLAY = 0;
	public static final int MENU = 1;
	public static final int LOBBY = 2;
	public static final int SERVER = 3;
	public static final int MULTIPLAYER = 4;
	
	public GameStateManager(DualRacer game){
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
		if(state == PLAY) return new Play(this);
		if(state == MENU) return new Menu(this);
		if(state == LOBBY) return new Lobby(this);
		if(state == SERVER) return new ServerState(this);
		if(state == MULTIPLAYER) return new Multiplayer(this);
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
