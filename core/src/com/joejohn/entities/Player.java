package com.joejohn.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.joejohn.game.DualRacer;

public class Player extends B2DSprite {
	
	public Player(Body body) {
		
		super(body);
		
		Texture tex = DualRacer.res.getTexture("player");
		TextureRegion[] sprites = new TextureRegion[4];
		for(int i = 0; i < sprites.length; i++) {
			sprites[i] = new TextureRegion(tex, i * 32, 0, 32, 32);
		}

		animation.setFrames(sprites, 1 / 6f);
		width = sprites[0].getRegionWidth();
		height = sprites[0].getRegionHeight();
		
	}

}
