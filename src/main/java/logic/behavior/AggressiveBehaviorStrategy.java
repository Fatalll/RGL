package logic.behavior;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class AggressiveBehaviorStrategy implements BehaviorStrategy {

    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        int[][] distanceToPlayerMap = context.getPlayer().getDistanceToPlayerMap();

        int dist = Integer.MAX_VALUE;
        Point nextPosition = position;

        List<Point> possibleSteps = BehaviorStrategy.defaultPossibleSteps(position);
        Collections.shuffle(possibleSteps);

        // find nearest to player position around
        for (Point possibleStep : possibleSteps) {
            if (distanceToPlayerMap[possibleStep.x][possibleStep.y] < dist) {
                dist = distanceToPlayerMap[possibleStep.x][possibleStep.y];
                nextPosition = possibleStep;
            }
        }

        return dist <= aggro ? nextPosition : position;
    }
}
