package rgl.map.terrain;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class TerrainMapImplTest {
    @Test
    public void testGenerate() {
        TerrainMap tm = new TerrainMapImpl(10, 10);
        Point dims = tm.getDimensions();
        assertEquals(dims, new Point(10, 10));
        Point s = tm.getEnterPoint();
        Point e = tm.getExitPoint();
        assertEquals(tm.getCellType(s), TerrainMap.TerrainCellType.VOID);
        assertEquals(tm.getCellType(e), TerrainMap.TerrainCellType.VOID);
    }

}