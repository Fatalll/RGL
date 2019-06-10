package logic.behavior;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class CowardlyBehaviorStrategy implements BehaviorStrategy {
    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        int[][] distanceToPlayerMap = context.getPlayer().getDistanceToPlayerMap();

        int dist = -1;
        Point nextPosition = position;
        List<Point> possibleSteps = BehaviorStrategy.defaultPossibleSteps(position);
        Collections.shuffle(possibleSteps);

        for (Point possibleStep : possibleSteps) {
            if (distanceToPlayerMap[possibleStep.x][possibleStep.y] != Integer.MAX_VALUE &&
                    distanceToPlayerMap[possibleStep.x][possibleStep.y] > dist) {
                dist = distanceToPlayerMap[possibleStep.x][possibleStep.y];
                nextPosition = possibleStep;
            }
        }

        int minDist = Integer.MAX_VALUE;
        for (Point possibleStep : possibleSteps) {
            if (distanceToPlayerMap[possibleStep.x][possibleStep.y] < minDist) {
                minDist = distanceToPlayerMap[possibleStep.x][possibleStep.y];
            }
        }

        return minDist <= aggro ? nextPosition : position;
    }
}
