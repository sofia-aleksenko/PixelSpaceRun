package com.genesislabs.pixelspace;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsScreen implements Screen {
	private PixelSpace game;
	private Stage stage;
	private Label labelCurLvl;
	private int initialLvl;
	private CheckBox isShowTips;

	public SettingsScreen(final PixelSpace game) {
		this.game = game;
		Table table;
		stage = new Stage(game.viewport);
		Group btnGroup = new Group();
		Skin skin = new Skin(game.atlas);
		Gdx.input.setInputProcessor(stage);

		Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.YELLOW);

		final Label labelLevel = new Label("INITIAL LEVEL", labelStyle);
		labelLevel.setScale(0.75f);
		labelLevel.setPosition(PixelSpace.SCREEN_WIDTH/2 - labelLevel.getWidth()/2,
				PixelSpace.SCREEN_HEIGHT - labelLevel.getHeight() - 12);
		stage.addActor(labelLevel);

		Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
		sliderStyle.background = skin.getDrawable("slider");
		sliderStyle.knob = skin.getDrawable("knob");
		final Slider slider = new Slider(1,50,1,false,sliderStyle);
		slider.setWidth(150);
		initialLvl = game.prefs.getInteger("initLevel", 1);
		slider.setValue(initialLvl);
		slider.setPosition(PixelSpace.SCREEN_WIDTH/2 - slider.getWidth()/2,
				labelLevel.getY() - 12 - slider.getHeight());
		slider.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				labelCurLvl.setText(Integer.toString((int)slider.getValue()));
			}
		});

		stage.addActor(slider);

		labelCurLvl = new Label(Integer.toString((int)slider.getValue()), labelStyle);
		labelCurLvl.setPosition(PixelSpace.SCREEN_WIDTH/2 - labelCurLvl.getWidth()/2,
				slider.getY() - 5 - labelCurLvl.getHeight());
		stage.addActor(labelCurLvl);

		CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle(PixelSpace.skin.getDrawable("checkboxUnchecked"),
				PixelSpace.skin.getDrawable("checkboxChecked"), PixelSpace.font, Color.YELLOW);
		isShowTips = new CheckBox("SHOW TIPS", checkBoxStyle);
		isShowTips.getLabelCell().padLeft(10);
		isShowTips.setPosition(PixelSpace.SCREEN_WIDTH/2 - isShowTips.getWidth()/2,
				labelCurLvl.getY() - isShowTips.getHeight() - 12);
		isShowTips.setChecked(game.prefs.getBoolean("isShowTips", true));
		stage.addActor(isShowTips);

		// sign in button
		String signText;
		final String SIGN_IN_TEXT = "SIGN IN", SIGN_OUT_TEXT = "SIGN OUT";
		if(game.googleServices.isSignedIn()) {
			signText = SIGN_OUT_TEXT;
		} else signText = SIGN_IN_TEXT;
		final TextButton btnSign = new TextButton(signText, game.buttonStyle);
		btnSign.setPosition(PixelSpace.SCREEN_WIDTH/2 - btnSign.getWidth()/2, isShowTips.getY()-btnSign.getHeight()-20);
		stage.addActor(btnSign);
		btnSign.addListener(new ClickListener()	{
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if(game.googleServices.isSignedIn()){
					game.googleServices.signOut();
					btnSign.setText(SIGN_IN_TEXT);
				} else {
					game.googleServices.startSignInIntent();
					btnSign.setText(SIGN_OUT_TEXT);
				}
			}
		});



		SimpleActor nyanSkin1, nyanSkin2;
		nyanSkin1 = new SimpleActor(skin.getRegion("nyanship-2"));
		nyanSkin2 = new SimpleActor(skin.getRegion("nyanship-3"));
		nyanSkin1.setScale(0.75f);
		nyanSkin1.setPosition(PixelSpace.VIEWPORT_LEFT+20, labelCurLvl.getY() - nyanSkin1.getHeight()*0.75f-8);
		nyanSkin1.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				game.curSkinName = "nyanship-2";
				return true;
			}
		});

		nyanSkin2.setScale(0.75f);
		nyanSkin2.setPosition(PixelSpace.VIEWPORT_RIGHT-20-nyanSkin2.getWidth()*0.75f, labelCurLvl.getY() - nyanSkin2.getHeight()*0.75f-8);
		nyanSkin2.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				game.curSkinName = "nyanship-3";
				return true;
			}
		});


		TextButton btnBack = new TextButton("BACK", game.buttonStyle);
		btnBack.setPosition(PixelSpace.SCREEN_WIDTH/2 - btnBack.getWidth()/2, 12);
		btnBack.addListener(new ClickListener() {
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				game.prefs.putInteger("initLevel", (int)slider.getValue());
				game.prefs.putBoolean("isShowTips", isShowTips.isChecked());
				game.prefs.flush();
				game.setScreen(new MainMenuScreen(game));
			}
		});
		stage.addActor(btnBack);
	}

	@Override
	public void show(){

	}

	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height){
		game.viewport.update(width, height, true);
	}

	@Override
	public void pause(){

	}

	@Override
	public void resume(){

	}

	@Override
	public void hide(){

	}

	@Override
	public void dispose()
	{
		stage.dispose();
	}
}
