package game_objects.mobs;

import logic.GameContext;
import logic.behavior.CowardlyBehaviorStrategy;
import org.jetbrains.annotations.NotNull;

public class CowardMob extends Hostile {
    public CowardMob(@NotNull GameContext context) {
        super(context, context.getPlayer().getLvl(), new CowardlyBehaviorStrategy());
    }

    @NotNull
    @Override
    public Character display() {
        return 'C';
    }
}
