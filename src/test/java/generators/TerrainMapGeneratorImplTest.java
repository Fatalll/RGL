package generators;

import map.terrain.TerrainMap;
import static org.junit.Assert.*;
import org.junit.Test;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.Random;

public class TerrainMapGeneratorImplTest {

	@Test
	public void test1() throws  Exception {
	    int x = 20;
	    int y = 10;
		Random rand = new Random();
		Point dims = new Point(x, y);

		for (int i = 0; i < 10; i++) {
			Point start;
			Point end;
			do {
				start = new Point(rand.nextInt(x - 2 + 1), rand.nextInt(y - 2) + 1);
				end = new Point(rand.nextInt(x - 2) + 1, rand.nextInt(y - 2) + 1);
			} while (start == end);

			TerrainMap.TerrainCellType[][] tr = new TerrainMapGenerator().generate(dims, start, end);
			Class<TerrainMapGenerator> cl = TerrainMapGenerator.class;
			Method bfs = cl.getDeclaredMethod("bfs", Point.class, TerrainMap.TerrainCellType[][].class, Point.class);
			bfs.setAccessible(true);
			boolean[][] res = (boolean[][]) bfs.invoke(null, start, tr, dims);
			assertTrue(res[start.y][start.x]);
			assertTrue(res[end.y][end.x]);
		}
	}
}
