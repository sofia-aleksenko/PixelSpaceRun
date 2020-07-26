package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class Background extends Actor {
	private Texture texture;

	public Background(FileHandle file) {
		texture = new Texture(file);
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
	}


	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(texture, 0, 0, (int)getX(), (int)getY(), (int) PixelSpace.SCREEN_WIDTH, (int) PixelSpace.SCREEN_HEIGHT);
	}


	void moveDown(int velocity)
	{
		setY(getY() - velocity * Gdx.graphics.getDeltaTime());
	}

}
