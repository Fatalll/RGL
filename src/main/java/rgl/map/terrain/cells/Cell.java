package rgl.map.terrain.cells;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rgl.gameobjects.Drawable;
import rgl.gameobjects.GameObject;

import java.awt.*;

/**
 * interface for any cell which can be present on the map
 *
 * @param <T> display type
 */
public interface Cell<T> extends Drawable<T> {
    /**
     * get position of the cell
     * @return position
     */
    Point getPosition();

    /**
     * get game object in this cell
     * @return game object or null
     */
    @Nullable
    GameObject<T> getGameObject();

    /**
     * check if cell can contain game object
     *
     * @return true if can
     */
    boolean canSetGameObject();

    /**
     * set game object to cell
     *
     * @param gameObject game object
     */
    void setGameObjectToCell(@NotNull GameObject<T> gameObject);

    /**
     * remove game object from cell
     */
    void clearGameObject();
}
