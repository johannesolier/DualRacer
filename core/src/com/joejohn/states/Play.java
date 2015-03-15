package com.joejohn.states;

import static com.joejohn.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.joejohn.entities.Player;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.B2DVars;
import com.joejohn.handlers.Controls;
import com.joejohn.handlers.GameButton;
import com.joejohn.handlers.GameStateManager;
import com.joejohn.handlers.MyContactListener;

public class Play extends GameState {

	protected World world;
	protected Box2DDebugRenderer b2dr;
	protected MyContactListener cl;
	protected TiledMap tileMap;
	protected OrthogonalTiledMapRenderer tmr;
	private Player player;
	private OrthogonalTiledMapRenderer tmRenderer;
	private OrthographicCamera b2dCam;
	private GameButton moveright_button, moveleft_button;
	private final int velocity = 3;
	public static int direction; // RIGHT = 1, LEFT = -1
	public static boolean stopPlayer = false;

	protected final Vector2 gravity;

	public static int level = 2;

	public static boolean moveright = false, moveleft = false;

	public Play(GameStateManager gsm) {
		super(gsm);

		DualRacer.res.getMusic("menu").stop();
		DualRacer.res.getMusic("Theme").play();

		gravity = new Vector2(0, -9.81f);
		world = new World(gravity, true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();

		Texture tex = DualRacer.res.getTexture("movebutton");
		moveright_button = new GameButton(new TextureRegion(tex), DualRacer.WIDTH - 34, 32, hudCam,0);
		moveleft_button = new GameButton(new TextureRegion(tex), DualRacer.WIDTH - 100, 32, hudCam,0);

		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, DualRacer.WIDTH / PPM, DualRacer.HEIGHT / PPM);

		// create player
		createPlayer();
		createLevel();

	}

	public void playerJump() {
		if (cl.isPlayerOnGround()) {
			player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0);
//			player.getBody().applyForceToCenter(0, 250, true);
			player.getBody().applyLinearImpulse(0, 4, player.getBody().getWorldCenter().x, player.getBody().getWorldCenter().y, true);
			DualRacer.res.getSound("jump").play();
		}
	}

	public void checkDirectionChanged(int dir){
		if (player.direction != dir) {
			player.swapTexture();
		}
	}

	public void move(int dx) {
		float yVel = player.getBody().getLinearVelocity().y;
		player.getBody().setLinearVelocity(dx, yVel);
	}

	public void update(float dt) {
		//handleInput();
		world.step(dt, 6, 2);
		
		handleInput();

		player.update(dt);
//		if(player.getBody().getPosition().x > 12)
//			System.out.println("Player won!");7
		
		if(stopPlayer){
			float velY = player.getBody().getLinearVelocity().y;
			player.getBody().setLinearVelocity(0, velY);
			stopPlayer = false;
		}
		
		moveright_button.update(dt);
		moveleft_button.update(dt);
	}

	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cam.position.set(player.getPosition().x * PPM + DualRacer.WIDTH / 4, DualRacer.HEIGHT / 2, 0);
		cam.update();

		tmRenderer.setView(cam);
		tmRenderer.render();

		sb.setProjectionMatrix(cam.combined);
		player.render(sb);

		sb.setProjectionMatrix(hudCam.combined);

		moveright_button.render(sb);
		moveleft_button.render(sb);

//		b2dr.render(world, b2dCam.combined);

	}
	
	public void handleInput(){
		if(moveright_button.isClicked()){
			System.out.println("RIGHT IS CLICKED");
			direction = 1;
		}
		
		if(moveleft_button.isClicked()){
			System.out.println("LEFT IS CLICKED");
			direction = -1;
		}
		
		if(!Controls.isDown())
			direction = 0;
		
		if(direction == 1){
			checkDirectionChanged(direction);
			move(velocity);
		}
		
		if(direction == -1){
			checkDirectionChanged(direction);
			move(-velocity);
		}
	}

	public void dispose() {

	}

	private void createPlayer() {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(160 / PPM, 200 / PPM);
		bdef.fixedRotation = true;
		Body body = world.createBody(bdef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(13 / PPM, 13 / PPM);

		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.density = 1;
		fdef.friction = 1;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		body.createFixture(fdef);
		shape.dispose();

		// create foot sensor
		shape = new PolygonShape();
		shape.setAsBox(8 / PPM, 3 / PPM, new Vector2(0, -13 / PPM), 0);
		fdef.shape = shape;
		fdef.isSensor = true;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		body.createFixture(fdef).setUserData("foot");
		shape.dispose();
		
		player = new Player(body);
		body.setUserData(player);

		MassData md = body.getMassData();
		md.mass = 1;
		body.setMassData(md);
	}

	private void createLevel() {
		try {
			tileMap = new TmxMapLoader().load("res/levels/level" + level + ".tmx");
		} catch (Exception e) {
			System.out.println("Cannot find level: res/levels/level" + level + ".tmx");
			Gdx.app.exit();
		}

		tmRenderer = new OrthogonalTiledMapRenderer(tileMap);

		TiledMapTileLayer layer;
		layer = (TiledMapTileLayer) tileMap.getLayers().get("Tile Layer 1");
		createTile(layer, B2DVars.BIT_GROUND);

	}

	private void createTile(TiledMapTileLayer layer, short bits) {
		float ts = layer.getTileWidth();
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		ChainShape cs = new ChainShape();
		Vector2[] v = new Vector2[3];
		v[0] = new Vector2(-ts / 2 / PPM, -ts / 2 / PPM);
		v[1] = new Vector2(-ts / 2 / PPM, ts / 2 / PPM);
		v[2] = new Vector2(ts / 2 / PPM, ts / 2 / PPM);

		cs.createChain(v);

		FixtureDef fd = new FixtureDef();
		fd.friction = 1;
		fd.shape = cs;
		fd.filter.categoryBits = bits;
		fd.filter.maskBits = B2DVars.BIT_PLAYER;

		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {
				Cell cell = layer.getCell(col, row);
				if (cell == null)
					continue;
				if (cell.getTile() == null)
					continue;
				bdef.position.set((col + 0.5f) * ts / PPM, (row + 0.5f) * ts / PPM);
				world.createBody(bdef).createFixture(fd);
			}
		}
		cs.dispose();
	}
}