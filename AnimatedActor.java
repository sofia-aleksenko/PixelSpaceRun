package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AnimatedActor extends Actor {
	private final Animation animation;
	private TextureRegion currentFrame;
	private float timeElapsed = 0;

	public AnimatedActor(AnimatedActor animatedActor){
		animation = animatedActor.getAnimation();
		getKeyFrame(0);
		setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
		setBounds(0, 0, getWidth(), getHeight());
	}

	public AnimatedActor (Animation animation){
		this.animation = animation;
		getKeyFrame(0);
		setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
		setBounds(0, 0, getWidth(), getHeight());
	}

	public AnimatedActor(TextureAtlas atlas, float duration, Animation.PlayMode playMode){
		animation = new Animation<TextureRegion>(duration, atlas.getRegions(), playMode);
		getKeyFrame(0);
		setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
		setBounds(0, 0, getWidth(), getHeight());
	}

	public TextureRegion getKeyFrame(float stateTime){
		return currentFrame = (TextureRegion)animation.getKeyFrame(stateTime, true);
	}

	public Animation getAnimation(){
		return animation;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		// Get current frame of animation for the current stateTime
		timeElapsed += Gdx.graphics.getDeltaTime();
		getKeyFrame(timeElapsed);
		if (animation.getPlayMode() == Animation.PlayMode.NORMAL && !animation.isAnimationFinished(timeElapsed)
				|| (animation.getPlayMode() != Animation.PlayMode.NORMAL)) {
			batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		}
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

	public void moveDown(int velocity){
		setY(getY() - velocity * Gdx.graphics.getDeltaTime());
	}
}
