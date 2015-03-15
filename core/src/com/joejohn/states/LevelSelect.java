package com.joejohn.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;

public class LevelSelect extends GameState {

	private TextureRegion reg;
	private GameButton[][] buttons;
	private int numOfLevels;

	public LevelSelect(GameStateManager gsm) {
		super(gsm);

		// numOfLevels = new
		// File("android/assets/res/levels").listFiles().length;
//		numOfLevels = Gdx.files.internal("res/levels");
		if(Gdx.app.getType() == ApplicationType.Android){
			numOfLevels = Gdx.files.internal("res/levels").list().length;
		}
		else{
			numOfLevels = Gdx.files.internal("../res/levels").list().length;
		}

		reg = new TextureRegion(DualRacer.res.getTexture("background"), 0, 0, 640, 360);

		TextureRegion buttonReg = new TextureRegion(DualRacer.res.getTexture("hud"), 0, 0, 32, 32);
		buttons = new GameButton[4][5];
		for (int level = 0; level < numOfLevels; level++) {
			buttons[level / 5][level] = new GameButton(buttonReg, 180 + level * 60, 300 - level / 5 * 80, cam);
			buttons[level / 5][level].setText(level / 5 * buttons[0].length + level + 1 + "");
		}

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
				gsm.setState(GameStateManager.PLAY);
			}
		}

	}

	public void render() {

		sb.setProjectionMatrix(cam.combined);

		sb.begin();
		sb.draw(reg, 0, 0);
		sb.end();

		for (int level = 0; level < numOfLevels; level++) {
			buttons[level / 5][level].render(sb);
		}

	}

	public void dispose() {
	}
}
