package map;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class WorldMapLayout {
    private Cell<Character>[][] world;
    private Point dimensions;

    public WorldMapLayout(@NotNull TerrainMap terrain) {
        dimensions = terrain.getDimensions();

        int width = dimensions.x;
        int height = dimensions.y;
        world = new Cell[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Point point = new Point(i, j);
                world[i][j] = terrain.getCellType(point) == TerrainMap.TerrainCellType.WALL ? new Wall(point) : new Floor(point);
            }
        }
    }

    @NotNull
    public Point getDimensions() {
        return dimensions;
    }

    @NotNull
    public Character displayCell(@NotNull Point position) {
        return world[position.x][position.y].display();
    }

    public boolean isPassable(@NotNull Point position) {
        return world[position.x][position.y].canSetGameObject();
    }
}
