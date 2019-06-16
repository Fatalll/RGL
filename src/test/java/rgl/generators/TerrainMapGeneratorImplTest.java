package rgl.generators;

import org.junit.Test;
import rgl.map.terrain.TerrainMap;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TerrainMapGeneratorImplTest {

    @Test
    public void test1() throws Exception {
        int x = 20;
        int y = 10;
        Random rand = new Random();
        Point dims = new Point(x, y);

        for (int i = 0; i < 100; i++) {
            Point start;
            Point end;
            do {
                start = new Point(rand.nextInt(x - 2) + 1, rand.nextInt(y - 2) + 1);
                end = new Point(rand.nextInt(x - 2) + 1, rand.nextInt(y - 2) + 1);
            } while (start == end);
            TerrainMap.TerrainCellType[][] tr = new TerrainMapGenerator().generate(dims, start, end);
            assertEquals(tr[start.y][start.x], TerrainMap.TerrainCellType.VOID);
            assertEquals(tr[end.y][end.x], TerrainMap.TerrainCellType.VOID);

            Class<TerrainMapGenerator> cl = TerrainMapGenerator.class;
            Method bfs = cl.getDeclaredMethod("bfs", Point.class, TerrainMap.TerrainCellType[][].class, Point.class);
            bfs.setAccessible(true);
            boolean[][] res = (boolean[][]) bfs.invoke(null, start, tr, dims);
            assertTrue(res[start.y][start.x]);
            assertTrue(res[end.y][end.x]);
        }
    }
}
