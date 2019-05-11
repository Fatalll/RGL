package map;

import game_objects.Player;
import game_objects.mobs.AggressiveMob;
import game_objects.mobs.CowardMob;
import game_objects.mobs.Hostile;
import game_objects.mobs.PassiveMob;
import logic.GameContext;
import map.terrain.cells.Cell;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class WorldMap {
    private WorldMapLayout layout;
    private GameContext context;

    public WorldMap(@NotNull WorldMapLayout layout, @NotNull GameContext context) {
        this.context = context;
        this.layout = layout;

        generateMobs();
    }

    private void generateMobs() {
        initializePlayer(context.getPlayer());

        Point dims = getDimensions();
        for (int x = 0; x < dims.x; x++) {
            for (int y = 0; y < dims.y; y++) {
                if (isPassable(x, y)) {
                    if (Math.random() < 0.025) {
                        Hostile hostile = new CowardMob(context);
                        hostile.moveToCell(getCell(new Point(x, y)));
                    } else if (Math.random() < 0.025) {
                        Hostile hostile = new AggressiveMob(context);
                        hostile.moveToCell(getCell(new Point(x, y)));
                    } else if (Math.random() < 0.025) {
                        Hostile hostile = new PassiveMob(context);
                        hostile.moveToCell(getCell(new Point(x, y)));
                    }
                }
            }
        }
    }

    public void loadMap(@NotNull WorldMapLayout layout) {
        this.layout = layout;

        generateMobs();
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
    public Character cellDisplay(int x, int y) {
        return layout.cellDisplay(x, y);
    }

    public boolean isPassable(@NotNull Point position) {
        return layout.isPassable(position);
    }

    public boolean isPassable(int x, int y) {
        return layout.isPassable(x, y);
    }

    @NotNull
    public Cell<Character> getCell(@NotNull Point position) {
        return layout.getCell(position);
    }
}
