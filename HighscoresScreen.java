package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HighscoresScreen implements Screen {
	private final PixelSpace game;
	private Stage stage;

	public HighscoresScreen(final PixelSpace game){
		this.game = game;
		stage = new Stage(PixelSpace.viewport);
		Gdx.input.setInputProcessor(stage);

		Table table = new Table();
		table.setFillParent(true);

		Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.YELLOW);

		// title
		Label.LabelStyle titleStyle = new Label.LabelStyle(labelStyle);
		Label title = new Label("YOUR BEST SCORES", titleStyle);
		table.add(title).pad(15);
		table.row();


		Table scoreTable = new Table();
		int i = 1;
		for(Integer score: game.highscores)	{
			Label label = new Label(String.valueOf(score), labelStyle);
			scoreTable.add(new Label(String.valueOf(i) + ". ", labelStyle));
			scoreTable.add(label);
			scoreTable.row();
			i++;
		}
		table.add(scoreTable).padBottom(20);
		table.row();



		TextButton btnLeaderboard = new TextButton("WORLD RATING", game.buttonStyle);
		btnLeaderboard.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.googleServices.showScore();
			}
		});

		TextButton btnAchievements = new TextButton("ACHIEVEMENTS", game.buttonStyle);
		btnAchievements.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.googleServices.showAchievement();
			}
		});

		TextButton btnBack = new TextButton("BACK", game.buttonStyle);
		btnBack.addListener(new ClickListener() {
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new MainMenuScreen(game));
			}
		});

		table.add(btnLeaderboard).pad(1);
		table.row();
		table.add(btnAchievements).pad(1);
		table.row();
		table.add(btnBack).pad(1);

		stage.addActor(table);

	}

	@Override
	public void show() {

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
		game.viewport.update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}