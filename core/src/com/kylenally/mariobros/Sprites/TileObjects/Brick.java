package com.kylenally.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.kylenally.mariobros.MarioBros;
import com.kylenally.mariobros.Scenes.Hud;
import com.kylenally.mariobros.Screens.PlayScreen;
import com.kylenally.mariobros.Sprites.Mario;
import com.kylenally.mariobros.Sprites.TileObjects.InteractiveTileObject;

/**
 * Created by kyleg on 12/23/2017.
 */

public class Brick extends InteractiveTileObject {

    public Brick(PlayScreen screen, MapObject object) {

        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            Gdx.app.log("Brick", "Collision");
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
