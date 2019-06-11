package logic;

import gui.ConsoleGUI;
import gui.GUI;
import gui.PlayerControl;
import map.WorldMapLayout;
import map.terrain.TerrainMapImpl;
import org.jetbrains.annotations.NotNull;
import protobuf.GameObjectsProto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class GameLoop {
    private boolean exit = false;
    private Set<IterationListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());
    private String mapPath;
    private GameContext context;
    private GUI gui;

    public GameLoop(String mapPath) throws IOException {
        this.mapPath = mapPath;

        context = new GameContext(mapPath == null ? new TerrainMapImpl(100, 29)
                : new TerrainMapImpl(mapPath, 100, 29), listeners);
        gui = new ConsoleGUI(context);
        context.setGui(gui);
        gui.addActionListener(context.getPlayer());
        gui.addActionListener(action -> {
            if (!exit) exit = action == PlayerControl.Control.EXIT;
        });
        gui.addActionListener(action -> {
            if (action == PlayerControl.Control.DROP) {
                context.getPlayer().dropItem(0);
            }
        });
        gui.addActionListener(action -> {
            if (action == PlayerControl.Control.SAVE) {
                try (FileOutputStream output = new FileOutputStream("gamestate")) {
                    context.getAsSerializableContext().serializeToProto().writeTo(output);
                    context.updateGameStatus("Game saved");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        gui.addActionListener(action -> {
            if (action == PlayerControl.Control.LOAD) {
                try (FileInputStream input = new FileInputStream("gamestate")) {
                    context.getAsSerializableContext().deserializeFromProto(GameObjectsProto.GameContext.parseFrom(input));
                    context.updateGameStatus("Game loaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void run() throws IOException {
        while (!exit) {
            if (gui.iteration()) {
                for (IterationListener listener : new HashSet<>(listeners)) {
                    listener.iterate(context);
                }

                // if the end of the current map, than load next map
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

    public void death() {
        exit = true;
        context.updateGameStatus("You died!");
    }

    public interface IterationListener {
        void iterate(@NotNull GameContext context);
    }
}