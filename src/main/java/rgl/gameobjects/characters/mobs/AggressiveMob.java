package rgl.gameobjects.characters.mobs;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.mobs.behavior.AggressiveBehaviorStrategy;
import rgl.logic.GameContext;

/**
 * implementation of hostile using an aggressive strategy
 */
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
