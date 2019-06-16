package rgl.map;

import org.junit.Test;
import rgl.logic.GameContext;
import rgl.map.terrain.TerrainMap;
import rgl.map.terrain.TerrainMapImpl;

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
        assertTrue(wml.isPassable(tm.getExitPoint()));
    }
}
