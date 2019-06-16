package rgl.map.terrain.cells;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;

import java.awt.*;

/**
 * exit cell from current map
 */
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
