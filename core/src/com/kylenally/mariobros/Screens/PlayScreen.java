package com.kylenally.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kylenally.mariobros.MarioBros;
import com.kylenally.mariobros.Scenes.Hud;
import com.kylenally.mariobros.Sprites.Enemies.Enemy;
import com.kylenally.mariobros.Sprites.Items.Item;
import com.kylenally.mariobros.Sprites.Items.ItemDef;
import com.kylenally.mariobros.Sprites.Items.Mushroom;
import com.kylenally.mariobros.Sprites.Mario;
import com.kylenally.mariobros.Tools.B2WorldCreator;
import com.kylenally.mariobros.Tools.WorldContactListener;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kyleg on 12/19/2017.
 */

public class PlayScreen implements Screen {

    // Reference to our game (used to set screens)
    private MarioBros game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;

    // playscreen vars
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    // tiles map vars
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // sprites
    private Mario player;

    // music variable
    private Music music;

    // box2D vars
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(MarioBros game) {

        atlas = new TextureAtlas("Mario_and_enemies.pack");
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

        // center the camera on the viewport at z height 0
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // create the world
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        // create the world
        creator = new B2WorldCreator(this);
        // create Mario
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);

        // uncomment to play the music
        //music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // call the update method and pass it the delta
        update(delta);

        // color and alpha
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render the map
        renderer.render();

        // render the box2d lines
        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getGoombas()) {
            enemy.draw(game.batch);
        }

        for (Item item : items) {
            item.draw(game.batch);
        }

        game.batch.end();

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
        handleSpawningItems();

        // these control CPU/physics updating per screen update
        world.step(1/60f, 6, 2);

        // update the player
        player.update(dt);

        // update the enemies

        // update the time
        hud.update(dt);

        // set the camera to the position of the player
        gameCam.position.x = player.b2Body.getPosition().x;

        // update the camera
        gameCam.update();

        // set the view of the renderer on the camera
        renderer.setView(gameCam);

        for (Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        for (Item item : items) {
            item.update(dt);
        }

    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return  world;
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

        // clean things up
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();

    }
}
