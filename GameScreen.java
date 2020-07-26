package com.genesislabs.pixelspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

class GameScreen implements Screen {
	private final PixelSpace game;
	//constants
	final static int FUEL_MAX = 100;
	private final int FUEL_POINT_PER_BOX = 40;
	private final int DISTANCE_BETWEEN_OBSTACLES = 140;
	private int FUEL_WASTE_INTERVAL = DISTANCE_BETWEEN_OBSTACLES/5;

	// general
	private enum State {START, PLAY, PAUSE, STOP, PREPARE, BANG, OUTOFFUEL}
	private State gameState = State.START, prevGameState = State.START;

	private Integer score = 0, scoreCount = 0;
	private Integer level = 1, initLevel = 1;
	private int velocity;
	private int delta;
	long startDur;

	//stage
	private Stage stage;
	private final Skin skin;
	private Group backgroundGroup, mainGroup, uiGroup, obstacleGroup, hintGroup;
	private Table menuTable;
	private NyanShip nyanShip;
	private Background background;
	private AnimatedActor bang;
	private long bangTime = 0, countdownTime = 0, startTime = 0;

	//ui
	private Texture transparentTexture;
	private Button pauseResume;
	private ImageButton controlBtnLeft, controlBtnRight;
	private TextButton btnExit;
	private AnimatedActor outOfFuelMessage;
	private Label scoreLabel;
	private Label lvlLabel;
	private Label countdownLabel;
	private Color color;

	//hints
	private boolean isFirstStart = false, isFirstObstacle = false, isFirstFuel = false;
	private BlinkingActor arrowRedLeft, arrowRedRight;
	private BlinkingActor arrowGreenLeft, arrowGreenRight, arrowFuelBox = null, arrowFuelBar = null;
	private BlinkingActor arrowOrangeLeft, arrowOrangeRight;
	private BlinkingActor alertLeft, alertRight;
	private BlinkingActor skull1, skull2, skull3;

	//fuel
	private FuelBar fuelBar;
	private AnimatedActor fuelBox;
	private int obstacleCount = 0, fuelBoxCount = 0;
	private float fuelWasteCount = 0;
	private int fuel = FUEL_MAX;
	private int collectedBoxes = 0;

	// obstacles
	private Array<Obstacle>obstacles;
	private Animation obstacleAnimation1, obstacleAnimation2, obstacleAnimation3;
	private int randomCount = 0, lastObstaclePosition = 0;

	private final Pool<Obstacle> obstaclePool2 = new Pool<Obstacle>() {
		@Override
		protected Obstacle newObject() {
			return new Obstacle(obstacleAnimation2);
		}
	};

	private final Pool<Obstacle> obstaclePool3 = new Pool<Obstacle>() {
		@Override
		protected Obstacle newObject() {
			return new Obstacle(obstacleAnimation3);
		}
	};

	private final Pool<Obstacle> obstaclePool1 = new Pool<Obstacle>() {
		@Override
		protected Obstacle newObject() {
			return new Obstacle(obstacleAnimation1);
		}
	};

