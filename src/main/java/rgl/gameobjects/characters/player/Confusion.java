package rgl.gameobjects.characters.player;

import org.jetbrains.annotations.NotNull;
import rgl.gui.GUI;
import rgl.gui.PlayerControl;

import java.util.Random;

import static rgl.gui.PlayerControl.Control.*;


/**
 * confusion status for player using decorator pattern
 */
public class Confusion implements GUI.ActionListener {
    public static final float CONFUSION_CHANCE = 0.15f;
    private Random random = new Random();
    private Player player;
    private GUI gui;
    private int timeout;

    public Confusion(@NotNull Player player, @NotNull GUI gui, int timeout) {
        this.player = player;
        this.gui = gui;
        this.timeout = timeout;
    }

    // decorator pattern for player confusion, override his action
    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        if (timeout > 0) { // if confusion, than change direction
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
            // remove confusion
            gui.removeActionListener(this);
            gui.addActionListener(player);
        }

        player.onAction(action);
    }
}
