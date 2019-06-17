package rgl.gameobjects.characters.mobs;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.Dummy;
import rgl.gameobjects.characters.mobs.behavior.PassiveBehaviorStrategy;
import rgl.logic.GameContext;

/**
 * implementation of hostile using an passive strategy
 */
public class PassiveMob extends Hostile {

    public PassiveMob(@NotNull GameContext context) {
        super(context,
                context.getPlayers().values().stream().map(Dummy::getLvl).max(Integer::compareTo).orElse(1),
                new PassiveBehaviorStrategy());
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.HOSTILE_PASS;
    }
}
