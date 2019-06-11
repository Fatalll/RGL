package game_objects;

import game_objects.items.HoodItem;
import game_objects.items.Item;
import game_objects.items.RingItem;
import gui.GUI;
import gui.PlayerControl;
import javafx.util.Pair;
import logic.GameContext;
import map.WorldMap;
import org.jetbrains.annotations.NotNull;
import protobuf.GameObjectsProto;
import protobuf.Serializable;
import util.Property;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.stream.Collectors;

public class Player extends Dummy implements GUI.ActionListener {
    private final int maxInventorySize = 5;
    protected int exp = 0;
    private int distanceToPlayerMap[][];
    private List<Item> inventory = new ArrayList<>();

    public Player(@NotNull GameContext context, int lvl) {
        super(context, lvl);
        stats.replace(StatType.HEALTH, new CharacterStat(100));
    }

    // TODO: cache
    @Override
    public Stat getStat(StatType type) {
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
        if (context.getWorld().isPickable(position) && inventory.size() < maxInventorySize) {
            pickItem(position);
        } else {
            super.nextMove(position);
        }
    }

    @Override
    public List<Property> getStatus() {
        List<Property> p = super.getStatus();
        ArrayList<Property> p2 = new ArrayList<>(p);
        p2.add(() -> "Level: " + lvl);
        return p2;
    }

    // reacting on Player pressing keyboard
    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        calculatePlayerDistanceMap();

        attended = false;
        Point position = PlayerControl.calculateNextPosition(getPosition(), action);
        nextMove(position);

        lvlUpIfCan();
    }

    public void pickItem(@NotNull Point position) {
        Item item = (Item) context.getWorld().getCell(position).getGameObject();
        context.getWorld().getCell(position).clearGameObject();
        inventory.add(item);
        context.updateGameStatus("Pick up an item!");
    }

    public void dropItem(int n) {
        if (n > inventory.size() || inventory.isEmpty()) {
            context.updateGameStatus("Nothing to drop!");
            return;
        }
        inventory.remove(n);
        context.updateGameStatus("Drop the item!");
    }


    // calculate a map of distances to player in each point which can be reached (simple bfs)
    public void calculatePlayerDistanceMap() {
        WorldMap map = context.getWorld();
        distanceToPlayerMap = new int[map.getDimensions().x][map.getDimensions().y];
        // fill all points as unreachable
        for (int[] row : distanceToPlayerMap) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        // bfs queue
        Queue<Pair<Point, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(getPosition(), 0));

        while (!q.isEmpty()) {
            Pair<Point, Integer> it = q.poll();

            int counter = it.getValue();
            Point position = it.getKey();

            distanceToPlayerMap[position.x][position.y] = counter;
            counter++;

            // step to each possible direction, increment distance (counter)
            checkPositionAndAddToQueue(position.x + 1, position.y, counter, q);
            checkPositionAndAddToQueue(position.x - 1, position.y, counter, q);
            checkPositionAndAddToQueue(position.x, position.y + 1, counter, q);
            checkPositionAndAddToQueue(position.x, position.y - 1, counter, q);
        }
    }

    // check if point is passable, not processed yet, and add to queue
    private void checkPositionAndAddToQueue(int x, int y, int counter, Queue<Pair<Point, Integer>> queue) {
        WorldMap map = context.getWorld();
        if (map.isPassable(x, y) && distanceToPlayerMap[x][y] == Integer.MAX_VALUE) {
            distanceToPlayerMap[x][y] = Integer.MAX_VALUE - 1;
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
        return Math.pow(2, lvl);
    }

    private void lvlUpIfCan() {
        if (nextLevelExp() < exp) {
            lvl += 1;

            Stat health = stats.get(StatType.HEALTH);
            Stat armor = stats.get(StatType.ARMOR);
            Stat attack = stats.get(StatType.ATTACK);

            Random random = new Random();
            health.set(health.get() + 10 + random.nextInt(lvl * 2));
            armor.set(armor.get() + random.nextInt(lvl * 2));
            attack.set(attack.get() + random.nextInt(lvl * 2));

            context.updateGameStatus("Level UP!");
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
                    .addAllInventory(inventory.stream().map(item -> item.getAsSerializableItem().serializeToProto()).collect(Collectors.toList()))
                    .setDummy(getAsSerializableDummy().serializeToProto())
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.Player object) {
            getAsSerializableDummy().deserializeFromProto(object.getDummy());
            exp = object.getExp();
            inventory = object.getInventoryList().stream().map(item -> {
                LinkedHashMap<StatType, Integer> property = item.getStatsList().stream()
                        .map(statEntry -> new AbstractMap.SimpleEntry<>(StatType.valueOf(statEntry.getStatType()), statEntry.getStatValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));

                switch (GameObjectType.valueOf(item.getItemType())) {
                    case RINGITEM:
                        return new RingItem(context, item.getName(), property);
                    case HOODITEM:
                        return new HoodItem(context, item.getName(), property);
                    default:
                        return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }
}
