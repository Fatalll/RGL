package map;

import game_objects.Player;
import map.terrain.Cell;
import map.terrain.Floor;
import map.terrain.TerrainMap;
import map.terrain.Wall;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class WorldMapLayout {
    private Cell<Character>[][] world;
    private Point dimensions;

    public WorldMapLayout(@NotNull TerrainMap terrain, Player player) {
        dimensions = terrain.getDimensions();

        int width = dimensions.y;
        int height = dimensions.x;
        world = new Cell[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Point point = new Point(i, j);
                world[j][i] = terrain.getCellType(point) == TerrainMap.TerrainCellType.WALL ? new Wall(point) : new Floor(point);
            }
        }

        player.moveToCell(world[terrain.getEnterPoint().x][terrain.getEnterPoint().y]);
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
        if (position.x >= dimensions.x || position.x < 0 || position.y < 0 || position.y >= dimensions.y) {
            return false;
        }

        return world[position.y][position.x].canSetGameObject();
    }

    @NotNull
    public Cell<Character> getCell(@NotNull Point position) {
        return world[position.y][position.x];
    }
}
