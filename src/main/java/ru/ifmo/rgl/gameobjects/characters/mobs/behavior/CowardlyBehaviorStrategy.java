package ru.ifmo.rgl.gameobjects.characters.mobs.behavior;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.logic.GameContext;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * coward behavior strategy, mob is running from player
 */
public class CowardlyBehaviorStrategy implements BehaviorStrategy {
    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        int[][] distanceToPlayerMap = context.getDistanceMap();

        int dist = -1;
        Point nextPosition = position;
        List<Point> possibleSteps = BehaviorStrategy.defaultPossibleSteps(position);
        Collections.shuffle(possibleSteps);

        // find farthest position to player around
        for (Point possibleStep : possibleSteps) {
            if (distanceToPlayerMap[possibleStep.y][possibleStep.x] != Integer.MAX_VALUE &&
                    distanceToPlayerMap[possibleStep.y][possibleStep.x] > dist) {
                dist = distanceToPlayerMap[possibleStep.y][possibleStep.x];
                nextPosition = possibleStep;
            }
        }

        // check if player in aggro distance
        int minDist = Integer.MAX_VALUE;
        for (Point possibleStep : possibleSteps) {
            if (distanceToPlayerMap[possibleStep.y][possibleStep.x] < minDist) {
                minDist = distanceToPlayerMap[possibleStep.y][possibleStep.x];
            }
        }

        return minDist <= aggro ? nextPosition : position;
    }
}
