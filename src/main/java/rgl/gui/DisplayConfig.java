package rgl.gui;

import rgl.gameobjects.GameObjectType;

import java.util.EnumMap;

public class DisplayConfig<T> {
    private EnumMap<GameObjectType, T> config;

    public DisplayConfig(EnumMap<GameObjectType, T> config) {
       this.config = config;
    }
    public DisplayConfig() {
        this.config = new EnumMap<>(GameObjectType.class);
    }

    public void set(GameObjectType key, T val) {
        config.put(key, val);
    }

    public T get(GameObjectType type) {
        return config.get(type);
    }
}
