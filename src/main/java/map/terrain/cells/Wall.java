package map.terrain.cells;

import game_objects.GameObject;
import game_objects.GameObjectType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Wall extends Cell<GameObjectType> {
    public Wall(Point position) {
        super(position);
    }

    @Override
    public GameObject<GameObjectType> getGameObject() {
        return null;
    }

    @Override
    public boolean canSetGameObject() {
        return false;
    }

    @Override
    public void clearGameObject() {

    }

    @Override
    public void setGameObjectToCell(@NotNull GameObject<GameObjectType> gameObject) {
        throw new IllegalArgumentException();
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.WALL;
    }
}
