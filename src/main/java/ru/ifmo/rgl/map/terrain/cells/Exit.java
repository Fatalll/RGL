package ru.ifmo.rgl.map.terrain.cells;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObjectType;

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
        return GameObjectType.EXIT;
    }
}
