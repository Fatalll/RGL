package logic.behavior;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface BehaviorStrategy {
    @NotNull Point step(@NotNull GameContext context, @NotNull Point position, int aggro);
}
