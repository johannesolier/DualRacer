package com.joejohn.connection;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class PlayerPacket implements Serializable {

    private Vector2 vec;
    private float angle;
    private int id;

    public PlayerPacket(Vector2 vec, float angle, int id) {
        this.vec = vec;
        this.angle = angle;
        this.id = id;
    }

    public Vector2 getVector() {
        return vec;
    }

    public float getAngle() {
        return angle;
    }

    public int getId() {
        return id;
    }
}
