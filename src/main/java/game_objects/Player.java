package game_objects;

import game_objects.items.Item;
import gui.GUI;
import gui.PlayerControl;
import javafx.util.Pair;
import logic.GameContext;
import map.WorldMap;
import org.jetbrains.annotations.NotNull;
import util.Property;

import java.awt.Point;
import java.util.*;

public class Player extends Dummy implements GUI.ActionListener {
    protected int exp = 0;
    private Runnable onDeath;
    private int playerMap[][];

    private List<Item> inventory = new ArrayList<>();
    private final int maxInventorySize = 5;

    public Player(@NotNull GameContext context, int lvl, Runnable onDeath) {
        super(context, lvl);
        this.onDeath = onDeath;
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

    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        calculatePlayerMap();

        attended = false;
        Point position = PlayerControl.calculateNextPosition(getPosition(), action);
        //moveOrAttack(position);
        nextMove(position);

        if (stats.get(StatType.HEALTH).get() <= 0) {
            onDeath.run();
        }

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


    public void calculatePlayerMap() {
        WorldMap map = context.getWorld();
        playerMap = new int[map.getDimensions().x][map.getDimensions().y];
        for (int[] row : playerMap) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        Queue<Pair<Point, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(getPosition(), 0));

        while (!q.isEmpty()) {
            Pair<Point, Integer> it = q.poll();

            int counter = it.getValue();
            Point position = it.getKey();

            playerMap[position.x][position.y] = counter;
            counter++;

            if (map.isPassable(position.x + 1, position.y) && playerMap[position.x + 1][position.y] == Integer.MAX_VALUE) {
                playerMap[position.x + 1][position.y] = Integer.MAX_VALUE - 1;
                q.add(new Pair<>(new Point(position.x + 1, position.y), counter));
            }

            if (map.isPassable(position.x - 1, position.y) && playerMap[position.x - 1][position.y] == Integer.MAX_VALUE) {
                playerMap[position.x - 1][position.y] = Integer.MAX_VALUE - 1;
                q.add(new Pair<>(new Point(position.x - 1, position.y), counter));
            }

            if (map.isPassable(position.x, position.y + 1) && playerMap[position.x][position.y + 1] == Integer.MAX_VALUE) {
                playerMap[position.x][position.y + 1] = Integer.MAX_VALUE - 1;
                q.add(new Pair<>(new Point(position.x, position.y + 1), counter));
            }

            if (map.isPassable(position.x, position.y - 1) && playerMap[position.x][position.y - 1] == Integer.MAX_VALUE) {
                playerMap[position.x][position.y - 1] = Integer.MAX_VALUE - 1;
                q.add(new Pair<>(new Point(position.x, position.y - 1), counter));
            }
        }
    }

    public int[][] getPlayerMap() {
        return playerMap;
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

}
