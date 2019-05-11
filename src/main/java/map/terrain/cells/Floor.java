package map.terrain.cells;

import game_objects.GameObject;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Floor extends Cell<Character> {
    private GameObject<Character> gameObject;

    public Floor(Point position) {
        super(position);
    }

    @Override
    public GameObject<Character> getGameObject() {
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
    public void setGameObjectToCell(@NotNull GameObject<Character> gameObject) {
        if (this.gameObject == null) {
            this.gameObject = gameObject;
        } else {
            throw new IllegalStateException();
        }
    }

    @NotNull
    @Override
    public Character display() {
        return (gameObject == null) ? ' ' : gameObject.display();
    }
}
