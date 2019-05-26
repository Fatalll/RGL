package game_objects.mobs;

import game_objects.GameObjectType;
import logic.GameContext;
import logic.behavior.PassiveBehaviorStrategy;
import org.jetbrains.annotations.NotNull;

public class PassiveMob extends Hostile {

    public PassiveMob(@NotNull GameContext context) {
        super(context, context.getPlayer().getLvl(), new PassiveBehaviorStrategy());
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.HOSTILE_PASS;
    }
}
