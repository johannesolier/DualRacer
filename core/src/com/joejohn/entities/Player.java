package com.joejohn.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.joejohn.game.DualRacer;

public class Player extends B2DSprite {

	private Texture playerTex = DualRacer.res.getTexture("player");
	private Texture playerleftTex = DualRacer.res.getTexture("playerleft");
	public int direction = 1; // RIGHT, LEFT = -1;

	public Player(Body body) {

		super(body);

		playerTex = DualRacer.res.getTexture("player");
		updateTexture(playerTex);
	}

	public void updateTexture(Texture tex) {
		TextureRegion[] sprites = new TextureRegion[4];
		for (int i = 0; i < sprites.length; i++) {
			sprites[i] = new TextureRegion(tex, i * 32, 0, 32, 32);
		}

		animation.setFrames(sprites, 1 / 6f);
		width = sprites[0].getRegionWidth();
		height = sprites[0].getRegionHeight();
	}

	public void swapTexture() {
		if (direction == 1) {
			updateTexture(playerleftTex);
			direction = -1;
		} else {
			updateTexture(playerTex);
			direction = 1;
		}
	}

	public void setDirection(int direction) {
		this.direction = direction;
		swapTexture();
	}

	public void setOpponent() {
		playerTex = DualRacer.res.getTexture("player2");
		playerleftTex = DualRacer.res.getTexture("player2left");
	}

}
