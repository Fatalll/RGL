package logic;

import gui.ConsoleGUI;
import gui.GUI;
import gui.PlayerControl;
import map.WorldMapLayout;
import map.terrain.TerrainMapImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class GameLoop {
    private boolean exit = false;
    private Set<IterationListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());

    public static void main(String[] args) throws IOException {
        GameLoop loop = new GameLoop();

        GameContext context = new GameContext(new TerrainMapImpl("/test.map"), loop.listeners, loop::death);
        GUI gui = new ConsoleGUI(context);

        context.setGui(gui);

        gui.addActionListener(context.getPlayer());
        gui.addActionListener(action -> loop.exit = action == PlayerControl.Control.EXIT);

        while (!loop.exit) {
            if (gui.iteration()) {
                for (IterationListener listener : context.getListenersToRemove()) {
                    loop.listeners.remove(listener);
                }

                context.getListenersToRemove().clear();

                for (IterationListener listener : loop.listeners) {
                    listener.iterate(context);
                }

                if (context.getPlayer().getPosition().equals(context.getWorld().getExit())) {
                    loop.listeners.clear();
                    // next map
                    context.getWorld().loadMap(new WorldMapLayout(new TerrainMapImpl("/test.map"), context));
                    System.out.println("Reload Map!");
                }
            }
        }
    }

    public void death() {
        exit = true;
        System.out.println("You died!");
    }

    public interface IterationListener {
        void iterate(@NotNull GameContext context);
    }
}