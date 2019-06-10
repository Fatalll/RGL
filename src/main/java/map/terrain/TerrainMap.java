package map.terrain;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface TerrainMap {
    public enum TerrainCellType {
        WALL,
        VOID,
    }

    @NotNull
    Point getDimensions();

    @NotNull
    Point getEnterPoint();

    @NotNull
    Point getExitPoint();

    @NotNull
    TerrainCellType getCellType(Point position);
}
