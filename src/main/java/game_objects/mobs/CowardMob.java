package game_objects.mobs;

import game_objects.GameObjectType;
import logic.GameContext;
import logic.behavior.CowardlyBehaviorStrategy;
import org.jetbrains.annotations.NotNull;

public class CowardMob extends Hostile {
    public CowardMob(@NotNull GameContext context) {
        super(context, context.getPlayer().getLvl(), new CowardlyBehaviorStrategy());
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.HOSTILE_COWARD;
    }
}
