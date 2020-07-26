package com.genesislabs.pixelspace;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

public class NyanShip extends AnimatedActor {
	private Rectangle hitZone;

	public NyanShip(TextureAtlas atlas, float duration) {
		super(atlas, duration, Animation.PlayMode.LOOP);
		hitZone = getRectangle();
	}

	public void setHitZone(int leftIndent, int  rightIndent, int topIndent, int bottomIndent){
		hitZone.setPosition(getX(), getY());
		hitZone.setWidth(hitZone.getWidth() - leftIndent - rightIndent);
		hitZone.setHeight(hitZone.getHeight() - topIndent - bottomIndent);
		hitZone.setX(hitZone.getX() + leftIndent);
		hitZone.setY(hitZone.getY() + bottomIndent);
	}

	public void moveLeft(int delta) {
		if(getX() - delta >=0 ) {
			setX(getX() - delta);
			hitZone.setX(hitZone.getX() - delta);
		}

	}

	public void moveRight(int delta) {
		if(getX() + getWidth() + delta <= PixelSpace.SCREEN_WIDTH) {
			setX(getX() + delta);
			hitZone.setX(hitZone.getX() + delta);
		}
	}

	public boolean partlyOverlaps(Rectangle rectangle) {
		Rectangle catRectangle = rectangle;
		return getRectangle().overlaps(rectangle);
	}

	public  boolean overlaps(SimpleActor actor, int hitZone) {
		Rectangle shipRect = getRectangle();
		shipRect.setSize(getWidth() - hitZone * 2, getHeight() - hitZone * 2);
		shipRect.setPosition(getX() + hitZone, getY() + hitZone);
		return shipRect.overlaps(actor.getRectangle());
	}

	public  boolean overlaps(SimpleActor actor) {
		return hitZone.overlaps(actor.getRectangle());
	}

	public  boolean overlaps(AnimatedActor actor) {
		return hitZone.overlaps(actor.getRectangle());
	}

	public Rectangle getRectangle() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
}
