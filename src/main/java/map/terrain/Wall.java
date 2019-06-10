package map.terrain;

import game_objects.GameObject;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Wall extends Cell<Character> {
    public Wall(Point position) {
        super(position);
    }

    @Override
    public boolean canSetGameObject() {
        return false;
    }

    @Override
    public void clearGameObject() {}

    @Override
    public void setGameObjectToCell(@NotNull GameObject<Character> gameObject) {
        throw new IllegalArgumentException();
    }

    @NotNull
    @Override
    public Character display() {
        return '#';
    }
}
