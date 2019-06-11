package map;

import logic.GameContext;
import map.terrain.TerrainMap;
import map.terrain.TerrainMapImpl;
import org.junit.Test;

import java.awt.*;
import java.util.Collections;
import java.util.IdentityHashMap;

import static org.junit.Assert.*;

public class WorldMapLayoutTest {

    private GameContext context = new GameContext(new TerrainMapImpl(100, 29),
            Collections.newSetFromMap(new IdentityHashMap<>()));

    @Test
    public void test1() {
        TerrainMap tm = new TerrainMapImpl(10, 10);
        WorldMapLayout wml = new WorldMapLayout(tm, context);
        assertEquals(wml.getDimensions(), new Point(10, 10));
        // Player is here.
        assertFalse(wml.isPassable(tm.getEnterPoint()));
        assertTrue(wml.isPassable(tm.getExitPoint()));
    }
}