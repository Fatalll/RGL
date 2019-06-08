package map.terrain.cells;

import game_objects.Drawable;
import game_objects.GameObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import protobuf.GameObjectsProto;
import protobuf.Serializable;

import java.awt.*;

public abstract class Cell<T> implements Drawable<T> {
    protected Point position;

    public Cell(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    @Nullable
    public abstract GameObject<T> getGameObject();

    public abstract boolean canSetGameObject();

    public abstract void setGameObjectToCell(@NotNull GameObject<T> gameObject);

    public abstract void clearGameObject();
}
