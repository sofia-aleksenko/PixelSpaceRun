package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Pool;


public class Obstacle extends AnimatedActor implements Pool.Poolable {
	boolean countScore;

	public Obstacle(Animation animation) {
		super(animation);
		setY(PixelSpace.SCREEN_HEIGHT);
		setWidth(PixelSpace.VIEWPORT_WIDTH/3);
		countScore = false;
		setVisible(false);
	}

	public Obstacle(Animation animation, int x) {
		super(animation);
		setX(x);
		setY(PixelSpace.SCREEN_HEIGHT);
		setWidth(PixelSpace.VIEWPORT_WIDTH/3);
	}
/*	public  Obstacle(String filename)
	{
		super(filename);
		setY(PixelSpace.SCREEN_HEIGHT);
		setWidth(PixelSpace.VIEWPORT_WIDTH/3);
	}*/

	void init(){
		setY(PixelSpace.SCREEN_HEIGHT);
		setVisible(true);
	}

	public void move(int velocity) {
		setY(getY() - velocity * Gdx.graphics.getDeltaTime());
	}

	@Override
	public void reset() {
		countScore = false;
		setY(PixelSpace.SCREEN_HEIGHT);
		setVisible(false);
	}
}
