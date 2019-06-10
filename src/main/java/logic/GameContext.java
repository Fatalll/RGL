package logic;

import game_objects.Player;
import gui.GUI;
import map.WorldMap;
import map.WorldMapLayout;
import map.terrain.TerrainMap;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GameContext {
    private Player player;
    private WorldMap world;
    private GUI gui;
    private GameStatus status;

    // listeners called on each game tick
    private Set<GameLoop.IterationListener> listeners;

    public GameContext(@NotNull TerrainMap initialMap, @NotNull Set<GameLoop.IterationListener> listeners) {
        this.listeners = listeners;

        player = new ConfusionPlayer(this, 1);
        world = new WorldMap(new WorldMapLayout(initialMap, this), this);
        status = new GameStatus();
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public WorldMap getWorld() {
        return world;
    }

    @NotNull
    public GUI getGui() {
        return gui;
    }

    public void setGui(@NotNull GUI gui) {
        this.gui = gui;
    }

    public void addIterationListener(@NotNull GameLoop.IterationListener listener) {
        listeners.add(listener);
    }

    public void removeIterationListener(@NotNull GameLoop.IterationListener listener) {
        listeners.remove(listener);
    }

    public GameStatus getGameStatus() {
        return status;
    }
    public void updateGameStatus(String msg) { status.updateStatus(msg); }
    public void appendGameStatus(String msg) { status.appendStatus(msg); }
}
