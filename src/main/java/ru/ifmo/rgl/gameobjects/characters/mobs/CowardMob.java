package ru.ifmo.rgl.gameobjects.characters.mobs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObjectType;
import ru.ifmo.rgl.gameobjects.characters.Dummy;
import ru.ifmo.rgl.gameobjects.characters.mobs.behavior.CowardlyBehaviorStrategy;
import ru.ifmo.rgl.logic.GameContext;

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
