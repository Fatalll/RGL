package ru.ifmo.rgl.gameobjects.characters.mobs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObjectType;
import ru.ifmo.rgl.gameobjects.characters.Dummy;
import ru.ifmo.rgl.gameobjects.characters.mobs.behavior.AggressiveBehaviorStrategy;
import ru.ifmo.rgl.logic.GameContext;

/**
 * implementation of hostile using an aggressive strategy
 */
public class AggressiveMob extends Hostile {
    public AggressiveMob(@NotNull GameContext context) {
        super(context,
                context.getPlayers().values().stream().map(Dummy::getLvl).max(Integer::compareTo).orElse(1),
                new AggressiveBehaviorStrategy());
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.HOSTILE_AGR;
    }
}
