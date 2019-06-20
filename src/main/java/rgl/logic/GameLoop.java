package rgl.logic;

import org.jetbrains.annotations.NotNull;
import rgl.commands.LoadCommand;
import rgl.commands.SaveCommand;
import rgl.gameobjects.characters.player.Player;
import rgl.gui.ConsoleGUI;
import rgl.gui.GUI;
import rgl.gui.PlayerControl;
import rgl.map.WorldMapLayout;
import rgl.map.terrain.TerrainMapImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * GameLoop for single player mod
 */
public class GameLoop {
    private boolean exit = false;
    private Set<IterationListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());
    private String mapPath;
    private GameContext context;
    private GUI gui;
    private Player player;

    public GameLoop(String mapPath, boolean load) throws IOException {
        this.mapPath = mapPath;

        context = new GameContext(mapPath == null ? new TerrainMapImpl(100, 29)
                : new TerrainMapImpl(mapPath, 100, 29), listeners);

        UUID playerID;
        if (load) {
            new LoadCommand(context).execute();
            // if load we sure there is a single player
            playerID = context.getPlayers().keySet().iterator().next();
            player = context.getPlayers().get(playerID);
        } else {
            player = new Player(context, 1);
            playerID = context.addPlayer(player);
        }

        gui = new ConsoleGUI(context, playerID, new SaveCommand(context), () -> exit = true);
        gui.addActionListener(player);
        gui.addActionListener(action -> {
            if (action == PlayerControl.Control.DROP) {
                player.dropItem(0);
            }
        });
    }

    public void run() throws IOException {
        while (!exit) {
            gui.update();
            if (gui.iteration()) {
                for (IterationListener listener : new HashSet<>(listeners)) {
                    listener.iterate(context);
                }

                // if the end of the current rgl.map, than load next rgl.map
                if (player.getPosition().equals(context.getWorld().getExit())) {
                    listeners.clear();
                    context.getWorld().loadMap(new WorldMapLayout(mapPath == null ? new TerrainMapImpl(100, 29)
                            : new TerrainMapImpl(mapPath, 100, 29), context));
                    context.getWorld().initializePlayer(player);
                    context.updateGameStatus("New region!");
                }

                if (player.getHealth() <= 0) {
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
        } catch (IOException ignored) {
        }
    }

    public interface IterationListener {
        void iterate(@NotNull GameContext context);
    }
}