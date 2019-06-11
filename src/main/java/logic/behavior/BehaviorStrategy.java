package logic.behavior;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

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

    @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro);
}
