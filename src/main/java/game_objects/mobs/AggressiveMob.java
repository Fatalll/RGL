package game_objects.mobs;

import game_objects.GameObjectType;
import logic.GameContext;
import logic.behavior.AggressiveBehaviorStrategy;
import org.jetbrains.annotations.NotNull;

public class AggressiveMob extends Hostile {
    public AggressiveMob(@NotNull GameContext context) {
        super(context, context.getPlayer().getLvl(), new AggressiveBehaviorStrategy());
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.HOSTILE_AGR;
    }
}
