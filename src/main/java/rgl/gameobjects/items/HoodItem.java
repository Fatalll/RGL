package rgl.gameobjects.items;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.stats.StatType;
import rgl.logic.GameContext;

import java.util.Map;

/**
 * Special item type: Hood.
 */
public class HoodItem extends Item {

    public HoodItem(GameContext ctx, String name, Map<StatType, Integer> prop) {
        super(ctx, name, prop);
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.HOODITEM;
    }
}
