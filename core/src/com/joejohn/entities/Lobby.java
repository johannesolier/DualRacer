package com.joejohn.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.joejohn.connection.Config;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.Controls;
import com.joejohn.states.ServerState;

public class Lobby {

    private static int LOBBY = -1;

    private ServerState ss;
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
    Texture texSelected;

    boolean selected;



    public Lobby(String title, int numOfPlayers, int id,OrthographicCamera cam, ServerState ss) {
        this.title = title;
        this.numOfPlayers = numOfPlayers;
        this.id = id;
        this.x = DualRacer.WIDTH / 2;
        this.y = DualRacer.HEIGHT / 2;
        this.cam = cam;
        this.ss = ss;
        font = ss.lobbyFont;
        selected = false;

        vec = new Vector3();
        tex = DualRacer.res.getTexture("lobby");
        texSelected = DualRacer.res.getTexture("lobbySelected");

        width = tex.getWidth();
        height = tex.getHeight();
    }

    public boolean isClicked() {
        return clicked;
    }

    public void update(float dt) {
        vec.set(Controls.x, Controls.y, 0);
        cam.unproject(vec);

        if(Controls.isPressed() &&
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
        if(!selected)
            sb.draw(tex, x - width / 2, y - height / 2);
        else
            sb.draw(texSelected, x - width / 2, y - height / 2);
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

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setTexSelected(boolean b) {
        selected = b;
    }


    public static int getLobby() {
        return LOBBY;
    }

    public static void setLobby(int lobby) {
        LOBBY = lobby;
    }

}
