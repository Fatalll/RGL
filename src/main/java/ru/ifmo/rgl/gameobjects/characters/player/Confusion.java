package ru.ifmo.rgl.gameobjects.characters.player;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gui.GUI;
import ru.ifmo.rgl.gui.PlayerControl;

import java.util.Random;

import static ru.ifmo.rgl.gui.PlayerControl.Control.*;


/**
 * confusion status for player using decorator pattern
 */
public class Confusion implements GUI.ActionListener {
    public static final float CONFUSION_CHANCE = 0.15f;
    private Random random = new Random();
    private Player player;

    public Confusion(@NotNull Player player) {
        this.player = player;
    }

    // decorator pattern for player confusion, override his action
    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
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

        player.onAction(action);
    }
}
