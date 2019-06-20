package ru.ifmo.rgl.map;

import org.junit.Test;
import ru.ifmo.rgl.logic.GameContext;
import ru.ifmo.rgl.map.terrain.TerrainMap;
import ru.ifmo.rgl.map.terrain.TerrainMapImpl;

import java.awt.*;
import java.util.Collections;
import java.util.IdentityHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorldMapLayoutTest {

    private GameContext context = new GameContext(new TerrainMapImpl(100, 29),
            Collections.newSetFromMap(new IdentityHashMap<>()));

    @Test
    public void testDimensions() {
        TerrainMap tm = new TerrainMapImpl(10, 10);
        WorldMapLayout wml = new WorldMapLayout(tm, context);
        assertEquals(wml.getDimensions(), new Point(10, 10));
    }

    @Test
    public void testExitPassable() {
        TerrainMap tm = new TerrainMapImpl(10, 10);
        WorldMapLayout wml = new WorldMapLayout(tm, context);
        assertTrue(wml.isPassable(tm.getExitPoint()));
    }
}
