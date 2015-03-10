package com.joejohn.states;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

public class LevelSelect extends GameState {
	
	private TextureRegion reg;
	
	private GameButton[][] buttons;
	
	public LevelSelect(GameStateManager gsm) {
		
		super(gsm);
		
		reg = new TextureRegion(DualRacer.res.getTexture("bgs"), 0, 0, 320, 240);
		
		TextureRegion buttonReg = new TextureRegion(DualRacer.res.getTexture("hud"), 0, 0, 32, 32);
		buttons = new GameButton[5][5];
		for(int row = 0; row < buttons.length; row++) {
			for(int col = 0; col < buttons[0].length; col++) {
				buttons[row][col] = new GameButton(buttonReg, 80 + col * 40, 200 - row * 40, cam);
				buttons[row][col].setText(row * buttons[0].length + col + 1 + "");
			}
		}
		
		cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);
		
	}
	
	public void handleInput() {
	}
	
	public void update(float dt) {
		
		handleInput();
		
		for(int row = 0; row < buttons.length; row++) {
			for(int col = 0; col < buttons[0].length; col++) {
				buttons[row][col].update(dt);
				if(buttons[row][col].isClicked()) {
					Play.level = row * buttons[0].length + col + 1;
					DualRacer.res.getSound("levelselect").play();
					gsm.setState(GameStateManager.PLAY);
				}
			}
		}
		
	}
	
	public void render() {
		
		sb.setProjectionMatrix(cam.combined);
		
		sb.begin();
		sb.draw(reg, 0, 0);
		sb.end();
		
		for(int row = 0; row < buttons.length; row++) {
			for(int col = 0; col < buttons[0].length; col++) {
				buttons[row][col].render(sb);
			}
		}
		
	}
	
	public void dispose() {
	}
}