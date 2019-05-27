package logic;

import game_objects.Player;
import gui.GUI;
import map.WorldMap;
import map.WorldMapLayout;
import map.terrain.TerrainMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameContext {
    private Player player;
    private WorldMap world;
    private GUI gui;
    private GameStatus status;

    private Set<GameLoop.IterationListener> listeners;
    private List<GameLoop.IterationListener> listenersToRemove = new ArrayList<>();

    public GameContext(@NotNull TerrainMap initialMap, @NotNull Set<GameLoop.IterationListener> listeners,
                       Runnable onDeath) {
        this.listeners = listeners;

        player = new Player(this, 1, onDeath);
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
        listenersToRemove.add(listener);
    }

    public List<GameLoop.IterationListener> getListenersToRemove() {
        return listenersToRemove;
    }

    public GameStatus getGameStatus() {
        return status;
    }
    public void updateGameStatus(String msg) { status.updateStatus(msg); }
    public void appendGameStatus(String msg) { status.appendStatus(msg); }
}
