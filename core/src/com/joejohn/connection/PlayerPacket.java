package com.joejohn.connection;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class PlayerPacket implements Serializable {

    private Vector2 pos;
    private Vector2 velocity;
    private float angle;
    private int direction;
    private int id;

    public PlayerPacket(Vector2 pos, Vector2 velocity, float angle,int direction, int id) {
        this.pos = pos;
        this.velocity = velocity;
        this.angle = angle;
        this.direction = direction;
        this.id = id;
    }

    public Vector2 getPosition() {
        return pos;
    }

    public Vector2 getVelocity() {
        return velocity;
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
