package map;

import java.awt.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class WorldMap<T> {
    protected TerrainMap<T> terrain;

    enum Control {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SKIP,
    }

    @NotNull
    public Point getDimensions() {
        return terrain.getDimensions();
    }

    @NotNull
    public T displayCell(@NotNull Point position) {
        GameObject<T> object = getGameObject(position);
        if (object == null) {
            return terrain.displayCell(position);
        } else {
            return object.display();
        }
    }

    public abstract boolean isPassable(@NotNull Point position);

    public abstract void step(@NotNull Control action);

    @Nullable
    public abstract GameObject<T> getGameObject(@NotNull Point position);

    public abstract void moveGameObjcetToPosition(@NotNull Point position, @NotNull GameObject<T> gameObject);
}
