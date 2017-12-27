package com.kylenally.mariobros.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kylenally.mariobros.Sprites.InteractiveTileObject;

/**
 * Created by kyleg on 12/27/2017.
 */

public class WorldContactListener implements ContactListener {

    // called when two fixtures collide
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // test to see if the object is Mario's head; if it is, see if the userdata on that object is null.
        // if it is not null and the object
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }
    }

    // called when two fixtures stop colliding
    @Override
    public void endContact(Contact contact) {

    }

    // changes the collision at time of contact
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    // changes what happens after collision
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
