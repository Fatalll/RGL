package rgl.gameobjects.characters.mobs.behavior;

import org.jetbrains.annotations.NotNull;
import rgl.logic.GameContext;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * interface of AI strategy for mobs
 */
public interface BehaviorStrategy {
    // possible steps for mobs for each strategy
    static List<Point> defaultPossibleSteps(Point position) {
        return Arrays.asList(
                new Point(position.x + 1, position.y),
                new Point(position.x - 1, position.y),
                new Point(position.x, position.y + 1),
                new Point(position.x, position.y - 1)
        );
    }

    /**
     * calculate next mob step based on current position and aggro radius
     *
     * @param context  game context
     * @param position current position
     * @param aggro    aggro radius
     * @return next position
     */
    @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro);
}
