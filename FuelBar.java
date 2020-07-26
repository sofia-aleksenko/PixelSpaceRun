package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class FuelBar extends Actor{
	private TextureRegion bar, emptyPart;
	private Array<TextureRegion>emptyParts;
	private int fuelAmount = GameScreen.FUEL_MAX;
	private int fuelMax = GameScreen.FUEL_MAX;
	private Animation changeAnimation;
	private TextureRegion currentFrame;
	private float timeElapsed = 0;
	private boolean changing = false;
	private float emptyPartPosX;

	public FuelBar(TextureRegion bar, TextureRegion emptyPart, TextureAtlas changeAnimation){
		this.bar = bar;
		emptyParts = new Array<TextureRegion>();

		for(int i=0; i<=fuelMax; i++){
			TextureRegion region = new TextureRegion(emptyPart,i, 0, fuelMax-i, emptyPart.getRegionHeight());
			emptyParts.add(region);
		}
		this.changeAnimation = new Animation<TextureRegion>(0.15f, changeAnimation.getRegions(), Animation.PlayMode.LOOP);
		this.fuelAmount = fuelMax;
		this.emptyPart = emptyParts.get(fuelAmount);
		setSize(bar.getRegionWidth(), bar.getRegionHeight());
		setBounds(0, 0, getWidth(), getHeight());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
			timeElapsed += Gdx.graphics.getDeltaTime();
			getKeyFrame(timeElapsed);
		batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

		batch.draw(emptyPart, emptyPartPosX, getY() + 6, getOriginX(), getOriginY(),
				emptyPart.getRegionWidth(), emptyPart.getRegionHeight(), getScaleX(), getScaleY(), getRotation());
	}

	public void setFuelAmount(int fuelAmount){
		if(this.fuelAmount<=fuelMax) {
			this.fuelAmount = fuelAmount;
			emptyPart = emptyParts.get(fuelAmount);
			emptyPartPosX = getX() + 6 + fuelAmount;
		}
	}

	public TextureRegion getKeyFrame(float stateTime){
		return currentFrame = (TextureRegion)changeAnimation.getKeyFrame(stateTime, true);
	}

	public float getEmptyPartPosX(){
		return emptyPartPosX;
	}

}

