package logic;

import gui.GUI;
import gui.PlayerControl;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static gui.PlayerControl.Control.*;

public class ConfusionStatus implements GUI.ActionListener {
    protected GameContext context;
    private int timeout;
    private Random random = new Random();

    public ConfusionStatus(int timeout, @NotNull GameContext context) {
        this.timeout = timeout;
        this.context = context;

        if (context.getGui().removeActionListener(context.getPlayer())) {
            context.getGui().addActionListener(this);
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
        } else {
            context.getGui().removeActionListener(this);
            context.getGui().addActionListener(context.getPlayer());
        }

        context.getPlayer().onAction(action);
    }
}
