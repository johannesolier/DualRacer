package com.joejohn.handlers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public class MyInputProcessor extends InputAdapter{
	
	public boolean touchDragged(int x, int y, int pointer) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		return true;
	}
	
	public boolean touchDown(int x, int y, int pointer, int button) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
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
			MyInput.setKey(MyInput.BUTTON2, true);
		}
		return true;
	}
	
	public boolean keyUp(int k){
		if(k == Keys.SPACE){
			MyInput.setKey(MyInput.JUMP, false);
		}
		if(k == Keys.RIGHT){
			MyInput.setKey(MyInput.BUTTON2, false);
		}
		return true;
	}

}