	public GameScreen(final PixelSpace game) {
		this.game = game;

		color = new Color(0, 0, 0.5f, 1);
		level = game.prefs.getInteger("initLevel", 1);
		initLevel = level;
		velocity = 75 + (level-1)*12;
		stage = new Stage(PixelSpace.viewport);

		uiGroup = new Group(); // always on top
		mainGroup = new Group();
		backgroundGroup = new Group();
		obstacleGroup = new Group();
		hintGroup = new Group();
		stage.addActor(backgroundGroup); // add first

		stage.addActor(obstacleGroup);
		stage.addActor(mainGroup); // add before uiGroup
		stage.addActor(hintGroup);
		stage.addActor(uiGroup); // important: add last

		delta = (int) PixelSpace.VIEWPORT_WIDTH / 3;
		skin = PixelSpace.skin;

		// car actor

		nyanShip = new NyanShip(new TextureAtlas(Gdx.files.internal("images/nyanShipGreen.pack")), 0.1f);
		nyanShip.setPosition(PixelSpace.SCREEN_WIDTH/2 - nyanShip.getWidth()/2, PixelSpace.SCREEN_HEIGHT*0.25f);
		nyanShip.setHitZone(5, 5, 5, 24);

		// obstacle array actor
		obstacles = new Array<Obstacle>();
		obstacleAnimation1 = new Animation<TextureRegion>(0.1f, new TextureAtlas(Gdx.files.internal("images/obstacle1.pack")).getRegions(),
		Animation.PlayMode.LOOP);
		obstacleAnimation2 = new Animation<TextureRegion>(0.1f, new TextureAtlas(Gdx.files.internal("images/obstacle2.pack")).getRegions(),
		Animation.PlayMode.LOOP);
		obstacleAnimation3 = new Animation<TextureRegion>(0.1f, new TextureAtlas(Gdx.files.internal("images/obstacle3.pack")).getRegions(),
		Animation.PlayMode.LOOP);


		// background actor
		background = new Background(Gdx.files.internal("images/background_space.png"));
		background.setPosition(0, 0);
		backgroundGroup.addActor(background);

		// control buttons
		Pixmap pixmap = new Pixmap(110, 200, Pixmap.Format.Alpha);
		transparentTexture = new Texture(pixmap);
		pixmap.dispose();
		Drawable transparentDrawable = new TextureRegionDrawable(new TextureRegion(transparentTexture));

		ImageButton.ImageButtonStyle imageButtonStyleLeft = new ImageButton.ImageButtonStyle(transparentDrawable, transparentDrawable, transparentDrawable,
		skin.getDrawable("buttonLeft-up"),skin.getDrawable("buttonLeft-down"), skin.getDrawable("buttonLeft-up"));
		ImageButton.ImageButtonStyle imageButtonStyleRight = new ImageButton.ImageButtonStyle(transparentDrawable, transparentDrawable, transparentDrawable,
		skin.getDrawable("buttonRight-up"),skin.getDrawable("buttonRight-down"), skin.getDrawable("buttonRight-up"));

		controlBtnLeft = new ImageButton(imageButtonStyleLeft);
		controlBtnLeft.align(Align.bottomLeft);
		controlBtnLeft.padBottom(12);
		controlBtnLeft.padLeft(PixelSpace.VIEWPORT_LEFT + 12);
		controlBtnLeft.setPosition(0,0);

		controlBtnRight = new ImageButton(imageButtonStyleRight);
		controlBtnRight.align(Align.bottomRight);
		controlBtnRight.padBottom(12);
		controlBtnRight.padRight(PixelSpace.SCREEN_WIDTH- PixelSpace.VIEWPORT_RIGHT + 12);
		controlBtnRight.setPosition(PixelSpace.SCREEN_WIDTH-controlBtnRight.getWidth(),0);
		uiGroup.addActor(controlBtnLeft);
		uiGroup.addActor(controlBtnRight);

		controlBtnLeft.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				nyanShip.moveLeft(delta);

				return true;
			}
		});

		controlBtnRight.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				nyanShip.moveRight(delta);
				return true;
			}
		});

		pauseResume = new Button(skin.getDrawable("pause"));
		pauseResume.setPosition(PixelSpace.VIEWPORT_LEFT + 19, PixelSpace.SCREEN_HEIGHT - pauseResume.getHeight() - 19);

		pauseResume.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				switch (gameState) {
				default:
					setPause();
					break;

				case PAUSE:
					setResume();
					break;
				}
				return true;
			}
		});

		btnExit = new TextButton("EXIT", game.buttonStyle);
		btnExit.addListener(new ClickListener() {
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				setGameState(State.STOP);
			}
		});

		TextButton btnResume = new TextButton("RESUME", game.buttonStyle);
		btnResume.addListener(new ClickListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				switch (gameState) {
				default:
					setPause();
					break;

				case PAUSE:
					setResume();
					break;
				}
				return true;
			}
		});


		outOfFuelMessage = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/outOfFuel.pack")), 0.5f, Animation.PlayMode.LOOP);
		outOfFuelMessage.setPosition(PixelSpace.SCREEN_WIDTH/2 - outOfFuelMessage.getWidth()/2,
		PixelSpace.SCREEN_HEIGHT/2 - outOfFuelMessage.getHeight()/2);
		outOfFuelMessage.setVisible(false);
		uiGroup.addActor(outOfFuelMessage);

		menuTable = new Table();
		menuTable.setPosition(PixelSpace.VIEWPORT_LEFT,0);
		menuTable.setSize(PixelSpace.VIEWPORT_WIDTH, PixelSpace.SCREEN_HEIGHT);
		menuTable.add(btnResume);
		menuTable.row();
		menuTable.add(btnExit);
		menuTable.row();
		menuTable.setVisible(false);

		//fuel bar
		fuelBar = new FuelBar(skin.getRegion("fuelBar"), skin.getRegion("fuelBarEmpty"),
		new TextureAtlas(Gdx.files.internal("images/fuelBarChange.pack")));
		fuelBar.setPosition(pauseResume.getX()+ pauseResume.getWidth()+10, pauseResume.getY()+ pauseResume.getHeight()/2-fuelBar.getHeight()/2);
		uiGroup.addActor(fuelBar);

		fuelBox = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/fuelBox.pack")), 0.07f, Animation.PlayMode.LOOP);
		fuelBox.setVisible(false);
		fuelBox.setPosition(0,0);
		mainGroup.addActor(fuelBox);


		// bang actors
		bang = new AnimatedActor(new TextureAtlas(Gdx.files.internal("images/explosion.pack")), 0.07f, Animation.PlayMode.NORMAL);

		// score
		score = 0;
		initLevel = level = game.prefs.getInteger("initLevel", 1);

		Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.YELLOW);
		scoreLabel = new Label("score: " + score, labelStyle);
		scoreLabel.setScale(0.5f);
		//		scoreLabel.setPosition(Gdx.graphics.getWidth()/2 - scoreLabel.getWidth()/2, 50);
		scoreLabel.setPosition(PixelSpace.SCREEN_WIDTH/2 - scoreLabel.getWidth()/2, 5);
		lvlLabel = new Label("level " + level, labelStyle);

		lvlLabel.setScale(0.5f);
		lvlLabel.setPosition(PixelSpace.SCREEN_WIDTH/2 - lvlLabel.getWidth()/2, scoreLabel.getY() + scoreLabel.getHeight() + 5);

		countdownLabel = new Label("", labelStyle);
		countdownLabel.setFontScale(2.5f);
		countdownLabel.setPosition(PixelSpace.SCREEN_WIDTH/2 - countdownLabel.getWidth()/2, nyanShip.getY() + nyanShip.getHeight() + 25);
		countdownLabel.setVisible(false);
		uiGroup.addActor(countdownLabel);

		arrowOrangeLeft = new BlinkingActor(skin.getRegion("arrowOrangeLeft"), 0.5f);
		arrowOrangeLeft.setOrigin(arrowOrangeLeft.getWidth(), 0);
		arrowOrangeLeft.setRotation(50f);
		arrowOrangeLeft.setPosition(PixelSpace.VIEWPORT_LEFT + 40 , 80);
		hintGroup.addActor(arrowOrangeLeft);

		arrowOrangeRight = new BlinkingActor(skin.getRegion("arrowOrangeRight"), 0.5f);
		arrowOrangeRight.setRotation(-50f);
		arrowOrangeRight.setPosition(PixelSpace.VIEWPORT_RIGHT - 40 - arrowOrangeRight.getWidth(), 80);
		hintGroup.addActor(arrowOrangeRight);

		arrowRedLeft = new BlinkingActor(skin.getRegion("arrowRedLeft"), 0.3f);
		arrowRedLeft.setOrigin(arrowOrangeLeft.getWidth(), 0);
		arrowRedLeft.setRotation(-45f);
		arrowRedLeft.setPosition(0, PixelSpace.SCREEN_HEIGHT*100);
		hintGroup.addActor(arrowRedLeft);

		arrowRedRight = new BlinkingActor(skin.getRegion("arrowRedRight"), 0.3f);
		arrowRedRight.setRotation(45f);
		arrowRedRight.setPosition(0, PixelSpace.SCREEN_HEIGHT*100);
		hintGroup.addActor(arrowRedRight);

		arrowGreenLeft = new BlinkingActor(skin.getRegion("arrowGreenLeft"), 0.3f);
		arrowGreenLeft.setOrigin(arrowGreenLeft.getWidth(), 0);
		arrowGreenLeft.setRotation(-45f);
		arrowGreenLeft.setPosition(PixelSpace.VIEWPORT_LEFT + 40 , 80);
		hintGroup.addActor(arrowGreenLeft);
		arrowGreenLeft.setVisible(false);

		arrowGreenRight = new BlinkingActor(skin.getRegion("arrowGreenRight"), 0.3f);
		arrowGreenRight.setRotation(45f);
		arrowGreenRight.setPosition(PixelSpace.VIEWPORT_LEFT + 40 , 80);
		hintGroup.addActor(arrowGreenRight);
		arrowGreenRight.setVisible(false);

		alertLeft = new BlinkingActor(skin.getRegion("alert"), 0.3f);
		hintGroup.addActor(alertLeft);
		alertLeft.setPosition(0, PixelSpace.SCREEN_HEIGHT*100);
		alertRight = new BlinkingActor(skin.getRegion("alert"), 0.3f);
		hintGroup.addActor(alertRight);
		alertRight.setPosition(0, PixelSpace.SCREEN_HEIGHT*100);

		skull1 = new BlinkingActor(skin.getRegion("skull"), 0.3f);
		skull1.setPosition(0, PixelSpace.SCREEN_HEIGHT*100);
		hintGroup.addActor(skull1);
		skull1.setVisible(false);
		skull2 = new BlinkingActor(skin.getRegion("skull"), 0.3f);
		skull2.setPosition(0, PixelSpace.SCREEN_HEIGHT*100);
		hintGroup.addActor(skull2);
		skull2.setVisible(false);
		skull3 = new BlinkingActor(skin.getRegion("skull"), 0.3f);
		skull3.setPosition(0, PixelSpace.SCREEN_HEIGHT*100);
		hintGroup.addActor(skull3);
		skull3.setVisible(false);


		arrowFuelBox = arrowGreenLeft;
		arrowFuelBar = arrowGreenRight;

		mainGroup.addActor(nyanShip);
		// always on top
		uiGroup.addActor(scoreLabel);
		uiGroup.addActor(lvlLabel);
		uiGroup.addActor(pauseResume);
		uiGroup.addActor(menuTable);
	}

	@Override
	public void show(){
		Gdx.input.setInputProcessor(stage);
		fuelWasteCount = 0;
		bangTime = 0;
		randomCount = 0;
		lastObstaclePosition = 0;
		score = 0;
		scoreCount = 0;
		obstacleCount = 0;
		fuelBoxCount = 0;
		if(game.prefs.getBoolean("isShowTips") || game.prefs.getBoolean("isFirstRun")) {
			isFirstStart = true;
			isFirstObstacle = true;
			isFirstFuel = true;
		}
		level = game.prefs.getInteger("initLevel", 1);
		lvlLabel.setText("level " + level);

		countdownTime = TimeUtils.millis();
		startTime = TimeUtils.millis();
		if(!isFirstFuel) hintGroup.remove();
		if(isFirstStart)
		startDur = 5000;
		else startDur = 3000;
		setGameState(State.START);
		fuelBar.setVisible(false);
	}

	private void update() {
		switch (gameState) {
		case START:
			background.moveDown(velocity);
			if (TimeUtils.millis() - startTime > startDur) {
				if(isFirstStart) {
					arrowOrangeLeft.remove();
					arrowOrangeRight.remove();
				}
				setGameState(State.PLAY);
				if(isFirstObstacle) {
					//	arrowRedLeft.setVisible(true);
					//	alertLeft.setVisible(true);
				}
				spawnObstacle();
				fuelBar.setVisible(true);
			}
			break;

		case PLAY:
			background.moveDown(velocity);
			if(isFirstObstacle) {
				skull1.moveDown(velocity);
				if(skull1.getY() + skull1.getHeight() < 0) {
					skull1.remove();
				}

				skull2.moveDown(velocity);
				if(skull2.getY() + skull2.getHeight() < 0) {
					skull2.remove();
				}

				skull3.moveDown(velocity);
				if(skull3.getY() + skull3.getHeight() < 0) {
					skull3.remove();
					isFirstObstacle = false;
				}

			}

			if(isFirstFuel){
				if(arrowFuelBox != null) {
					arrowFuelBox.moveDown(velocity);
				}
				if(arrowFuelBar != null){
					arrowFuelBar.setX(fuelBar.getEmptyPartPosX()-10);
				}

			}
			Obstacle lastObstacle = obstacles.get(obstacles.size-1);
			if (PixelSpace.SCREEN_HEIGHT - (lastObstacle.getY()) > DISTANCE_BETWEEN_OBSTACLES)
			spawnObstacle();

			Iterator<Obstacle> iter = obstacles.iterator();
			while (iter.hasNext()) {
				Obstacle obstacle = iter.next();
				// every obstacle moves
				obstacle.move(velocity);

				// score
				if (nyanShip.getY() > (obstacle.getY() + obstacle.getHeight()) && !obstacle.countScore)	{
					scoreCount++;
					score+=level;
					obstacle.countScore = true;
					scoreLabel.setText("score: " + score.toString());
					if (scoreCount%10 == 0) {
						velocity += 12;
						level ++;
						lvlLabel.setText("level " + level.toString());
					}
					if(scoreCount == 10) {
//							game.googleServices.unlockAchievement(game.googleServices.getResourceString("achievement_test"));
					}
					if(scoreCount%5==0){
//							game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_test_incremental"));
					}

					// hardcore survivor achievement
					if(level - initLevel >= AchievementsConstants.hardcore_survivor_i){
						game.googleServices.unlockAchievement(game.googleServices.getResourceString("achievement_hardcore_survivor_i"));
					}

					if(level - initLevel >= AchievementsConstants.hardcore_survivor_ii){
						game.googleServices.unlockAchievement(game.googleServices.getResourceString("achievement_hardcore_survivor_ii"));
					}

					if(level - initLevel >= AchievementsConstants.hardcore_survivor_iii){
						game.googleServices.unlockAchievement(game.googleServices.getResourceString("achievement_hardcore_survivor_iii"));
					}

				}

				// overlapping (game over)
				if (nyanShip.overlaps(obstacle)) {
					bang();
				}

				// remove obstacle if out of screen
				if (obstacle.getY() + obstacle.getHeight() < 0) {

					if(obstacle.getAnimation() == obstacleAnimation1) {
						obstaclePool1.free(obstacle);
					}
					else if(obstacle.getAnimation() == obstacleAnimation2) {
						obstaclePool2.free(obstacle);
					}
					else if(obstacle.getAnimation() == obstacleAnimation3) {
						obstaclePool3.free(obstacle);
					}
					iter.remove();
				}
			}

			fuelWasteCount += velocity * Gdx.graphics.getDeltaTime() ;
			if(fuelWasteCount >= FUEL_WASTE_INTERVAL){

				fuel-=1;
				fuelBar.setFuelAmount(fuel);
				fuelWasteCount = 0;
				if(isFirstFuel){
					if(fuel < FUEL_MAX/2)
					arrowFuelBar.setDrawable(skin.getDrawable("arrowRedRight"));
					else
					arrowFuelBar.setDrawable(skin.getDrawable("arrowGreenRight"));
				}
				if(fuel<=0)
				outOfFuel();
			}

			if(fuelBox.isVisible()) fuelBox.moveDown(velocity);

			// fuelBox
			if (fuelBox.isVisible() && nyanShip.overlaps(fuelBox)) {
				// pixel extreme achievement
				if(fuel < FUEL_MAX/5) {
					game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_pixel_extreme_iii"));
					game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_pixel_extreme_ii"));
					game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_pixel_extreme_i"));
				}
				fuel += Math.min(FUEL_POINT_PER_BOX, FUEL_MAX - fuel);
				fuelBar.setFuelAmount(fuel);
				fuelBox.setVisible(false);
				if(isFirstFuel) {
					arrowGreenLeft.setVisible(false);
					collectedBoxes++;
					if (collectedBoxes >= 3) {
						arrowGreenLeft.remove();
						arrowGreenRight.remove();
						isFirstFuel = false;
						if(game.prefs.getBoolean("isFirstRun")) {
							game.prefs.putBoolean("isFirstRun", false);
							game.prefs.putBoolean("isShowTips", false);
							game.prefs.flush();
						}
					}
				}
				// fuel collector achievement
				game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_fuel_collector_iii"));
				game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_fuel_collector_ii"));
				game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_fuel_collector_i"));


			}
			break;

		case PREPARE:
			countdown(3, 2000);
			menuTable.setVisible(false);
			break;

		case BANG:
			// wait for 2 sec and go to main menu
			if (TimeUtils.millis() - bangTime > 2000)
			setGameState(State.STOP);
			break;

		case OUTOFFUEL:
			// wait for 5 sec and go to main menu
			if (TimeUtils.millis() - bangTime > 3300)
			setGameState(State.STOP);
			break;

		case STOP:
			refreshHighscores();
			game.deathCount++;
			if(game.deathCount >= game.deathForAd &&
					TimeUtils.millis() - game.prevAdTime >= game.adLag) {
				game.deathCount = 0;
				game.prevAdTime = TimeUtils.millis();
				game.googleServices.showInterstitialAd(new Runnable() {
					@Override
					public void run() {
						//						System.out.println("Interstitial app closed");
						dispose();
						game.setScreen(new MainMenuScreen(game));
					}
				});
			}
			else {
				game.setScreen(new MainMenuScreen(game));
			}
			break;

		case PAUSE:
			break;

		}
	}

	@Override
	public void render(float delta)	{
		Gdx.gl.glClearColor(color.r, color.g, color.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		update();
	}

	private void spawnObstacle() {
		int obstaclePos = MathUtils.random(0, 2);
		if(obstaclePos == lastObstaclePosition) randomCount++;
		else randomCount = 0;
		if(randomCount>2)
		while(obstaclePos == lastObstaclePosition)
		obstaclePos = MathUtils.random(0, 2);
		int randomTexture = MathUtils.random(0, 2);
		Animation animation;
		Obstacle obstacle;
		switch (randomTexture){
		case 0:
		default:
			obstacle = obstaclePool1.obtain();
			animation = obstacleAnimation1;
			break;
		case 1:
			obstacle = obstaclePool2.obtain();
			animation = obstacleAnimation2;
			break;
		case 2:
			obstacle = obstaclePool3.obtain();
			animation = obstacleAnimation3;
			break;
		}

		lastObstaclePosition = obstaclePos;
		obstacle.init();
		obstacle.setX(PixelSpace.VIEWPORT_LEFT + PixelSpace.VIEWPORT_WIDTH*((float)obstaclePos/3f));
		obstacles.add(obstacle);
		obstacleGroup.addActor(obstacle);

		obstacleCount++;
		if(obstacleCount%6 == 0){
			fuelBoxCount++;
			int fuelBoxPos = MathUtils.random(0, 2);
			if(fuelBoxPos == obstaclePos) {
				switch(fuelBoxPos) {
				case 0:
					fuelBoxPos = MathUtils.random(1, 2);
					break;
				case 1:
					fuelBoxPos += MathUtils.randomSign();
					break;
				case 2:
					fuelBoxPos = MathUtils.random(0, 1);
					break;
				}

			}
			fuelBox.setX(PixelSpace.VIEWPORT_LEFT + PixelSpace.VIEWPORT_WIDTH*((float)fuelBoxPos/3f) +
			PixelSpace.VIEWPORT_WIDTH/3/2-fuelBox.getWidth()/2);
			fuelBox.setY(PixelSpace.SCREEN_HEIGHT);
			fuelBox.setVisible(true);

			if(isFirstFuel){
				switch (fuelBoxPos){

				case 0:
					arrowFuelBox.setPosition(fuelBox.getX() + fuelBox.getWidth()/4,
					PixelSpace.SCREEN_HEIGHT - fuelBox.getHeight() - arrowFuelBox.getHeight()/2);
					break;
					
				case 1:
					arrowFuelBox.setPosition(fuelBox.getX() + fuelBox.getWidth()/4,
					PixelSpace.SCREEN_HEIGHT - fuelBox.getHeight() - arrowFuelBox.getHeight()/2);
					break;
					
				case 2:								
					arrowFuelBox.setPosition(fuelBox.getX()  + fuelBox.getWidth()/4,
					PixelSpace.SCREEN_HEIGHT - fuelBox.getHeight() - arrowFuelBox.getHeight()/2);
					break;
				}
				arrowFuelBar.setPosition(fuelBar.getEmptyPartPosX()-10, fuelBar.getY() - arrowFuelBar.getHeight()-5);
				arrowGreenLeft.setVisible(true);
				arrowGreenRight.setVisible(true);
			}
		}

		if(isFirstObstacle && obstacleCount == 1) {
			skull1.setPosition(obstacle.getX() + obstacle.getWidth()/2 - skull1.getWidth()/2,
			obstacle.getY() + obstacle.getHeight()/2 - skull1.getHeight()/2);
			skull1.setVisible(true);
		}

		if(isFirstObstacle && obstacleCount == 2) {
			skull2.setPosition(obstacle.getX() + obstacle.getWidth()/2 - skull2.getWidth()/2,
			obstacle.getY() + obstacle.getHeight()/2 - skull2.getHeight()/2);
			skull2.setVisible(true);
		}

		if(isFirstObstacle && obstacleCount == 3) {
			skull3.setPosition(obstacle.getX() + obstacle.getWidth()/2 - skull3.getWidth()/2,
			obstacle.getY() + obstacle.getHeight()/2 - skull3.getHeight()/2);
			skull3.setVisible(true);
		}
	}

	private void refreshHighscores() {
		for(int i=0; i<game.highscores.size; i++)
		{
			if(score > game.highscores.get(i))
			{
				for(int j=game.highscores.size-1; j>i; j--)
				{
					game.highscores.set(j, game.highscores.get(j-1));
				}
				game.highscores.set(i, score);
				break;
			}
		}
		for(int i=0; i<game.highscores.size; i++) {
			String scorenum = "scoreNum" + String.valueOf(i+1);
			game.prefs.putInteger(scorenum, game.highscores.get(i));
		}
		game.prefs.flush();
		game.googleServices.submitScore(score);
	}

	private void bang() {
		bang.setPosition(nyanShip.getX()+ nyanShip.getWidth()/2 - bang.getWidth()/2, nyanShip.getY() + nyanShip.getHeight()/2 - bang.getHeight() / 2);
		mainGroup.addActor(bang);
		nyanShip.setVisible(false);
		bangTime = TimeUtils.millis();
		// bang bang achievement
		game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_bang_bang_iii"));
		game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_bang_bang_ii"));
		game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_bang_bang_i"));

		setGameState(State.BANG);
	}

	private void outOfFuel() {
		outOfFuelMessage.setVisible(true);
		bangTime = TimeUtils.millis();
		// relax achevement
		game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_relax_iii"));
		game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_relax_ii"));
		game.googleServices.incrementAchievement(game.googleServices.getResourceString("achievement_relax_i"));
		setGameState(State.OUTOFFUEL);
	}

	private void countdown(Integer count, long duration) {
		controlBtnLeft.setTouchable(Touchable.disabled);
		controlBtnRight.setTouchable(Touchable.disabled);
		float countDur = duration / count;
		countdownLabel.setText(count.toString());
		countdownLabel.setVisible(true);
		for(Integer i=count-1; i>0; i--){
			if(TimeUtils.millis() - countdownTime > countDur * (count - i)){
				countdownLabel.setText(i.toString());
			}
		}
		if (TimeUtils.millis() - countdownTime > duration) {
			countdownLabel.setVisible(false);
			controlBtnLeft.setTouchable(Touchable.enabled);
			controlBtnRight.setTouchable(Touchable.enabled);
			pauseResume.getStyle().up = skin.getDrawable("pause");
			setGameState(State.PLAY);
		}

	}

	private void setGameState(State newState){
		prevGameState = gameState;
		gameState = newState;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		setPause();
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		stage.dispose();
		transparentTexture.dispose();
		obstacles.clear();
	}

	private void setPause() {
		switch (gameState) {
		case PLAY:
		case PREPARE:
			setGameState(State.PAUSE);
			controlBtnLeft.setTouchable(Touchable.disabled);
			controlBtnRight.setTouchable(Touchable.disabled);
			pauseResume.getStyle().up = skin.getDrawable("resume");
			countdownLabel.setVisible(false);
			menuTable.setVisible(true);
			break;

		case START:
			setGameState(State.PAUSE);
			controlBtnLeft.setTouchable(Touchable.disabled);
			controlBtnRight.setTouchable(Touchable.disabled);
			pauseResume.getStyle().up = skin.getDrawable("resume");
			menuTable.setVisible(true);
			startDur -= (TimeUtils.millis() - startTime);
			break;
		}
	}

	private void setResume(){
		if(prevGameState == State.START){
			startTime = TimeUtils.millis();
			menuTable.setVisible(false);
			controlBtnLeft.setTouchable(Touchable.enabled);
			controlBtnRight.setTouchable(Touchable.enabled);
			pauseResume.getStyle().up = skin.getDrawable("pause");
			setGameState(State.START);
		}
		else {
			countdownTime = TimeUtils.millis();
			setGameState(State.PREPARE);
		}
	}
}
