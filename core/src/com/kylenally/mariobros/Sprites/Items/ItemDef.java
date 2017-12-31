package com.kylenally.mariobros.Sprites.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by kyleg on 12/31/2017.
 */

public class ItemDef {

    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }

}
