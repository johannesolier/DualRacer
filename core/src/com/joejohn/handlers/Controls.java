package com.joejohn.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.joejohn.states.Play;

public class Controls extends InputAdapter{
	
	public static int x;
	public static int y;
	
	public static boolean down;
	public static boolean pdown;
	
	private Play state;
	
	public Controls(){
	}
	
	public void update(){
		pdown = down;
	}
	
	public void setGameState(Play state){
		this.state = state;
	}
	
	public boolean touchDragged(int xPos, int yPos, int pointer) {
		x = xPos;
		y = yPos;
		down = true;
		return true;
	}
	
	public boolean touchDown(int xPos, int yPos, int pointer, int button) {
		x = xPos;
		y = yPos;
		down = true;
		if(x < Gdx.graphics.getWidth() / 2 && state != null)
			state.playerJump();
		return true;
	}
	
	public boolean touchUp(int xPos, int yPos, int pointer, int button) {
		x = xPos;
		y = yPos;
		if(state != null){
			if(x >= Gdx.graphics.getWidth() / 2){
				down = false;
				Play.direction = 0;
			}
		}
		down = false;
		return true;
	}
	
	public boolean keyDown(int k){
		if(k == Keys.RIGHT)
			Play.direction = 1;
		if(k == Keys.LEFT)
			Play.direction = -1;
		if(k == Keys.SPACE){
			state.playerJump();
		}
		return true;
	}
	
	public boolean keyUp(int k){
		Play.direction = 0;
		return true;
	}
	
	public static boolean isDown(){ return down; }
	public static boolean isPressed(){ return down && !pdown; }
	public static boolean isReleased() {return !down && pdown; }

}
