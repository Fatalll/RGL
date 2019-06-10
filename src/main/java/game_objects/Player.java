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
    private int distanceToPlayerMap[][];

    public Player(@NotNull GameContext context, int lvl) {
        super(context, lvl);
        health = 100;
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return GameObjectType.PLAYER;
    }

    // reacting on Player pressing keyboard
    @Override
    public void onAction(PlayerControl.@NotNull Control action) {
        calculatePlayerDistanceMap(); // update distance map

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

    private double nextLevelExp() {
       return Math.pow(2, lvl);
    }

    @Override
    public List<Property> getStatus() {
        List<Property> p = super.getStatus();
        ArrayList<Property> p2 = new ArrayList<>(p);
        p2.add(() -> "Level: " + lvl);
        return p2;
    }
}
