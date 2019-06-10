package logic.behavior;

import game_objects.Player;
import logic.GameContext;
import map.terrain.TerrainMapImpl;
import org.junit.Test;

import java.awt.*;
import java.util.Collections;
import java.util.IdentityHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BehaviorStrategyTest {

    private GameContext context = new GameContext(new TerrainMapImpl(100, 29),
            Collections.newSetFromMap(new IdentityHashMap<>()));

    @Test
    public void testAggressiveBehavior() {
        BehaviorStrategy strategy = new AggressiveBehaviorStrategy();

        Player player = context.getPlayer();
        player.calculatePlayerDistanceMap();

        Point position = player.getPosition();

        Point farPosition = new Point(position.x - 5, position.y - 5);
        assertEquals(strategy.step(context, farPosition, 5), farPosition);

        Point nearPosition = new Point(position.x + 2, position.y);
        assertEquals(strategy.step(context, nearPosition, 5), new Point(position.x + 1, position.y));
    }

    @Test
    public void testCowardlyBehavior() {
        BehaviorStrategy strategy = new CowardlyBehaviorStrategy();

        Player player = context.getPlayer();
        player.calculatePlayerDistanceMap();

        Point position = player.getPosition();

        Point farPosition = new Point(position.x - 5, position.y - 5);
        assertEquals(strategy.step(context, farPosition, 5), farPosition);

        Point nearPosition = new Point(position.x + 2, position.y);
        assertNotEquals(strategy.step(context, nearPosition, 5), new Point(position.x + 2, position.y));
        assertNotEquals(strategy.step(context, nearPosition, 5), new Point(position.x + 1, position.y));
    }

    @Test
    public void testPassiveBehavior() {
        BehaviorStrategy strategy = new PassiveBehaviorStrategy();

        Point position = new Point(5, 5);
        assertEquals(strategy.step(context, position, 10), position);
    }
}