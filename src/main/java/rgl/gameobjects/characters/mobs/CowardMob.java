package rgl.gameobjects.characters.mobs;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.Dummy;
import rgl.gameobjects.characters.mobs.behavior.CowardlyBehaviorStrategy;
import rgl.logic.GameContext;

/**
 * implementation of hostile using an cowardly strategy
 */
public class CowardMob extends Hostile {
    public CowardMob(@NotNull GameContext context) {
        super(context,
                context.getPlayers().values().stream().map(Dummy::getLvl).max(Integer::compareTo).orElse(1),
                new CowardlyBehaviorStrategy());
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.HOSTILE_COWARD;
    }
}
