package ru.ifmo.rgl.gameobjects.items;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObjectType;
import ru.ifmo.rgl.gameobjects.characters.stats.StatType;
import ru.ifmo.rgl.logic.GameContext;

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
