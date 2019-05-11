package logic.behavior;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class PassiveBehaviorStrategy implements BehaviorStrategy {
    @Override
    public @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro) {
        return position;
    }
}
