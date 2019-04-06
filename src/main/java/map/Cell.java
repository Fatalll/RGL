package map;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class Cell<T> implements Drawable<T> {
    private Point position;

    public Cell(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    abstract boolean canSetGameObject();

    abstract void setGameObjectToCell(@NotNull GameObject<T> gameObject);

    abstract void clearGameObject();
}
