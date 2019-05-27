package gui;

import game_objects.GameObjectType;

import java.util.EnumMap;
import java.util.HashMap;

public class DisplayConfig<T> {
    private EnumMap<GameObjectType, DisplayGameObject<T>> config;

    public DisplayConfig(EnumMap<GameObjectType, DisplayGameObject<T>> config) {
       this.config = config;
    }
    public DisplayConfig() {
        this.config = new EnumMap<>(GameObjectType.class);
    }

    public void set(GameObjectType key, DisplayGameObject<T> val) {
        config.put(key, val);
    }

    public DisplayGameObject get(GameObjectType type) {
        return config.get(type);
    }
}
