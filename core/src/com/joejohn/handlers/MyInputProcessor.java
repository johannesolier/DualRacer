package com.joejohn.handlers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter{
	
	public boolean touchDragged(int x, int y, int pointer) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		MyInput.delta = x - MyInput.lastTouch;
		MyInput.lastTouch = x;
		return true;
	}
	
	public boolean touchDown(int x, int y, int pointer, int button) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		MyInput.lastTouch = x;
		return true;
	}
	
	public boolean touchUp(int x, int y, int pointer, int button) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = false;
		return true;
	}
	
	public boolean keyDown(int k){
		if(k == Keys.SPACE){
			MyInput.setKey(MyInput.JUMP, true);
		}
		if(k == Keys.RIGHT){
			MyInput.setKey(MyInput.RIGHT, true);
		}
		if(k == Keys.LEFT){
			MyInput.setKey(MyInput.LEFT, true);
		}
		return true;
	}
	
	public boolean keyUp(int k){
		if(k == Keys.SPACE){
			MyInput.setKey(MyInput.JUMP, false);
		}
		if(k == Keys.RIGHT){
			MyInput.setKey(MyInput.RIGHT, false);
		}
		if(k == Keys.LEFT){
			MyInput.setKey(MyInput.LEFT, false);
		}
		return true;
	}

}
