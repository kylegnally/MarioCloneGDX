package com.kylenally.mariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.kylenally.mariobros.MarioBros;

/**
 * Created by kyleg on 12/20/2017.
 */

public class Mario extends Sprite {

    public World world;
    public Body b2Body;

    public Mario(World world) {
        this.world = world;
        defineMario();
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
        shape.setRadius(5 / MarioBros.PPM);

        fdef.shape = shape;
        b2Body.createFixture(fdef);
    }
}
