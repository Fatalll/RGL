package gui;

import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public abstract class GUI {
    protected GameContext context;
    private Set<ActionListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());
    private List<ActionListener> listenersToRemove = new ArrayList<>();
    private List<ActionListener> listenersToAdd = new ArrayList<>();

    public GUI(@NotNull GameContext context) {
        this.context = context;
    }

    public abstract boolean iteration() throws IOException;

    public abstract void reload();

    protected void onPlayerAction(@NotNull PlayerControl.Control action) {
        for (ActionListener listener : listenersToRemove) {
            listeners.remove(listener);
        }

        listenersToRemove.clear();

        listeners.addAll(listenersToAdd);
        listenersToAdd.clear();

        for (ActionListener listener : listeners) {
            listener.onAction(action);
        }
    }

    public void addActionListener(@NotNull ActionListener listener) {
        listenersToAdd.add(listener);
    }

    public boolean removeActionListener(@NotNull ActionListener listener) {
        listenersToRemove.add(listener);
        return listeners.contains(listener);
    }

    public interface ActionListener {
        void onAction(@NotNull PlayerControl.Control action);
    }
}
