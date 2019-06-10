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

        int width = dimensions.x;
        int height = dimensions.y;
        world = new Cell[height][width];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                Point point = new Point(col, row);
                world[row][col] = terrain.getCellType(point) == TerrainMap.TerrainCellType.WALL ?
                                  new Wall(point) : new Floor(point);
            }
        }

        System.out.println(terrain.getEnterPoint() + " " + terrain.getExitPoint());
        player.moveToCell(world[terrain.getEnterPoint().y][terrain.getEnterPoint().x]);
    }

    @NotNull
    public Point getDimensions() {
        return dimensions;
    }

    @NotNull
    public Character displayCell(@NotNull Point position) {
        validatePoint(position);
        return world[position.x][position.y].display();
    }

    public boolean isPassable(@NotNull Point position) {
        if (!isValidPoint(position)) {
            return false;
        }

        return world[position.y][position.x].canSetGameObject();
    }

    @NotNull
    public Cell<Character> getCell(@NotNull Point position) {
		validatePoint(position);
        return world[position.y][position.x];
    }

	private void validatePoint(@NotNull Point point) {
        if (!isValidPoint(point)) {
			throw new IllegalArgumentException("Wrong new position.");
        }
	}

    private boolean isValidPoint(@NotNull Point point) {
        return point.x < dimensions.x || point.x >= 0 || point.y >= 0 || point.y < dimensions.y;
    }
}
