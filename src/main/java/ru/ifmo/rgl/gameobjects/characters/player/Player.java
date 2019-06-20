package ru.ifmo.rgl.gameobjects.characters.player;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObjectType;
import ru.ifmo.rgl.gameobjects.characters.Dummy;
import ru.ifmo.rgl.gameobjects.characters.stats.CharacterStat;
import ru.ifmo.rgl.gameobjects.characters.stats.Stat;
import ru.ifmo.rgl.gameobjects.characters.stats.StatType;
import ru.ifmo.rgl.gameobjects.items.HoodItem;
import ru.ifmo.rgl.gameobjects.items.Item;
import ru.ifmo.rgl.gameobjects.items.RingItem;
import ru.ifmo.rgl.gui.GUI;
import ru.ifmo.rgl.gui.PlayerControl;
import ru.ifmo.rgl.logic.GameContext;
import ru.ifmo.rgl.map.WorldMap;
import ru.ifmo.rgl.proto.GameObjectsProto;
import ru.ifmo.rgl.util.Pair;
import ru.ifmo.rgl.util.Property;
import ru.ifmo.rgl.util.Serializable;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.stream.Collectors;

/**
 * player
 */
public class Player extends Dummy implements GUI.ActionListener {
    private final int maxInventorySize = 5;
    private int exp = 0;
    private String id = "";
    private int confusionTimeout = 0;
    private int distanceToPlayerMap[][];
    private List<Item> inventory = new ArrayList<>();

    public Player(@NotNull GameContext context, int lvl) {
        super(context, lvl);
        getStats().replace(StatType.HEALTH, new CharacterStat(100));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * add exp for something
     *
     * @param exp exp
     */
    public void addExp(int exp) {
        this.exp += exp;
    }

    public void confuse(int timeout) {
        confusionTimeout = timeout;
    }

    public boolean isConfused() {
        return confusionTimeout > 0;
    }

    // TODO: cache
    @Override
    public Stat getStat(@NotNull StatType type) {
        int stat = super.getStat(type).get();
        for (Item item : inventory) {
            stat += item.getDiff(type);
        }
        return new CharacterStat(stat);
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.PLAYER;
    }

    @Override
    public void nextMove(@NotNull Point position) {
        if (getContext().getWorld().isPickable(position) && inventory.size() < maxInventorySize) {
            pickItem(position);
        } else {
            super.nextMove(position);
        }
    }

    @Override
    public List<Property> getStatus() {
        List<Property> p = super.getStatus();
        ArrayList<Property> p2 = new ArrayList<>(p);
        p2.add(() -> "Level: " + getLvl());
        return p2;
    }

    // reacting on Player pressing keyboard
    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        if (confusionTimeout > 0) confusionTimeout--;

        calculatePlayerDistanceMap();

        attend();
        Point position = PlayerControl.calculateNextPosition(getPosition(), action);
        nextMove(position);

        lvlUpIfCan();
    }

    public void pickItem(@NotNull Point position) {
        Item item = (Item) getContext().getWorld().getCell(position).getGameObject();
        getContext().getWorld().getCell(position).clearGameObject();
        inventory.add(item);
        getContext().updateGameStatus("Pick up an item!");
    }

    public void dropItem(int n) {
        if (n > inventory.size() || inventory.isEmpty()) {
            getContext().updateGameStatus("Nothing to drop!");
            return;
        }
        inventory.remove(n);
        getContext().updateGameStatus("Drop the item!");
    }


    /**
     * calculate a rgl.map of distances to player in each point which can be reached (simple bfs)
     */
    public void calculatePlayerDistanceMap() {
        WorldMap map = getContext().getWorld();
        distanceToPlayerMap = new int[map.getDimensions().y][map.getDimensions().x];
        // fill all points as unreachable
        for (int[] row : distanceToPlayerMap) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        distanceToPlayerMap[getPosition().y][getPosition().x] = 0;

        // bfs queue
        Queue<Pair<Point, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(getPosition(), 0));

        while (!q.isEmpty()) {
            Pair<Point, Integer> it = q.poll();

            int counter = it.getValue();
            Point position = it.getKey();

            counter++;

            // step to each possible direction, increment distance (counter)
            checkPositionAndAddToQueue(position.x + 1, position.y, counter, q);
            checkPositionAndAddToQueue(position.x - 1, position.y, counter, q);
            checkPositionAndAddToQueue(position.x, position.y + 1, counter, q);
            checkPositionAndAddToQueue(position.x, position.y - 1, counter, q);
        }
    }

    private void checkPositionAndAddToQueue(int x, int y, int counter, Queue<Pair<Point, Integer>> queue) {
        WorldMap map = getContext().getWorld();
        if (map.isPassable(x, y) && distanceToPlayerMap[y][x] == Integer.MAX_VALUE) {
            distanceToPlayerMap[y][x] = counter;
            queue.add(new Pair<>(new Point(x, y), counter));
        }
    }

    public int[][] getDistanceToPlayerMap() {
        return distanceToPlayerMap;
    }

    public List<Item> getInventory() {
        return inventory;
    }


    private double nextLevelExp() {
        return Math.pow(2, getLvl());
    }

    private void lvlUpIfCan() {
        if (nextLevelExp() < exp) {
            setLvl(getLvl() + 1);

            Stat health = getStats().get(StatType.HEALTH);
            Stat armor = getStats().get(StatType.ARMOR);
            Stat attack = getStats().get(StatType.ATTACK);

            Random random = new Random();
            health.set(health.get() + 10 + random.nextInt(getLvl() * 2));
            armor.set(armor.get() + random.nextInt(getLvl() * 2));
            attack.set(attack.get() + random.nextInt(getLvl() * 2));

            getContext().updateGameStatus("Level UP!");
        }
    }

    final public Serializable<GameObjectsProto.Player> getAsSerializablePlayer() {
        return new SerializablePlayerImpl();
    }

    protected class SerializablePlayerImpl implements Serializable<GameObjectsProto.Player> {
        @Override
        public GameObjectsProto.Player serializeToProto() {
            return GameObjectsProto.Player.newBuilder()
                    .setExp(exp)
                    .setId(id)
                    .addAllInventory(inventory.stream().map(item -> item.getAsSerializableItem().serializeToProto()).collect(Collectors.toList()))
                    .setDummy(getAsSerializableDummy().serializeToProto())
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.Player object) {
            getAsSerializableDummy().deserializeFromProto(object.getDummy());
            exp = object.getExp();
            id = object.getId();
            inventory = object.getInventoryList().stream().map(item -> {
                LinkedHashMap<StatType, Integer> property = item.getStatsList().stream()
                        .map(statEntry -> new AbstractMap.SimpleEntry<>(StatType.valueOf(statEntry.getStatType()), statEntry.getStatValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));

                switch (GameObjectType.valueOf(item.getItemType())) {
                    case RINGITEM:
                        return new RingItem(getContext(), item.getName(), property);
                    case HOODITEM:
                        return new HoodItem(getContext(), item.getName(), property);
                    default:
                        return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }
}
