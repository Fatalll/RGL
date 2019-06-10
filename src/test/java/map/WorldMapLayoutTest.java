package map;

import static org.junit.Assert.*;

import game_objects.Player;
import map.terrain.TerrainMap;
import map.terrain.TerrainMapImpl;
import org.junit.Test;

import java.awt.*;

public class WorldMapLayoutTest {
    @Test
    public void test1() {
        Player p = new Player();
        TerrainMap tm = new TerrainMapImpl(10, 10);
        WorldMapLayout wml = new WorldMapLayout(tm, p);
        assertEquals(wml.getDimensions(), new Point(10, 10));
        // Player is here.
        assertFalse(wml.isPassable(tm.getEnterPoint()));
        assertTrue(wml.isPassable(tm.getExitPoint()));
    }
}