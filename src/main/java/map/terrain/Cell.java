package map.terrain;

import game_objects.Drawable;
import game_objects.GameObject;
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

    public abstract boolean canSetGameObject();

    public abstract void setGameObjectToCell(@NotNull GameObject<T> gameObject);

    public abstract void clearGameObject();
}
