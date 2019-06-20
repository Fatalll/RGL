package ru.ifmo.rgl.map.terrain;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface TerrainMap {
    @NotNull
    Point getDimensions();

    @NotNull
    Point getEnterPoint();

    @NotNull
    Point getExitPoint();

    @NotNull
    TerrainCellType getCellType(Point position);

    enum TerrainCellType {
        WALL,
        VOID,
    }
}
