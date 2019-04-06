package map;

import org.jetbrains.annotations.NotNull;

public abstract class GameObject<T> implements Drawable<T> {
    private Cell<T> cell;

    public void moveToCell(@NotNull Cell<T> cell) {
        this.cell.clearGameObject();
        cell.setGameObjectToCell(this);
        this.cell = cell;
    }
}
