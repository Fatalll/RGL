package rgl.gameobjects.items;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.stats.StatType;
import rgl.logic.GameContext;

import java.util.Map;

/**
 * Special item type: Ring.
 */
public class RingItem extends Item {

    public RingItem(GameContext ctx, String name, Map<StatType, Integer> prop) {
        super(ctx, name, prop);
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.RINGITEM;
    }
}
