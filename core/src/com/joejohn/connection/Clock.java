package com.joejohn.connection;

public class Clock {

    private static Clock clock;
    private long time;

    Clock() {

    }

    public static Clock getInstance() {
        if(clock == null) {
            clock = new Clock();
        }
        return clock;
    }

    public void synchronizeTime() {
        time = System.currentTimeMillis();
    }

    public void synchronizeTime(long dt) {
        time = System.currentTimeMillis() - dt;
    }

    public long getTime() {
        return System.currentTimeMillis() - time;
    }
}
