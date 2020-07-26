package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {
	private final PixelSpace game;
//	private OrthographicCamera camera;
//	private Viewport viewport;
	private Stage stage;

	public MainMenuScreen(final PixelSpace game) {

		this.game = game;

	}

	void initStage(){
		stage = new Stage(PixelSpace.viewport);
		Gdx.input.setInputProcessor(stage);
		Table table = new Table();
		table.setFillParent(true);

		Group details = new Group();

		SimpleActor background = new SimpleActor(PixelSpace.skin.getRegion("menuBackground"));

		AnimatedActor pipesLeft = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/pipes1.pack")), 0.1f, Animation.PlayMode.LOOP);
		pipesLeft.setPosition(33, 59);
		details.addActor(pipesLeft);
		AnimatedActor pipesRight = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/pipes2.pack")), 0.1f, Animation.PlayMode.LOOP);
		pipesRight.setPosition(160, 59);
		details.addActor(pipesRight);
		AnimatedActor display = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/display.pack")), 0.1f, Animation.PlayMode.LOOP);
		display.setPosition(98, 264);
		details.addActor(display);
		AnimatedActor lamp = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/lamp.pack")), 0.1f, Animation.PlayMode.LOOP);
		lamp.setPosition(102, 42);
		details.addActor(lamp);

		AnimatedActor flash11 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flash1.pack")), 0.1f, Animation.PlayMode.LOOP);
		flash11.setPosition(5, 7);
		details.addActor(flash11);

		AnimatedActor flash21 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flash2.pack")), 0.1f, Animation.PlayMode.LOOP);
		flash21.setPosition(231, 7);

		AnimatedActor flash31 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flash3.pack")), 0.1f, Animation.PlayMode.LOOP);
		flash31.setPosition(5, 307);

		AnimatedActor flash41 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flash4.pack")), 0.1f, Animation.PlayMode.LOOP);
		flash41.setPosition(231, 307);

		AnimatedActor cogs = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/cogs.pack")), 0.1f, Animation.PlayMode.LOOP);
		cogs.setPosition(53, 185);
		details.addActor(cogs);

		AnimatedActor littleDisplay1 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/displayLittle.pack")), 0.1f, Animation.PlayMode.LOOP);
		littleDisplay1.setPosition(64, 265);
		details.addActor(littleDisplay1);

		AnimatedActor littleDisplay2 = new AnimatedActor(littleDisplay1);
		littleDisplay2.setPosition(153, 265);
		details.addActor(littleDisplay2);

		AnimatedActor littleDisplay3 = new AnimatedActor(littleDisplay1);
		littleDisplay3.setPosition(64, 36);
		details.addActor(littleDisplay3);

		AnimatedActor littleDisplay4 = new AnimatedActor(littleDisplay1);
		littleDisplay4.setPosition(153, 36);
		details.addActor(littleDisplay4);

		AnimatedActor flashBig1 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flashBig1.pack")), 0.1f, Animation.PlayMode.LOOP);
		flashBig1.setPosition(33, 21);
		details.addActor(flashBig1);

		AnimatedActor flashBig2 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flashBig2.pack")), 0.1f, Animation.PlayMode.LOOP);
		flashBig2.setPosition(33, 267);
		details.addActor(flashBig2);

		AnimatedActor flashBig3 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flashBig3.pack")), 0.1f, Animation.PlayMode.LOOP);
		flashBig3.setPosition(179, 267);
		details.addActor(flashBig3);

		AnimatedActor flashBig4 = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/flashBig4.pack")), 0.1f, Animation.PlayMode.LOOP);
		flashBig4.setPosition(179, 21);
		details.addActor(flashBig4);

		AnimatedActor brick = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/brick.pack")), 0.1f, Animation.PlayMode.LOOP);
		brick.setPosition(118, 305);
		details.addActor(brick);


		TextButton btnPlay = new TextButton("PLAY", game.buttonStyle);
		btnPlay.addListener(new ClickListener() {
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				dispose();
				game.setScreen(new GameScreen(game));

			};
		});
		TextButton btnSettings = new TextButton("SETTINGS", game.buttonStyle);
		btnSettings.addListener(new ClickListener() {
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				dispose();
				game.setScreen(new SettingsScreen(game));
			}
		});

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(PixelSpace.skin.getDrawable("menuButtonDetailedUp"),
				PixelSpace.skin.getDrawable("menuButtonDetailedDown"), PixelSpace.skin.getDrawable("menuButtonDetailedUp"), game.font);
		TextButton btnScores = new TextButton("HIGHSCORES", textButtonStyle);
		btnScores.addListener(new ClickListener() {
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				dispose();
				game.setScreen(new HighscoresScreen(game));

			}
		});

		table.add(btnPlay).pad(1);
		table.row();
		table.add(btnSettings).pad(1);
		table.row();
		table.add(btnScores).pad(1);
		table.setPosition(table.getX(), table.getY()+8);
		stage.addActor(background);
		stage.addActor(table);
		stage.addActor(details);
	}

	@Override
	public void show() {
		initStage();

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
		initStage();
	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
