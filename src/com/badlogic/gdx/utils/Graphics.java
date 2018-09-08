package com.badlogic.gdx.utils;

public class Graphics {

    float deltaTime = 0;
    long frameStart = 0;
    int frames = 0;
    int fps;
    long lastTime = System.nanoTime();

    public float getDeltaTime() {
	return deltaTime;
    }

    public float getRawDeltaTime() {
	return deltaTime;
    }

    public int getFramesPerSecond() {
	return fps;
    }

    public void updateTime() {
	long time = System.nanoTime();
	deltaTime = (time - lastTime) / 1000000000.0f;
	lastTime = time;

	if (time - frameStart >= 1000000000) {
	    fps = frames;
	    frames = 0;
	    frameStart = time;
	}
	frames++;
    }

}
