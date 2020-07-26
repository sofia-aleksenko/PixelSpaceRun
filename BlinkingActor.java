package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class BlinkingActor extends Image {

	private float fadeTime;
	private float timeElapsed;

	BlinkingActor(TextureRegion region, float fadeTime){
		super(region);
		addBlinking(fadeTime);
	}

	BlinkingActor (Texture texture){
		super(texture);
		addBlinking(fadeTime);
	}

	BlinkingActor (Skin skin, String drawableName){
		super(skin, drawableName);
		addBlinking(fadeTime);
	}

	private void addBlinking(float fadeTime){
		addAction(Actions.forever(Actions.sequence(Actions.fadeIn(fadeTime), Actions.fadeOut(fadeTime))));
	}

	public void moveDown(int velocity){
		setY(getY() - velocity * Gdx.graphics.getDeltaTime());
	}

}
