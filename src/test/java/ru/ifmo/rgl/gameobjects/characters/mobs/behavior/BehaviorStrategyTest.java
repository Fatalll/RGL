package ru.ifmo.rgl.gameobjects.characters.mobs.behavior;

import org.junit.Test;
import ru.ifmo.rgl.gameobjects.characters.player.Player;
import ru.ifmo.rgl.logic.GameContext;
import ru.ifmo.rgl.map.terrain.TerrainMapImpl;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BehaviorStrategyTest {

    private GameContext context;
    private Player player;

    public BehaviorStrategyTest() throws IOException {
        context = new GameContext(new TerrainMapImpl(getClass().getResource("/test.map").getPath(), 100, 29),
                Collections.newSetFromMap(new IdentityHashMap<>()));

        player = new Player(context, 1);
        context.addPlayer(player);
    }

    @Test
    public void testAggressiveBehavior() {
        BehaviorStrategy strategy = new AggressiveBehaviorStrategy();

        Point position = player.getPosition();
        context.getWorld().getCell(new Point(position.x - 1, position.y)).clearGameObject();
        player.calculatePlayerDistanceMap();

        Point farPosition = new Point(position.x - 5, position.y);
        assertEquals(farPosition, strategy.step(context, farPosition, 3));

        Point nearPosition = new Point(position.x - 2, position.y);
        assertEquals(new Point(position.x - 1, position.y), strategy.step(context, nearPosition, 5));
    }

    @Test
    public void testCowardlyBehavior() {
        BehaviorStrategy strategy = new CowardlyBehaviorStrategy();

        player.calculatePlayerDistanceMap();

        Point position = player.getPosition();

        Point farPosition = new Point(position.x - 5, position.y);
        assertEquals(farPosition, strategy.step(context, farPosition, 3));

        Point nearPosition = new Point(position.x + 2, position.y);
        assertNotEquals(new Point(position.x + 2, position.y), strategy.step(context, nearPosition, 5));
        assertNotEquals(new Point(position.x + 1, position.y), strategy.step(context, nearPosition, 5));
    }

    @Test
    public void testPassiveBehavior() {
        BehaviorStrategy strategy = new PassiveBehaviorStrategy();

        Point position = new Point(5, 5);
        assertEquals(position, strategy.step(context, position, 10));
    }
}