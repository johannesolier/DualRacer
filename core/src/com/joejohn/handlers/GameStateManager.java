package com.joejohn.handlers;

import java.util.Stack;

import com.joejohn.game.DualRacer;
import com.joejohn.states.GameState;
import com.joejohn.states.Play;

public class GameStateManager {
	
	private DualRacer game;
	
	private Stack<GameState> gameStates;
	
	public static final int PLAY = 0;
	
	public GameStateManager(DualRacer game){
		this.game = game;
		gameStates = new Stack<GameState>();
		pushState(PLAY);
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
