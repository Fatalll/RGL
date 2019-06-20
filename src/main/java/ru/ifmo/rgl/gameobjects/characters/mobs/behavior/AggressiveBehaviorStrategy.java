package ru.ifmo.rgl.gameobjects.characters.mobs.behavior;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.logic.GameContext;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * behavior for aggressive strategy, mob is chasing player
 */
public class AggressiveBehaviorStrategy implements BehaviorStrategy {

    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        int[][] distanceToPlayerMap = context.getDistanceMap();

        int dist = Integer.MAX_VALUE;
        Point nextPosition = position;

        List<Point> possibleSteps = BehaviorStrategy.defaultPossibleSteps(position);
        Collections.shuffle(possibleSteps);

        // find nearest to player position around
        for (Point possibleStep : possibleSteps) {
            if (distanceToPlayerMap[possibleStep.y][possibleStep.x] < dist) {
                dist = distanceToPlayerMap[possibleStep.y][possibleStep.x];
                nextPosition = possibleStep;
            }
        }

        return dist <= aggro ? nextPosition : position;
    }
}
