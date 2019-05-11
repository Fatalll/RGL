package logic.behavior;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CowardlyBehaviorStrategy implements BehaviorStrategy {
    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        int[][] playerMap = context.getPlayer().getPlayerMap();

        int dist = -1;
        Point nextPosition = position;

        if (playerMap[position.x + 1][position.y] != Integer.MAX_VALUE && playerMap[position.x + 1][position.y] > dist) {
            dist = playerMap[position.x + 1][position.y];
            nextPosition = new Point(position.x + 1, position.y);
        }

        if (playerMap[position.x - 1][position.y] != Integer.MAX_VALUE && playerMap[position.x - 1][position.y] > dist) {
            dist = playerMap[position.x - 1][position.y];
            nextPosition = new Point(position.x - 1, position.y);
        }

        if (playerMap[position.x][position.y + 1] != Integer.MAX_VALUE && playerMap[position.x][position.y + 1] > dist) {
            dist = playerMap[position.x][position.y + 1];
            nextPosition = new Point(position.x, position.y + 1);
        }

        if (playerMap[position.x][position.y - 1] != Integer.MAX_VALUE && playerMap[position.x][position.y - 1] > dist) {
            dist = playerMap[position.x][position.y - 1];
            nextPosition = new Point(position.x, position.y - 1);
        }


        int minDist = Integer.MAX_VALUE;
        if (playerMap[position.x + 1][position.y] < dist) {
            minDist = playerMap[position.x + 1][position.y];
        }

        if (playerMap[position.x - 1][position.y] < dist) {
            minDist = playerMap[position.x - 1][position.y];
        }

        if (playerMap[position.x][position.y + 1] < dist) {
            minDist = playerMap[position.x][position.y + 1];
        }

        if (playerMap[position.x][position.y - 1] < dist) {
            minDist = playerMap[position.x][position.y - 1];
        }

        return minDist <= aggro ? nextPosition : position;
    }
}
