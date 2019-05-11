package game_objects;

import gui.GUI;
import gui.PlayerControl;
import javafx.util.Pair;
import logic.GameContext;
import map.WorldMap;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Player extends Dummy implements GUI.ActionListener {
    protected int exp = 0;
    private Runnable onDeath;
    private int playerMap[][];

    public Player(@NotNull GameContext context, int lvl, Runnable onDeath) {
        super(context, lvl);
        this.onDeath = onDeath;
        health = 100000;
    }

    @NotNull
    @Override
    public Character display() {
        return '@';
    }

    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        calculatePlayerMap();

        attended = false;
        Point position = PlayerControl.calculateNextPosition(getPosition(), action);
        moveOrAttack(position);

        if (health <= 0) {
            onDeath.run();
        }

        lvlUpIfCan();
    }

    private void lvlUpIfCan() {
        if (Math.pow(2, lvl) < exp) {
            lvl += 1;

            Random random = new Random();
            health += 10 + random.nextInt(5);
            armor += random.nextInt(5);
            attack += random.nextInt(5);

            System.out.println("Lvl UP!");
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
}
