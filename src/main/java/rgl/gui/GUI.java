package rgl.gui;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * interface for any GUI
 */
public abstract class GUI {
    // listeners for GUI events
    private Set<ActionListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * called when game loop iteration needed
     *
     * @return true if iteration executed
     * @throws IOException
     */
    public abstract boolean iteration() throws IOException;


    /**
     * request for redraw a gui
     *
     * @throws IOException
     */
    public abstract void update() throws IOException;

    /**
     * close GUI
     *
     * @throws IOException
     */
    public abstract void close() throws IOException;

    void onPlayerAction(@NotNull PlayerControl.Control action) {
        for (ActionListener listener : new HashSet<>(listeners)) {
            listener.onAction(action);
        }
    }

    /**
     * add listener to GUI actions
     *
     * @param listener listener
     */
    public void addActionListener(@NotNull ActionListener listener) {
        listeners.add(listener);
    }

    /**
     * remove listener to GUI action
     *
     * @param listener listener
     * @return true if present
     */
    public boolean removeActionListener(@NotNull ActionListener listener) {
        return listeners.remove(listener);
    }

    /**
     * interface for GUI action listener
     */
    public interface ActionListener {
        void onAction(@NotNull PlayerControl.Control action);
    }
}
