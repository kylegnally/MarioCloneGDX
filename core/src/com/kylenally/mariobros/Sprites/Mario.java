package com.kylenally.mariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.kylenally.mariobros.MarioBros;
import com.kylenally.mariobros.Screens.PlayScreen;

/**
 * Created by kyleg on 12/20/2017.
 */

public class Mario extends Sprite {

    public World world;
    public Body b2Body;

    private TextureRegion marioStand;

    public Mario(World world, PlayScreen screen) {

        // find the region of the texture map associated with Little Mario
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = world;
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
    }

    // method to define Mario and his collision position, size, what shape
    // to use when colliding, and creating his fixture
    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        fdef.shape = shape;
        b2Body.createFixture(fdef);
    }
}
