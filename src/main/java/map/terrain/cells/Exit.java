package map.terrain.cells;

import game_objects.GameObjectType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Exit extends Floor {
    public Exit(Point position) {
        super(position);
    }

    @Override
    public @NotNull GameObjectType display() {
        GameObjectType label = super.display();
        return GameObjectType.EXIT;
    }
}
