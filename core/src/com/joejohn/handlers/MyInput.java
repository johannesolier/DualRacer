package com.joejohn.handlers;

public class MyInput {

	public static int x;
	public static int y;
	public static boolean down;
	public static boolean pdown;

	public static boolean[] keys;
	public static boolean[] pKeys;
	public static final int NUM_KEYS = 3;
	public final static int JUMP = 0;
	public final static int RIGHT = 1;
	public final static int LEFT = 2;
	
	public static int firstX;
	public static int lastTouch, delta;

	static {
		keys = new boolean[NUM_KEYS];
		pKeys = new boolean[NUM_KEYS];
	}

	public static void update() {
		pdown = down;
		for (int i = 0; i < NUM_KEYS; i++) {
			pKeys[i] = keys[i];
		}
	}
	
	public static int moveRight(){
		if(Math.abs(x - firstX) > 30){
			if((x - firstX) < 0)
				return 1;
			else
				return -1;
		}
		return 0;
		
	}

	public static boolean isDown() { return down; }
	public static boolean isPressed(){return down && !pdown;}
	public static boolean isReleased() {return !down && pdown;}
	
	public static void setKey(int i, boolean b) {keys[i] = b;}
	public static boolean isDown(int i) { return keys[i]; }
	public static boolean isPressed(int i) {return keys[i] && !pKeys[i];}
}
