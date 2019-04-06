package map;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface TerrainMap<T> {
    enum TerrainCellType {
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
    T displayCell(Point position);

    @NotNull
    TerrainCellType getCellType(Point position);
}
