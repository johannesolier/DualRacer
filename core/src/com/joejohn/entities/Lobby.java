package com.joejohn.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.joejohn.connection.Config;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.MyInput;

public class Lobby {

    private String title;
    private int numOfPlayers;
    private int id;

    private float x;
    private float y;
    private float width;
    private float height;

    Vector3 vec;
    private OrthographicCamera cam;
    private BitmapFont font;

    private boolean clicked;

    Texture tex;



    public Lobby(String title, int numOfPlayers, int id, float x, float y, OrthographicCamera cam) {
        this.title = title;
        this.numOfPlayers = numOfPlayers;
        this.id = id;
        this.x = x;
        this.y = y;
        this.cam = cam;

        font = new BitmapFont();
        font.scale(0.3f);
        vec = new Vector3();
        tex = DualRacer.res.getTexture("lobby");

        width = tex.getWidth();
        height = tex.getHeight();
    }

    public boolean isClicked() {
        return clicked;
    }

    public void update(float dt) {
        vec.set(MyInput.x, MyInput.y, 0);
        cam.unproject(vec);

        if(MyInput.isPressed() &&
                vec.x > x - width / 2 && vec.x < x + width / 2 &&
                vec.y > y - height / 2 && vec.y < y + height / 2) {
            clicked = true;
        }
        else {
            clicked = false;
        }

    }

    public void render(SpriteBatch sb) {
        sb.begin();

        sb.draw(tex, x - width / 2, y - height / 2);

        font.draw(sb, title, x - (width / 2) + 10, y + 7);
        String playerString = getPlayerSpotString();
        font.draw(sb, playerString, x + (width / 2) - playerString.length() * 8 - 10, y + 7);
        sb.end();
    }

    private String getPlayerSpotString() {
        StringBuilder sb = new StringBuilder();
        sb.append(numOfPlayers);
        sb.append("/");
        sb.append(Config.MAX_PLAYERS);
        return sb.toString();
    }

    public void dispose() {
        tex.dispose();
        font.dispose();
    }

    public int getId() {
        return id;
    }

}
