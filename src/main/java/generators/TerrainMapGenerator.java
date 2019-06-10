package generators;

import map.terrain.TerrainMap;

import java.awt.*;
import java.util.*;

public class TerrainMapGenerator {
	private Random rand;

	public TerrainMapGenerator() {
		rand = new Random();
	}

	public TerrainMapGenerator(long seed) {
		rand = new Random(seed);
	}

    public TerrainMap.TerrainCellType[][] generate(Point dims, Point s, Point e) {
		TerrainMap.TerrainCellType[][] terrain = new TerrainMap.TerrainCellType[dims.y][dims.x];
		for (int i = 0; i < dims.y; ++i) {
			for (int j = 0; j < dims.x; ++j) {
				terrain[i][j] = TerrainMap.TerrainCellType.VOID;
			}
		}
		generateBorders(terrain, dims);
		generateWalls(terrain, s, e, dims);

		return terrain;
    }

    private void generateWalls(TerrainMap.TerrainCellType[][] terrain, Point s, Point e, Point dims) {
		ArrayList<Point> lst = new ArrayList<>();
		for (int i = 0; i < dims.y; ++i) {
			for (int j = 0; j < dims.x; ++j) {
				if (terrain[i][j] == TerrainMap.TerrainCellType.WALL
				  || (i == s.y && j == s.x) || (i == e.y && j == e.x)) {
    				  continue;
				}
				lst.add(new Point(j, i));
			}
		}
		int free = (int)(lst.size() * 0.7);
		while (lst.size() != free) {
			Point p = lst.remove(rand.nextInt(lst.size() - 1));
			if (!check_neibs(terrain, p, dims)) {
				continue;
			}

			terrain[p.y][p.x] = TerrainMap.TerrainCellType.WALL;
			boolean[][] visited = bfs(s, terrain, dims);
			boolean bad = false;
			for (int i = 0; i < dims.y; ++i) {
				for (int j = 0; j < dims.x; ++j) {
					if (terrain[i][j] == TerrainMap.TerrainCellType.VOID && !visited[i][j]) {
						bad = true;
						break;
					}
				}
				if (bad) {
    				break;
				}
			}
			if (bad) {
    			terrain[p.y][p.x] = TerrainMap.TerrainCellType.VOID;
			}
		}
    }

    private static boolean valid(Point p, Point dims) {
		return p.x >= 0 && p.x < dims.x && p.y >= 0 && p.y < dims.y;
    }

	private boolean check_neibs(TerrainMap.TerrainCellType[][] terrain, Point pnt, Point dims) {
    	Point[] pts = {new Point(pnt.x, pnt.y + 1)
    	              , new Point(pnt.x, pnt.y - 1)
    	              , new Point(pnt.x - 1, pnt.y)
    	              , new Point(pnt.x + 1, pnt.y)};
		int cnt = 0;
		for (Point p : pts) {
			if (valid(p, dims) && terrain[p.y][p.x] != TerrainMap.TerrainCellType.WALL) {
				cnt++;
			}
		}
		return cnt >= 3;
	}

    private static boolean[][] bfs(Point start, TerrainMap.TerrainCellType[][] terrain, Point dims) {
		Queue<Point> q = new ArrayDeque<>();
		boolean[][] visited = new boolean[terrain.length][terrain[0].length];

		q.add(start);
		visited[start.y][start.x] = true;
		while (!q.isEmpty()) {
			Point p = q.remove();
        	Point[] pts = {new Point(p.x, p.y + 1)
        	              , new Point(p.x, p.y - 1)
        	              , new Point(p.x - 1, p.y)
        	              , new Point(p.x + 1, p.y)};
			for (Point move : pts) {
				if (!valid(move, dims) || visited[move.y][move.x] || terrain[move.y][move.x] == TerrainMap.TerrainCellType.WALL) {
    				continue;
				}
				visited[move.y][move.x] = true;
				q.add(move);
			}
		}
		return visited;
    }


	private void generateBorders(TerrainMap.TerrainCellType[][] terrain, Point dims) {
	    generateRooms(terrain, 0, 0, dims.x, dims.y);
	}

   	private void generateRooms(TerrainMap.TerrainCellType[][] terrain, int sx, int sy, int ex, int ey) {
		for (int i = sx; i < ex; ++i) {
			terrain[sy][i] = TerrainMap.TerrainCellType.WALL;
			terrain[ey - 1][i] = TerrainMap.TerrainCellType.WALL;
		}

		for (int i = sy; i < ey; ++i) {
			terrain[i][sx] = TerrainMap.TerrainCellType.WALL;
			terrain[i][ex - 1] = TerrainMap.TerrainCellType.WALL;
		}
	}
}
