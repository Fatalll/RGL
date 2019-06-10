package gui;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public abstract class GUI {
    protected GameContext context;
    private Set<ActionListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());

    public GUI(@NotNull GameContext context) {
        this.context = context;
    }

    public abstract boolean iteration() throws IOException;

    public abstract void reload();

    public abstract void close() throws IOException;

    protected void onPlayerAction(@NotNull PlayerControl.Control action) {
        for (ActionListener listener : new HashSet<>(listeners)) {
            listener.onAction(action);
        }
    }

    public void addActionListener(@NotNull ActionListener listener) {
        listeners.add(listener);
    }

    public boolean removeActionListener(@NotNull ActionListener listener) {
        return listeners.remove(listener);
    }

    public interface ActionListener {
        void onAction(@NotNull PlayerControl.Control action);
    }
}
