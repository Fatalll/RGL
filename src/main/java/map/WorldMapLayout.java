package map;

import game_objects.Player;
import logic.GameContext;
import map.terrain.TerrainMap;
import map.terrain.cells.Cell;
import map.terrain.cells.Exit;
import map.terrain.cells.Floor;
import map.terrain.cells.Wall;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class WorldMapLayout {
    private Cell<Character>[][] world;
    private Point dimensions, entry, exit;
    private GameContext context;

    public WorldMapLayout(@NotNull TerrainMap terrain, @NotNull GameContext context) {
        this.context = context;
        dimensions = terrain.getDimensions();

        int width = dimensions.y;
        int height = dimensions.x;
        world = new Cell[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Point point = new Point(i, j);
                world[j][i] = terrain.getCellType(point) == TerrainMap.TerrainCellType.WALL ? new Wall(point) : new Floor(point);
            }
        }

        entry = terrain.getEnterPoint();
        exit = terrain.getExitPoint();
        world[exit.y][exit.x] = new Exit(exit);
    }

    public void initializePlayer(@NotNull Player player) {
        player.moveToCell(world[entry.x][entry.y]);
    }

    @NotNull
    public Point getDimensions() {
        return dimensions;
    }

    @NotNull
    public Point getExit() {
        return exit;
    }

    @NotNull
    public Character cellDisplay(@NotNull Point position) {
        return world[position.x][position.y].display();
    }

    @NotNull
    public Character cellDisplay(int x, int y) {
        return world[x][y].display();
    }

    public boolean isPassable(@NotNull Point position) {
        return isPassable(position.x, position.y);
    }

    public boolean isPassable(int x, int y) {
        if (x >= dimensions.x || x < 0 || y < 0 || y >= dimensions.y) {
            return false;
        }

        return world[y][x].canSetGameObject();
    }

    @NotNull
    public Cell<Character> getCell(@NotNull Point position) {
        return world[position.y][position.x];
    }
}
