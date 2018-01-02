package com.kylenally.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kylenally.mariobros.MarioBros;
import com.kylenally.mariobros.Scenes.Hud;
import com.kylenally.mariobros.Screens.PlayScreen;
import com.kylenally.mariobros.Sprites.Items.ItemDef;
import com.kylenally.mariobros.Sprites.Items.Mushroom;
import com.kylenally.mariobros.Sprites.Mario;
import com.kylenally.mariobros.Sprites.TileObjects.InteractiveTileObject;


/**
 * Created by kyleg on 12/23/2017.
 */

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {

        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin", "Collision");
        if (getCell().getTile().getId() == BLANK_COIN)
        {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef
                        (new Vector2(body.getPosition().x,
                                body.getPosition().y + 16 / MarioBros.PPM),
                                Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();

            } else {
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);

    }
}
