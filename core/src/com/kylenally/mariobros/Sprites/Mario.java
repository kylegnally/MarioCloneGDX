package com.kylenally.mariobros.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.kylenally.mariobros.MarioBros;
import com.kylenally.mariobros.Screens.PlayScreen;

/**
 * Created by kyleg on 12/20/2017.
 */

public class Mario extends Sprite {

    public World world;
    public Body b2Body;
    private TextureRegion marioStand;
    public enum State {
        FALLING,
        JUMPING,
        STANDING,
        RUNNING,
        GROWING,
        DEAD
    }
    public State currentState;
    public State previousState;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private boolean runningRight;
    private float stateTimer;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;

    public Mario(PlayScreen screen) {

        // find the region of the texture map associated with Little Mario
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        for (int i = 4; i < 6; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        // define where Little Mario starts and how much sprite space he takes up
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        defineMario();


        // scale the little bastard
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);

        // plonk him in place
        setRegion(marioStand);
    }

    public void update(float dt) {

        // bind the position of the collider with the position of the sprite we're using
        // (the math is for the offset of the sprite from the center of the b2body)
        if (marioIsBig) {
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        } else {
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(dt));
        if (timeToDefineBigMario) {
            defineBigMario();
        }

        if (timeToRedefineMario) {
            redefineMario();
        }
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
                default:
                    region = marioIsBig ? bigMarioStand : marioStand;
                    break;
        }

        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (marioIsDead) {
            return State.DEAD;
        } else if (runGrowAnimation) {
            return State.GROWING;
        } else if (b2Body.getLinearVelocity().y > 0 ||
                b2Body.getLinearVelocity().y < 0 &&
                        previousState == State.JUMPING) {
            return State.JUMPING;
        } else if (b2Body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2Body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else return State.STANDING;
    }

    public void grow() {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    // method to define Mario and his collision position, size, what shape
    // to use when colliding, and creating his fixture
    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(220 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        // sensor for mario's head. Line between two points
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM,6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM,6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData(this);
    }

    public void defineBigMario() {

        Vector2 currentPosition = b2Body.getPosition();
        world.destroyBody(b2Body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2Body.createFixture(fdef).setUserData(this);
        // sensor for mario's head. Line between two points
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM,6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM,6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData("head");
        timeToDefineBigMario = false;
    }

    public void hit() {
        if (marioIsBig) {
            marioIsBig = false;
            timeToRedefineMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
        } else {
            MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;
            for (Fixture fixture : b2Body.getFixtureList()) {
                fixture.setFilterData(filter);
            }
            b2Body.applyLinearImpulse(new Vector2(0, 4f), b2Body.getWorldCenter(), true);
        }
    }

    public void redefineMario() {
        Vector2 position = b2Body.getPosition();
        world.destroyBody(b2Body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        // sensor for mario's head. Line between two points
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM,6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM,6 / MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;
    }

    public boolean isBig() {
        return marioIsBig;
    }
}
