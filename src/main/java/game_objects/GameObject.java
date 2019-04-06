package game_objects;

import map.terrain.Cell;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class GameObject<T> implements Drawable<T> {
    protected Cell<T> cell;

    @NotNull
    public Point getPosition() {
        return cell.getPosition();
    }

    public void moveToCell(@NotNull Cell<T> cell) {
        if (this.cell != null) {
            this.cell.clearGameObject();
        }

        cell.setGameObjectToCell(this);
        this.cell = cell;
    }
}
