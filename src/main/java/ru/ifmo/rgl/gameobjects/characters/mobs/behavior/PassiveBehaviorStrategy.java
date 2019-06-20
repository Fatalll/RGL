package ru.ifmo.rgl.gameobjects.characters.mobs.behavior;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.logic.GameContext;

import java.awt.*;

/**
 * passive behavior strategy, mob is standing in one place
 */
public class PassiveBehaviorStrategy implements BehaviorStrategy {
    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        return position;
    }
}
