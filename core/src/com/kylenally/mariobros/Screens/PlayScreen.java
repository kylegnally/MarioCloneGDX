package com.kylenally.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kylenally.mariobros.MarioBros;
import com.kylenally.mariobros.Scenes.Hud;
import com.kylenally.mariobros.Sprites.Mario;

/**
 * Created by kyleg on 12/19/2017.
 */

public class PlayScreen implements Screen {

    private MarioBros game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Mario player;

    private World world;
    private Box2DDebugRenderer b2dr;

    public PlayScreen(MarioBros game) {

        this.game = game;

        // create a camera
        gameCam = new OrthographicCamera();

        // create a viewport and fit the camera to the viewport height and width
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM,
                MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);

        // create a new HUD
        hud =  new Hud(game.batch);

        // instantiate a map loader object
        mapLoader = new TmxMapLoader();

        // pass the map loader our level tileset
        map = mapLoader.load("level1.tmx");

        // render the map with an instance of the renderer, passing it
        // our map
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);

        // create the world
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        // create Mario
        player = new Mario(world);

        // center the camera on the viewport at z height 0
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // create the world

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // create the ground bodies/fixtures
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // create the pipe bodies/fixtures
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // create the brick bodies/fixtures
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // create the coin bodies/fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // apply the gameport
        gamePort.apply();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // call the update method and pass it the delta
        update(delta);

        // set the view of the renderer to the camera
        renderer.setView(gameCam);

        // color and alpha
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render the map
        renderer.render();

        // render the box2d lines
        b2dr.render(world, gameCam.combined);

        // project the hud and the stage through the camera and combine them
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        // draw the HUD
        hud.stage.draw();


    }

    public void handleInput(float dt) {

        // check for key presses and apply a linear impulse. .isKeyJustPressed
        // allows us to have a jump that's NOT flying (ie constant velocity)
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true);
        }

        // the next two look for a key that is being held down and applies the motion during the
        // time the player holds the key (yes, you can use DPAD here as well)
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2Body.getLinearVelocity().x <= 2) {
            player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2Body.getLinearVelocity().x >= -2) {
            player.b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2Body.getWorldCenter(), true);
        }
    }

    public void update(float dt) {

        handleInput(dt);

        // these control CPU/physics updating per screen update
        world.step(1/60f, 6, 2);

        gameCam.position.x = player.b2Body.getPosition().x;
        gameCam.update();
        renderer.setView(gameCam);

    }

    @Override
    public void resize(int width, int height) {

        gamePort.update(width, height);

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
