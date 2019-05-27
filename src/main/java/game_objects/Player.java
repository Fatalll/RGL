package game_objects;

import gui.GUI;
import gui.PlayerControl;
import javafx.util.Pair;
import logic.GameContext;
import map.WorldMap;
import org.jetbrains.annotations.NotNull;
import util.Property;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

public class Player extends Dummy implements GUI.ActionListener {
    protected int exp = 0;
    private int playerMap[][];

    public Player(@NotNull GameContext context, int lvl) {
        super(context, lvl);
        health = 100;
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.PLAYER;
    }

    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        calculatePlayerMap();

        attended = false;
        Point position = PlayerControl.calculateNextPosition(getPosition(), action);
        moveOrAttack(position);

        lvlUpIfCan();
    }

    private void lvlUpIfCan() {
        if (nextLevelExp() < exp) {
            lvl += 1;

            Random random = new Random();
            health += 10 + random.nextInt(5);
            armor += random.nextInt(5);
            attack += random.nextInt(5);

            context.updateGameStatus("Level UP!");
        }
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

    private double nextLevelExp() {
       return Math.pow(2, lvl);
    }

    @Override
    public List<Property> getStatus() {
        List<Property> p = super.getStatus();
        ArrayList<Property> p2 = new ArrayList<>(p);
        p2.add(() -> "Level: " + lvl);
        //p2.add(() -> "Exp: " + exp);
        //p2.add(() -> "Until next level: " + (int)(nextLevelExp() - exp));
        return p2;
    }
}
