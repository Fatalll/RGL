package ru.ifmo.rgl.map;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObjectType;
import ru.ifmo.rgl.gameobjects.characters.player.Player;
import ru.ifmo.rgl.gameobjects.items.Item;
import ru.ifmo.rgl.logic.GameContext;
import ru.ifmo.rgl.map.terrain.TerrainMap;
import ru.ifmo.rgl.map.terrain.cells.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class WorldMapLayout {
    private Cell<GameObjectType>[][] world;
    private Point dimensions, entry, exit;
    private GameContext context;

    public WorldMapLayout(@NotNull TerrainMap terrain, @NotNull GameContext context) {
        this.context = context;
        dimensions = terrain.getDimensions();

        int width = dimensions.x;
        int height = dimensions.y;
        world = new Cell[height][width];

        // Construct the world rgl.map based on terrain rgl.map.
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Point point = new Point(j, i);
                world[i][j] = terrain.getCellType(point) == TerrainMap.TerrainCellType.WALL ? new Wall(point) : new Floor(point);
            }
        }

        entry = terrain.getEnterPoint();
        exit = terrain.getExitPoint();
        world[exit.y][exit.x] = new Exit(exit);
        world[entry.y][entry.x] = new Entry(entry);
    }

    public void initializePlayer(@NotNull Player player) {
        player.moveToCell(world[entry.y][entry.x]);
    }

    public void initializePlayerRandomly(@NotNull Player player) {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(dimensions.x - 2) + 1;
            y = random.nextInt(dimensions.y - 2) + 1;
        } while (!isPassable(x, y));

        player.moveToCell(world[y][x]);
    }

    @NotNull
    public Point getDimensions() {
        return dimensions;
    }

    @NotNull
    public Point getExit() {
        return exit;
    }

    public void setExit(Point exit) {
        this.exit = exit;
    }

    @NotNull
    public GameObjectType cellDisplay(@NotNull Point position) {
        validatePoint(position);
        return world[position.y][position.x].display();
    }

    @NotNull
    public GameObjectType cellDisplay(int x, int y) {
        return world[y][x].display();
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

    public Point getEntry() {
        return entry;
    }

    public void setEntry(Point entry) {
        this.entry = entry;
    }
}
