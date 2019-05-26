package game_objects.items;

import game_objects.GameObjectType;
import game_objects.Stat;
import game_objects.StatDiff;
import game_objects.StatType;
import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
