package rgl.gameobjects.items;

import rgl.gameobjects.GameObject;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.characters.stats.StatType;
import rgl.logic.GameContext;
import rgl.proto.GameObjectsProto;
import rgl.util.Serializable;

import java.awt.*;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Item extends GameObject<GameObjectType> {
    private String name;
    private Map<StatType, Integer> property;

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
                    .setPosition(GameObjectsProto.Position.newBuilder()
                            .setX(isPresent() ? getPosition().x : 0)
                            .setY(isPresent() ? getPosition().y : 0).build()) // TODO its hack
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
            moveToCell(getContext().getWorld().getCell(position));
        }
    }
}
