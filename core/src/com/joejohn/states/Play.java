package com.joejohn.states;

import static com.joejohn.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.joejohn.entities.Player;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.B2DVars;
import com.joejohn.handlers.GameStateManager;
import com.joejohn.handlers.MyContactListener;
import com.joejohn.handlers.MyInput;

public class Play extends GameState {

	protected World world;
	protected Box2DDebugRenderer b2dr;

	protected OrthographicCamera b2dCam;

	protected MyContactListener cl;

	protected TiledMap tileMap;
	protected OrthogonalTiledMapRenderer tmr;

	protected final Vector2 gravity;

	private Player player;

	public static int level = 1;

	private int tileMapWidth;
	private int tileMapHeight;

	private int tileSize;
	private OrthogonalTiledMapRenderer tmRenderer;

	public Play(GameStateManager gsm) {
		super(gsm);

		DualRacer.res.getMusic("menu").stop();
		DualRacer.res.getMusic("Theme").play();

		gravity = new Vector2(0, -9.81f);
		world = new World(gravity, true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();

		// create player
		createPlayer();
		createLevel();

	}

	private void playerJump() {
		if (cl.isPlayerOnGround()) {
			player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0);
			player.getBody().applyForceToCenter(0, 200, true);
			DualRacer.res.getSound("jump").play();
		}
	}

	public void handleInput() {
		if (MyInput.isPressed(MyInput.JUMP)) {
			if (cl.isPlayerOnGround()) {
				playerJump();
			}
		}

		if (MyInput.isPressed()) {
			if (MyInput.x < Gdx.graphics.getWidth() / 2) {
				playerJump();
			}
			if (MyInput.x > Gdx.graphics.getWidth() / 2) {
				if (MyInput.moveRight() == 1) {
					player.getBody().setLinearVelocity(5, 0);
				} else if (MyInput.moveRight() == -1) {
					player.getBody().setLinearVelocity(-5, 0);
				}
			}
		}
	}

	public void update(float dt) {
		handleInput();
		world.step(dt, 6, 2);

		player.update(dt);
	}

	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		tmRenderer.setView(cam);
		tmRenderer.render();

		sb.setProjectionMatrix(cam.combined);
		player.render(sb);

		b2dr.render(world, b2dCam.combined);
	}

	public void dispose() {

	}

	private void createPlayer() {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(160 / PPM, 200 / PPM);
		Body body = world.createBody(bdef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(32 / PPM, 32 / PPM);

		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.restitution = 0.2f;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		body.createFixture(fdef).setUserData("player");

		// create foot sensor
		shape.setAsBox(32 / PPM, 2 / PPM, new Vector2(0, -32 / PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");

		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, DualRacer.WIDTH / PPM, DualRacer.HEIGHT / PPM);

		player = new Player(body);
	}

	private void createLevel() {
		try {
			tileMap = new TmxMapLoader().load("res/levels/level" + level + ".tmx");
		} catch (Exception e) {
			System.out.println("Cannot find level: res/levels/level" + level + ".tmx");
			Gdx.app.exit();
		}

		tileMapWidth = tileMap.getProperties().get("width", Integer.class);
		tileMapHeight = tileMap.getProperties().get("height", Integer.class);
		tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
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
		fd.friction = 0;
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
