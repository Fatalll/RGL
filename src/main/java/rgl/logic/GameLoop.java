package rgl.logic;

import org.jetbrains.annotations.NotNull;
import rgl.commands.LoadCommand;
import rgl.commands.SaveCommand;
import rgl.gui.ConsoleGUI;
import rgl.gui.GUI;
import rgl.gui.PlayerControl;
import rgl.map.WorldMapLayout;
import rgl.map.terrain.TerrainMapImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * game loop
 */
public class GameLoop {
    private boolean exit = false;
    private Set<IterationListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());
    private String mapPath;
    private GameContext context;
    private GUI gui;

    public GameLoop(String mapPath, boolean load) throws IOException {
        this.mapPath = mapPath;

        context = new GameContext(mapPath == null ? new TerrainMapImpl(100, 29)
                : new TerrainMapImpl(mapPath, 100, 29), listeners);
        gui = new ConsoleGUI(context, new SaveCommand(context), () -> exit = true);
        context.setGui(gui);
        gui.addActionListener(context.getPlayer());
        gui.addActionListener(action -> {
            if (action == PlayerControl.Control.DROP) {
                context.getPlayer().dropItem(0);
            }
        });

        if (load) {
            new LoadCommand(context).execute();
        }
    }

    public void run() throws IOException {
        while (!exit) {
            if (gui.iteration()) {
                for (IterationListener listener : new HashSet<>(listeners)) {
                    listener.iterate(context);
                }

                // if the end of the current rgl.map, than load next rgl.map
                if (context.getPlayer().getPosition().equals(context.getWorld().getExit())) {
                    listeners.clear();
                    context.getWorld().loadMap(new WorldMapLayout(mapPath == null ? new TerrainMapImpl(100, 29)
                            : new TerrainMapImpl(mapPath, 100, 29), context));
                    context.updateGameStatus("New region!");
                }

                if (context.getPlayer().getHealth() <= 0) {
                    death();
                }
            }
        }

        gui.close();
    }

    private void death() {
        exit = true;
        context.updateGameStatus("You died!");
        // Remove save if exist
        try {
            Files.deleteIfExists(Paths.get("gamestate"));
        } catch (IOException ignored) {}
    }

    public interface IterationListener {
        void iterate(@NotNull GameContext context);
    }
}