package map;

import game_objects.GameObjectType;
import game_objects.Player;
import game_objects.items.Item;
import logic.GameContext;
import map.terrain.TerrainMap;
import map.terrain.cells.Cell;
import map.terrain.cells.Exit;
import map.terrain.cells.Floor;
import map.terrain.cells.Wall;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class WorldMapLayout {
    private Cell<GameObjectType>[][] world;
    private Point dimensions, entry, exit;
    private GameContext context;

    public WorldMapLayout(@NotNull TerrainMap terrain, @NotNull GameContext context) {
        this.context = context;
        dimensions = terrain.getDimensions();

        int width = dimensions.y;
        int height = dimensions.x;
        world = new Cell[width][height];

        // Construct the world map based on terrain map.
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
        player.moveToCell(world[entry.y][entry.x]);
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
    public GameObjectType cellDisplay(@NotNull Point position) {
        validatePoint(position);
        return world[position.x][position.y].display();
    }

    @NotNull
    public GameObjectType cellDisplay(int x, int y) {
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

    public boolean isPickable(@NotNull Point position) {
       Cell<?> obj = getCell(position);
       return obj.getGameObject() instanceof Item;
    }

    @NotNull
    public Cell<GameObjectType> getCell(@NotNull Point position) {
        validatePoint(position);
        return world[position.y][position.x];
    }

    private void validatePoint(@NotNull Point point) {
        if (!isValidPoint(point)) {
            throw new IllegalArgumentException("Wrong new position.");
        }
    }

    private boolean isValidPoint(@NotNull Point point) {
        return isValidPoint(point.x, point.y);
    }

    private boolean isValidPoint(int x, int y) {
        return x < dimensions.x && x >= 0 && y >= 0 && y < dimensions.y;
    }

    @NotNull
    public Stream<Cell<GameObjectType>> getCellStream() {
        return Arrays.stream(world).flatMap(Arrays::stream);
    }

    public void setWorld(Cell<GameObjectType>[][] world) {
        this.world = world;
        dimensions = new Point(world[0].length, world.length);
    }

    public void setExit(Point exit) {
        this.exit = exit;
    }

    public void setEntry(Point entry) {
        this.entry = entry;
    }

    public Point getEntry() {
        return entry;
    }
}
