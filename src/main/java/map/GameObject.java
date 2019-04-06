package map;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class GameObject<T> implements Drawable<T> {
    private Point position;

    @NotNull
    Point getPosition() {
        return position;
    }

    void moveToPosition(@NotNull Point position) {
        this.position = position;
    }
}
