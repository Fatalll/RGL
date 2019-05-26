package game_objects.items;

import game_objects.*;
import logic.GameContext;

import java.util.*;

public abstract class Item extends GameObject<GameObjectType> {
    String name;
    Map<StatType, Integer> property;

    public Item(GameContext context, String name, Map<StatType, Integer> prop) {
        super(context);
        this.name = name;
        this.property = prop;
    }

    public String desciption() {
        return "`" + name + "' " + showProp();
    }

    public String showProp() {
       StringBuilder str = new StringBuilder();
        property.forEach((key, value) -> str.append(key.show() + " " + value + " "));
        return str.toString();
    }

    public int getDiff(StatType type) {
        return property.getOrDefault(type, 0);
    }
}
