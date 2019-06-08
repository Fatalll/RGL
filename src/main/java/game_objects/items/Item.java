package game_objects.items;

import game_objects.GameObject;
import game_objects.GameObjectType;
import game_objects.StatType;
import logic.GameContext;
import protobuf.GameObjectsProto;
import protobuf.Serializable;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public final Serializable<GameObjectsProto.Item> getAsSerializableItem() {
        return new SerializableItemImpl();
    }

    private class SerializableItemImpl implements Serializable<GameObjectsProto.Item> {

        @Override
        public GameObjectsProto.Item serializeToProto() {
            return GameObjectsProto.Item.newBuilder()
                    .setName(name)
                    .setPosition(GameObjectsProto.Position.newBuilder().setX(getPosition().x).setY(getPosition().y).build())
                    .addAllStats(property.entrySet().stream().map(
                            statTypeStatEntry -> GameObjectsProto.StatEntry.newBuilder()
                                    .setStatType(statTypeStatEntry.getKey().toString())
                                    .setStatValue(statTypeStatEntry.getValue()).build()).collect(Collectors.toList()))
                    .setItemType(display().toString())
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.Item object) {
            name = object.getName();
            property = object.getStatsList().stream()
                    .map(statEntry -> new AbstractMap.SimpleEntry<>(StatType.valueOf(statEntry.getStatType()), statEntry.getStatValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
            Point position = new Point(object.getPosition().getX(), object.getPosition().getY());
            moveToCell(context.getWorld().getCell(position));
        }
    }
}
