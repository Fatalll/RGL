package game_objects;

import org.jetbrains.annotations.NotNull;

public interface Drawable<T> {
    @NotNull
    T display();
}
