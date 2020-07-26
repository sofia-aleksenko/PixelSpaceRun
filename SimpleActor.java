package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SimpleActor extends Actor {
	protected final TextureRegion region;

	public SimpleActor() {
		super();
		region = new TextureRegion();
	}

	public SimpleActor(TextureRegion region) {
		this.region = region;
		setSize(region.getRegionWidth(), region.getRegionHeight());
		setBounds(0, 0, getWidth(), getHeight());
	}

	public SimpleActor(String filename) {
		this.region = new TextureRegion(new Texture(filename));
		setSize(region.getRegionWidth(), region.getRegionHeight());
		setBounds(0, 0, getWidth(), getHeight());
	}

	public SimpleActor(TextureRegion region, int x, int y, int width, int height) {
		this.region = new TextureRegion(region, x, y, width, height);
		setSize(region.getRegionWidth(), region.getRegionHeight());
		setBounds(0, 0, getWidth(), getHeight());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}

	public Rectangle getRectangle(){
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public boolean overlaps(SimpleActor actor){
		return this.getRectangle().overlaps(actor.getRectangle());
	}

	public boolean overlaps(AnimatedActor actor){
		return this.getRectangle().overlaps(actor.getRectangle());
	}

	public boolean overlaps(Rectangle rectangle){
		return this.getRectangle().overlaps(rectangle);
	}

	public void verticalArrange(Actor actor) {
		setX(actor.getX()+actor.getWidth()/2 - this.getWidth()/2);
	}

	void moveDown(int velocity) {
		setY(getY() - velocity * Gdx.graphics.getDeltaTime());
	}

	void setRepeatWrap(){

	}
}
