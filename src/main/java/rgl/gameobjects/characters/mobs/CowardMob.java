package rgl.gameobjects.characters.mobs;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.mobs.behavior.CowardlyBehaviorStrategy;
import rgl.logic.GameContext;

/**
 * implementation of hostile using an cowardly strategy
 */
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
