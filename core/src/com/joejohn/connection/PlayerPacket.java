package com.joejohn.connection;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class PlayerPacket implements Serializable {

    private Vector2 vec;
    private float angle;
    private int direction;
    private int id;

    public PlayerPacket(Vector2 vec, float angle,int direction, int id) {
        this.vec = vec;
        this.angle = angle;
        this.direction = direction;
        this.id = id;
    }

    public Vector2 getVector() {
        return vec;
    }

    public float getAngle() {
        return angle;
    }

    public int getDirection() {
        return this.direction;
    }

    public int getId() {
        return id;
    }
}
