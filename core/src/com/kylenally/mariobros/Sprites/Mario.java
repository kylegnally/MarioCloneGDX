package com.kylenally.mariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
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
        RUNNING
    }
    public State currentState;
    public State previousState;
    private Animation<TextureRegion> marioRun;
    private Animation<TextureRegion> marioJump;
    private boolean runningRight;
    private float stateTimer;

    public Mario(PlayScreen screen) {

        // find the region of the texture map associated with Little Mario
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }

        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for (int i = 4; i < 6; i++) {
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        }

        marioJump = new Animation<TextureRegion>(0.1f, frames);

        defineMario();

        // define where Little Mario starts and how much sprite space he takes up
        marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16);

        // scale the little bastard
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);

        // plonk him in place
        setRegion(marioStand);
    }

    public void update(float dt) {

        // bind the position of the collider with the position of the sprite we're using
        // (the math is for the offset of the sprite from the center of the b2body)
        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
                default:
                    region = marioStand;
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
        if (b2Body.getLinearVelocity().y > 0 ||
                b2Body.getLinearVelocity().y < 0 &&
                        previousState == State.JUMPING) {
            return State.JUMPING;
        } else if (b2Body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2Body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else return State.STANDING;
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
        b2Body.createFixture(fdef);

        // sensor for mario's head. Line between two points
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM,6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM,6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData("head");
    }
}
