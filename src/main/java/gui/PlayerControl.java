package gui;

import java.awt.*;

public class PlayerControl {
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
            case SKIP:
            case EXIT:
                return position;
        }

        throw new IllegalArgumentException();
    }

    public enum Control {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SKIP,
        EXIT,
    }
}
