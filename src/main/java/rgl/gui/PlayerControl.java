package rgl.gui;

import org.jetbrains.annotations.Nullable;
import rgl.proto.PlayerAction;

import java.awt.*;

/**
 * util class for player control
 */
public class PlayerControl {
    /**
     * calculate next position based on player choice
     *
     * @param position current position
     * @param action   GUI action
     * @return next position
     */
    public static Point calculateNextPosition(Point position, Control action) {
        switch (action) {
            case UP:
                return new Point(position.x, position.y - 1);
            case DOWN:
                return new Point(position.x, position.y + 1);
            case LEFT:
                return new Point(position.x - 1, position.y);
            case RIGHT:
                return new Point(position.x + 1, position.y);
            default:
                return position;
        }
    }

    public static Control castFromPlayerAction(@Nullable PlayerAction action) {
        if (action != null) {
            switch (action) {
                case UP:
                    return Control.UP;
                case DOWN:
                    return Control.DOWN;
                case LEFT:
                    return Control.LEFT;
                case RIGHT:
                    return Control.RIGHT;
                case SKIP:
                    return Control.SKIP;
                case DROP:
                    return Control.DROP;
            }
        }

        return Control.SKIP;
    }

    /**
     * possible player actions
     */
    public enum Control {
        UP,    // player press up 'w'
        DOWN,  // player press down 's'
        LEFT,  // player press left 'a'
        RIGHT, // player press right 'd'
        SKIP,  // other button is pressed (skip)
        DROP   // player press drop item 'e'
    }
}
