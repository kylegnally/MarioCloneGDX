package com.kylenally.mariobros.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.kylenally.mariobros.Screens.PlayScreen;

/**
 * Created by kyleg on 12/30/2017.
 */

public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {

        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();

        velocity = new Vector2(1f, -1.5f);
        b2body.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void update(float dt);
    public abstract void hitOnHead();

    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }

        if (y){
            velocity.y = -velocity.y;
        }
    }

}
