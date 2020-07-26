package com.genesislabs.pixelspace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PixelSpace extends Game {
	public static final float SCREEN_WIDTH = 240f;
	public static final float SCREEN_HEIGHT = 320f;
	public static float VIEWPORT_LEFT;
	public static float VIEWPORT_RIGHT;
	public static float VIEWPORT_WIDTH;

	public TextureAtlas atlas;
	public static Skin skin;
	public static BitmapFont font;
	public TextButton.TextButtonStyle buttonStyle;
	public FileHandle file;
	public static Viewport viewport;
	public OrthographicCamera camera;
	Preferences prefs;
	String curSkinName = "nyanship-2";

	private MainMenuScreen mMainMenuScreen;
	public Array<Integer> highscores;

	public static GoogleServices googleServices;

	public  final int deathForAd = 5;
	public int deathCount = 0;

	public final long adLag = 60000*1;
	public long prevAdTime = 0;

	public PixelSpace() {
		super();
	}

	public PixelSpace(GoogleServices googleServices) {
		this.googleServices = googleServices;
	}

	@Override
	public void create () {

		prefs = Gdx.app.getPreferences("prefs");
		highscores = new Array<Integer>();
		for(int i=0; i<5; i++) {
			String scorenum = "scoreNum" + String.valueOf(i+1);
			highscores.add(prefs.getInteger(scorenum, 0));
		}

		boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
		prefs.putBoolean("isFirstRun", isFirstRun);
		boolean isShowTips = prefs.getBoolean("isShowTips", false) || isFirstRun;
		prefs.putBoolean("isShowTips", isShowTips);
		prefs.flush();
		atlas = new TextureAtlas(Gdx.files.internal("images/images.pack"));
		skin = new Skin(atlas);
		font = new BitmapFont(Gdx.files.internal("fonts/pixeled.fnt"));
		font.setUseIntegerPositions(false);
		font.getData().setScale(0.75f);

		FreeTypeFontGenerator generator;
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Pixeled.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 7;//(int)Math.ceil(10);
		generator.dispose(); // don't forget to dispose to avoid memory leaks!

		Skin skin = new Skin(atlas);
		buttonStyle = new TextButton.TextButtonStyle(skin.getDrawable("menuButtonUp"),
				skin.getDrawable("menuButtonDown"), skin.getDrawable("menuButtonUp"), font);

		camera = new OrthographicCamera();
		viewport = new FillViewport(PixelSpace.SCREEN_WIDTH, PixelSpace.SCREEN_HEIGHT, camera);
		prevAdTime = TimeUtils.millis();
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		float aspectRatio = (float) width / height;
		VIEWPORT_WIDTH = SCREEN_HEIGHT * aspectRatio;

		VIEWPORT_LEFT = (SCREEN_WIDTH - VIEWPORT_WIDTH) / 2;
		VIEWPORT_RIGHT = VIEWPORT_LEFT + VIEWPORT_WIDTH;
		int a = 5;
	}

	@Override
	public void dispose () {
		skin.dispose();
		atlas.dispose();
	}
}
