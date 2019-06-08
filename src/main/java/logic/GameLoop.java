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

       context = new GameContext(new TerrainMapImpl(mapPath), listeners, this::death);
       gui = new ConsoleGUI(context);
       context.setGui(gui);
       gui.addActionListener(context.getPlayer());
       gui.addActionListener(action -> exit = action == PlayerControl.Control.EXIT);
       gui.addActionListener(action -> {
           if (action == PlayerControl.Control.DROP) {
               context.getPlayer().dropItem(0);
           }
       });
       gui.addActionListener(action -> {
           if (action == PlayerControl.Control.SAVE) {
               try {
                   FileOutputStream output = new FileOutputStream("gamestate");
                   context.getAsSerializableContext().serializeToProto().writeTo(output);
                   output.close();
               } catch (Exception ignored) {
                   ignored.printStackTrace();
               }

           }
       });
        gui.addActionListener(action -> {
            if (action == PlayerControl.Control.LOAD) {
                try {
                    FileInputStream input = new FileInputStream("gamestate");
                    context.getAsSerializableContext().deserializeFromProto(GameObjectsProto.GameContext.parseFrom(input));
                    input.close();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }

            }
        });
    }

    public void run() throws IOException {
        while (!exit) {
            if (gui.iteration()) {
                for (IterationListener listener : context.getListenersToRemove()) {
                    listeners.remove(listener);
                }

                context.getListenersToRemove().clear();

                for (IterationListener listener : listeners) {
                    listener.iterate(context);
                }

                if (context.getPlayer().getPosition().equals(context.getWorld().getExit())) {
                    listeners.clear();
                    context.getWorld().loadMap(new WorldMapLayout(new TerrainMapImpl(mapPath), context));
                    context.updateGameStatus("New region!");
                }
            }
        }
    }

    public void death() {
        exit = true;
        context.updateGameStatus("You died!");
    }

    public interface IterationListener {
        void iterate(@NotNull GameContext context);
    }
}