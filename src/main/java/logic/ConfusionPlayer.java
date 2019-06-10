package logic;

import game_objects.Player;
import gui.PlayerControl;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static gui.PlayerControl.Control.*;

public class ConfusionPlayer extends Player {
    private static final float CONFUSION_CHANCE = 0.15f;
    private Random random = new Random();
    private int timeout = 0;

    public ConfusionPlayer(@NotNull GameContext context, int lvl) {
        super(context, lvl);
    }

    public void confuse(int timeout) {
        if (Math.random() < CONFUSION_CHANCE) {
            this.timeout = timeout;
            context.updateGameStatus("Hostile casted 'Confusion'!");
        }
    }

    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        if (timeout > 0) {
            timeout--;

            switch (action) {
                case UP:
                    action = new PlayerControl.Control[]{UP, LEFT, RIGHT}[random.nextInt(3)];
                    break;
                case DOWN:
                    action = new PlayerControl.Control[]{DOWN, LEFT, RIGHT}[random.nextInt(3)];
                    break;
                case LEFT:
                    action = new PlayerControl.Control[]{UP, DOWN, LEFT}[random.nextInt(3)];
                    break;
                case RIGHT:
                    action = new PlayerControl.Control[]{UP, DOWN, RIGHT}[random.nextInt(3)];
                    break;
            }
        }

        super.onAction(action);
    }
}
