package ru.ifmo.rgl.gui;

import ru.ifmo.rgl.gameobjects.GameObjectType;

import java.util.EnumMap;

/**
 * Display configuration.
 * <p>
 * This class determines how game objects displays.
 * <p>
 * `T` is an object which contains display desciption
 * (for instance, symbol and color).
 */
public class DisplayConfig<T> {
    private EnumMap<GameObjectType, T> config;

    public DisplayConfig(EnumMap<GameObjectType, T> config) {
        this.config = config;
    }

    public DisplayConfig() {
        this.config = new EnumMap<>(GameObjectType.class);
    }

    /**
     * Set value by key.
     *
     * @param key Object that represents type of the game object.
     * @param val Generic display desciption of some game object.
     */
    public void set(GameObjectType key, T val) {
        config.put(key, val);
    }

    /**
     * Get `T` by key.
     *
     * @param key Object that represents type of the game object.
     * @return val Generic display desciption of some game object.
     */
    public T get(GameObjectType type) {
        return config.get(type);
    }
}
