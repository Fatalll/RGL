package rgl.gameobjects;

import org.jetbrains.annotations.NotNull;
import rgl.logic.GameContext;
import rgl.map.terrain.cells.Cell;

import java.awt.*;

/**
 * default class for any game object
 *
 * @param <T> display type
 */
public abstract class GameObject<T> implements Drawable<T> {
    private Cell<T> cell;
    private GameContext context;

    public GameObject(@NotNull GameContext context) {
        this.context = context;
    }

    public GameContext getContext() {
        return context;
    }

    @NotNull
    public Point getPosition() {
        return cell.getPosition();
    }

    public void clearGameObject() {
        cell.clearGameObject();
    }

    /**
     * is bound to position
     *
     * @return true if bound
     */
    public boolean isPresent() {
        return cell != null;
    }

    public void moveToCell(@NotNull Cell<T> cell) {
        if (isPresent()) {
            this.cell.clearGameObject();
        }

        cell.setGameObjectToCell(this);
        this.cell = cell;
    }
}
