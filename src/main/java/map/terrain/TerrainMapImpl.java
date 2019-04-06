package map.terrain;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TerrainMapImpl implements TerrainMap {
    private TerrainCellType [][] terrain;
    private Point enterPoint;
    private Point exitPoint;
    private int height;
    private int width;

    public TerrainMapImpl(TerrainCellType [][] terrain, Point enterPoint, Point exitPoint, int width, int height) {
        this.terrain = terrain;
        this.enterPoint = enterPoint;
        this.exitPoint = exitPoint;
        this.height = height;
        this.width = width;
    }

    @Override
    public @NotNull Point getDimensions() {
        return new Point(width, height);
    }

    @Override
    public @NotNull Point getEnterPoint() {
        return enterPoint;
    }

    @Override
    public @NotNull Point getExitPoint() {
        return exitPoint;
    }

    @Override
    public @NotNull TerrainCellType getCellType(Point position) {
        return terrain[position.y][position.x];
    }
}
