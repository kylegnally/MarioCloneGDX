package com.kylenally.mariobros.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kylenally.mariobros.MarioBros;
import com.kylenally.mariobros.Sprites.Enemies.Enemy;
import com.kylenally.mariobros.Sprites.Items.Item;
import com.kylenally.mariobros.Sprites.Mario;
import com.kylenally.mariobros.Sprites.TileObjects.InteractiveTileObject;

/**
 * Created by kyleg on 12/27/2017.
 */

public class WorldContactListener implements ContactListener {

    // called when two fixtures collide
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // test to see if the object is Mario's head; if it is, see if the userdata on that object is null.
        // if it is not null and the object
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        // check if things | collide with things
        switch (cDef) {
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT){
                    ((Enemy)fixA.getUserData()).hitOnHead();
                } else {
                    ((Enemy)fixB.getUserData()).hitOnHead();
                }
                break;

            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT){
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;

            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                Gdx.app.log("MARIO", "DIED");
                break;

            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;

            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                } else {
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                }
                break;
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
