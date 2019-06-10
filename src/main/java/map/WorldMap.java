package map;

import game_objects.Player;
import org.jetbrains.annotations.NotNull;
import utils.PlayerControl;

import java.awt.*;

public class WorldMap<T> {

    private WorldMapLayout layout;
    private Player player;

    public WorldMap(@NotNull WorldMapLayout layout, @NotNull Player player) {
        this.layout = layout;
        this.player = player;
    }

    @NotNull
    public WorldMapLayout getMapLayout() {
        return layout;
    }

    public void changeMapLayout(@NotNull WorldMapLayout layout) {
        this.layout = layout;
    }

    public void step(@NotNull PlayerControl.Control action) {
        Point newPosition = PlayerControl.calculateNextPosition(player.getPosition(), action);
        if (layout.isPassable(newPosition)) {
            player.moveToCell(layout.getCell(newPosition));
        }
    }
}
