package rgl.map;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.mobs.AggressiveMob;
import rgl.gameobjects.characters.mobs.CowardMob;
import rgl.gameobjects.characters.mobs.Hostile;
import rgl.gameobjects.characters.mobs.PassiveMob;
import rgl.gameobjects.characters.player.Player;
import rgl.gameobjects.items.HoodItem;
import rgl.gameobjects.items.Item;
import rgl.gameobjects.items.RingItem;
import rgl.generators.ItemGenerator;
import rgl.logic.GameContext;
import rgl.map.terrain.cells.Cell;

import java.awt.*;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Representation of games' world map.
 * Contains landscapes, items, npcs and the player.
 */
public class WorldMap {
    private WorldMapLayout layout;
    private GameContext context;

    public WorldMap(@NotNull WorldMapLayout layout, @NotNull GameContext context) {
        this.context = context;
        this.layout = layout;

        generateContent();
    }

    private void generateContent() {
        initializePlayer(context.getPlayer());

        Point dims = getDimensions();
        for (int x = 0; x < dims.x; x++) {
            for (int y = 0; y < dims.y; y++) {
                if (isPassable(x, y)) {
                    generateItem(x, y);
                }

                if (isPassable(x, y)) {
                   generateMob(x, y);
                }
            }
        }
    }

    public void loadMap(@NotNull WorldMapLayout layout) {
        this.layout = layout;

        generateContent();
    }

    public void initializePlayer(@NotNull Player player) {
        layout.initializePlayer(player);
    }

    @NotNull
    public Point getDimensions() {
        return layout.getDimensions();
    }

    @NotNull
    public Point getExit() {
        return layout.getExit();
    }

    @NotNull
    public GameObjectType cellDisplay(int x, int y) {
        return layout.cellDisplay(x, y);
    }

    public boolean isPassable(@NotNull Point position) {
        return layout.isPassable(position);
    }

    public boolean isPassable(int x, int y) {
        return layout.isPassable(x, y);
    }

    public boolean isPickable(@NotNull Point position) {
        return layout.isPickable(position);
    }

    @NotNull
    public Cell<GameObjectType> getCell(@NotNull Point position) {
        return layout.getCell(position);
    }

	/**
	 * Place item on the map by coordiatex `x` and `y`.
	 */
    private void generateItem(int x, int y) {
        Random rand = new Random();

        int n = rand.nextInt(context.getPlayer().statsSize()) + 1;
        if (rand.nextDouble() < 0.005) {
            int min = -5;
            int max = 10;
            Item item = new RingItem(context,
                    ItemGenerator.generateName("Ring of"),
                    ItemGenerator.generatePropreties(n, min, max)
            );
            item.moveToCell(getCell(new Point(x, y)));
        } else if (rand.nextDouble() < 0.005) {
            int min = -3;
            int max = 5;
            Item item = new HoodItem(context,
                    ItemGenerator.generateName("Hood of"),
                    ItemGenerator.generatePropreties(n, min, max)
            );
            item.moveToCell(getCell(new Point(x, y)));
        }
    }

	/**
	 * Place a hostile mov on the map by coordiatex `x` and `y`.
	 */
    private void generateMob(int x, int y) {
        if (Math.random() < 0.015) {
            Hostile hostile = new CowardMob(context);
            hostile.moveToCell(getCell(new Point(x, y)));
        } else if (Math.random() < 0.015) {
            Hostile hostile = new AggressiveMob(context);
            hostile.moveToCell(getCell(new Point(x, y)));
        } else if (Math.random() < 0.015) {
            Hostile hostile = new PassiveMob(context);
            hostile.moveToCell(getCell(new Point(x, y)));
        }
    }

    @NotNull
    public Stream<Cell<GameObjectType>> getCellStream() {
        return layout.getCellStream();
    }

    public WorldMapLayout getLayout() {
        return layout;
    }
}
