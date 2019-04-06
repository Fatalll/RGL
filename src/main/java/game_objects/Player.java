package game_objects;

import org.jetbrains.annotations.NotNull;

public class Player extends GameObject<Character> {
    @NotNull
    @Override
    public Character display() {
        return '$';
    }
}
