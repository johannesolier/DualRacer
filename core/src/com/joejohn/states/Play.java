package com.joejohn.states;

import static com.joejohn.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.joejohn.game.DualRacer;
import com.joejohn.handlers.B2DVars;
import com.joejohn.handlers.GameStateManager;
import com.joejohn.handlers.MyContactListener;
import com.joejohn.handlers.MyInput;

public class Play extends GameState {

	protected World world;
	protected Box2DDebugRenderer b2dr;

	protected OrthographicCamera b2dCam;
	
	protected Body playerBody;
	protected MyContactListener cl;
	
	protected TiledMap tileMap;
	protected OrthogonalTiledMapRenderer tmr;

	protected final Vector2 gravity;

	public Play(GameStateManager gsm) {
		super(gsm);

		gravity = new Vector2(0, -9.81f);
		world = new World(gravity, true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();

		// create platform
		createPlatform();

		BodyDef bdef = new BodyDef();
		bdef.position.set(160 / PPM, 120 / PPM);
		bdef.type = BodyType.StaticBody;
		Body body = world.createBody(bdef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(50 / PPM, 5 / PPM);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_GROUND;
		fdef.filter.maskBits = B2DVars.BIT_PLAYER;
		body.createFixture(fdef).setUserData("ground");

		// create player
		createPlayer();

		bdef.position.set(160 / PPM, 200 / PPM);
		bdef.type = BodyType.DynamicBody;
		playerBody = world.createBody(bdef);
		shape.setAsBox(5 / PPM, 5 / PPM);
		fdef.shape = shape;
		fdef.restitution = 0.2f;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		playerBody.createFixture(fdef).setUserData("player");
		
		//create foot sensor
		shape.setAsBox(2 / PPM, 2 / PPM, new Vector2(0, -5 / PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		fdef.isSensor = true;
		playerBody.createFixture(fdef).setUserData("foot");

		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, DualRacer.WIDTH / PPM, DualRacer.HEIGHT / PPM);
		
		//TILES
		tileMap = new TmxMapLoader().load("res/tiles/testmap.tmx");
		tmr = new OrthogonalTiledMapRenderer(tileMap);
		
	}
	
	private void playerJump() {
		if(cl.isPlayerOnGround()) {
			playerBody.setLinearVelocity(playerBody.getLinearVelocity().x, 0);
			playerBody.applyForceToCenter(0, 200, true);
			DualRacer.res.getSound("jump").play();
		}
	}

	public void handleInput() {

		if(MyInput.isPressed(MyInput.JUMP)){
			if(cl.isPlayerOnGround()){
				playerJump();
			}
		}
		
		if(MyInput.isPressed()) {
			if(MyInput.x < Gdx.graphics.getWidth() / 2) {
				playerJump();
			}
		}
		
	}

	public void update(float dt) {
		handleInput();
		world.step(dt, 6, 2);
	}

	public void render() {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		tmr.setView(cam);
		tmr.render();
		
		b2dr.render(world, b2dCam.combined);
	}

	public void dispose() {

	}


	private void createPlayer() {
	}

	private void createPlatform() {

	}

}
