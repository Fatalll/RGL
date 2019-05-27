package map.terrain.cells;

import game_objects.GameObject;
import game_objects.GameObjectType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public class Floor extends Cell<GameObjectType> {
    private GameObject<GameObjectType> gameObject;
    private GameObjectType floor = chooseFloor();

    public Floor(Point position) {
        super(position);
    }

    @Override
    public GameObject<GameObjectType> getGameObject() {
        return gameObject;
    }

    @Override
    public boolean canSetGameObject() {
        return gameObject == null;
    }

    @Override
    public void clearGameObject() {
        gameObject = null;
    }

    @Override
    public void setGameObjectToCell(@NotNull GameObject<GameObjectType> gameObject) {
        if (this.gameObject == null) {
            this.gameObject = gameObject;
        } else {
            throw new IllegalStateException();
        }
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return (gameObject == null) ?
                floor :
                gameObject.display();
    }

    private GameObjectType chooseFloor() {
        double a = Math.random();
        if (a > 0.4) {
            return GameObjectType.FLOOR1;
        } else if (a > 0.1) {
            return GameObjectType.FLOOR2;
        }
        return GameObjectType.FLOOR3;
    }
}
