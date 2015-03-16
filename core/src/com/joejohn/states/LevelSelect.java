package com.joejohn.states;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

public class LevelSelect extends GameState {

	private TextureRegion reg;
	private GameButton[][] buttons;
	private int numOfLevels = 5;
	private Image levels;
	private int lastState;

	public LevelSelect(GameStateManager gsm, int lastState) {
		super(gsm);
		this.lastState = lastState;

		reg = new TextureRegion(DualRacer.res.getTexture("background"), 0, 0, 640, 360);

		TextureRegion buttonReg = new TextureRegion(DualRacer.res.getTexture("hud"), 0, 0, 32, 32);
		buttons = new GameButton[4][5];
		for (int level = 0; level < numOfLevels; level++) {
			buttons[level / 5][level] = new GameButton(buttonReg, 180 + level * 60, 200 - level / 5 * 80, cam);
			buttons[level / 5][level].setText(level / 5 * buttons[0].length + level + 1 + "");
		}
		
		levels = new Image(DualRacer.res.getTexture("levels"));

		cam.setToOrtho(false, DualRacer.WIDTH, DualRacer.HEIGHT);

	}

	public void handleInput() {
	}

	public void update(float dt) {

		handleInput();
		
		for (int level = 0; level < numOfLevels; level++) {
			buttons[level / 5][level].update(dt);
			if (buttons[level / 5][level].isClicked()) {
				Play.level = level / 5 * buttons[0].length + level + 1;
				DualRacer.res.getSound("btnclick").play();
				if(lastState == GameStateManager.PLAY || lastState == GameStateManager.MENU) {
					gsm.setState(GameStateManager.PLAY);
				} else if(lastState == GameStateManager.LOBBY) {
					gsm.setState(GameStateManager.LOBBY);
				}
			}
		}

	}

	public void render() {

		sb.setProjectionMatrix(cam.combined);

		sb.begin();
		sb.draw(reg, 0, 0);
		levels.draw(sb, 1);
		sb.end();

		for (int level = 0; level < numOfLevels; level++) {
			buttons[level / 5][level].render(sb);
		}

	}

	public void dispose() {
	}
}
