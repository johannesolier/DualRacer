package com.joejohn.handlers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

/**
 * Simple image button.
 */
public class GameButton {

	private float x;
	private float y;
	private float width;
	private float height;

	private long lastUpdate;
	private static int UPDATE_THRESHOLD = 150;
	
	Vector3 vec;
	private OrthographicCamera cam;
	
	private boolean clicked;

	private TextureRegion tex;
	
	private String text;
	private TextureRegion[] font;
	
	public GameButton(TextureRegion tex, float x, float y, OrthographicCamera cam) {

		this.x = x;
		this.y = y;
		this.cam = cam;
		this.tex = tex;

		width = tex.getRegionWidth();
		height = tex.getRegionHeight();

		vec = new Vector3();

		font = new TextureRegion[11];
		for(int i = 0; i < 6; i++) {
			font[i] = new TextureRegion(tex, 32 + i * 9, 16, 9, 9);
		}
		for(int i = 0; i < 5; i++) {
			font[i + 6] = new TextureRegion(tex, 32 + i * 9, 25, 9, 9);
		}

		lastUpdate = System.currentTimeMillis() - UPDATE_THRESHOLD;
		
	}

	public GameButton(TextureRegion tex, float x, float y, OrthographicCamera cam, int threshold) {
		this(tex, x, y, cam);
		UPDATE_THRESHOLD = threshold;
	}
	
	public boolean isClicked() {
		if(clicked) {
			clicked = false;
			return true;
		}
		return false;
	}
	public void setText(String s) { text = s; }
	
	public void update(float dt) {

		vec.set(Controls.x, Controls.y, 0);
		cam.unproject(vec);

		if(Controls.isPressed() &&
			vec.x > x - width / 2 && vec.x < x + width / 2 &&
			vec.y > y - height / 2 && vec.y < y + height / 2) {
			if(System.currentTimeMillis() - lastUpdate < UPDATE_THRESHOLD) return;
			clicked = true;
			lastUpdate = System.currentTimeMillis();
		}
		else {
			clicked = false;
		}

		
	}
	
	public void render(SpriteBatch sb) {
		
		sb.begin();
		
		sb.draw(tex, x - width / 2, y - height / 2);
		
		if(text != null) {
			drawString(sb, text, x, y);
		}
		
		sb.end();
		
	}
	
	private void drawString(SpriteBatch sb, String s, float x, float y) {
		int len = s.length();
		float xo = len * font[0].getRegionWidth() / 2;
		float yo = font[0].getRegionHeight() / 2;
		for(int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if(c == '/') c = 10;
			else if(c >= '0' && c <= '9') c -= '0';
			else continue;
			sb.draw(font[c], x + i * 9 - xo, y - yo);
		}
	}

}
