package com.genesislabs.pixelspace;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class NyanShip2 extends SimpleActor
{
//	int delta = 150;

	public NyanShip2(TextureRegion region)
	{
		super(region);
	}

	public NyanShip2(String filename)
	{
		super(filename);
	}

	public NyanShip2(TextureRegion region, int x, int y, int width, int height)
	{
		super(region, x, y, width, height);
	}


	public void moveLeft(int delta)
	{
		if(getX() - delta >=0 )
			setX(getX() - delta);
	}

	public void moveRight(int delta)
	{
		if(getX() + getWidth() + delta <= PixelSpace.SCREEN_WIDTH)
			setX(getX() + delta);
	}

	public boolean partlyOverlaps(Rectangle rectangle)
	{
		Rectangle catRectangle = rectangle;
		return this.getRectangle().overlaps(rectangle);
	}

	public  boolean overlaps(SimpleActor actor, int hitZone)
	{
		Rectangle catRect = this.getRectangle();
		catRect.setSize(getWidth() - hitZone * 2, getHeight() - hitZone * 2);
		catRect.setPosition(getX() + hitZone, getY() + hitZone);
		return catRect.overlaps(actor.getRectangle());

	}
}
