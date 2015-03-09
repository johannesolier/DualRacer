package com.joejohn.connection;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class PlayerPacket implements Serializable {

    private Vector2 vec;
    private float angle;

    public PlayerPacket(Vector2 vec, float angle) {
        this.vec = vec;
        this.angle = angle;

    }

    public Vector2 getVector() {
        return vec;
    }

    public float getAngle() {
        return angle;
    }
}
