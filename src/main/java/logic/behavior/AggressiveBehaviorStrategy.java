package logic.behavior;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AggressiveBehaviorStrategy implements BehaviorStrategy {
    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        int[][] playerMap = context.getPlayer().getPlayerMap();

        int dist = Integer.MAX_VALUE;
        Point nextPosition = position;

        if (playerMap[position.x + 1][position.y] < dist) {
            dist = playerMap[position.x + 1][position.y];
            nextPosition = new Point(position.x + 1, position.y);
        }

        if (playerMap[position.x - 1][position.y] < dist) {
            dist = playerMap[position.x - 1][position.y];
            nextPosition = new Point(position.x - 1, position.y);
        }

        if (playerMap[position.x][position.y + 1] < dist) {
            dist = playerMap[position.x][position.y + 1];
            nextPosition = new Point(position.x, position.y + 1);
        }

        if (playerMap[position.x][position.y - 1] < dist) {
            dist = playerMap[position.x][position.y - 1];
            nextPosition = new Point(position.x, position.y - 1);
        }

        return dist <= aggro ? nextPosition : position;
    }
}
