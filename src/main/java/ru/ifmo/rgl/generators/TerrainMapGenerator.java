package ru.ifmo.rgl.generators;

import ru.ifmo.rgl.map.terrain.TerrainMap;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

/**
 * Class which is responsible for landscape generation.
 */
public class TerrainMapGenerator {
    private Random rand;

    public TerrainMapGenerator() {
        rand = new Random();
    }

    /**
     * @param seed Seed for a generator of random numbers.
     */
    public TerrainMapGenerator(long seed) {
        rand = new Random(seed);
    }

    private static boolean valid(Point p, Point dims) {
        return p.x >= 0 && p.x < dims.x && p.y >= 0 && p.y < dims.y;
    }

    /*
     * Check connectivity.
     */
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
                if (!valid(move, dims) || visited[move.y][move.x]
                        || terrain[move.y][move.x] == TerrainMap.TerrainCellType.WALL) {
                    continue;
                }
                visited[move.y][move.x] = true;
                q.add(move);
            }
        }
        return visited;
    }

    /**
     * Generate a random landscape.
     * <p>
     * Instantiates new terrain object,
     *
     * @param s    Start point.
     * @param e    End point.
     * @param dims Terrain map dimentions.
     */
    public TerrainMap.TerrainCellType[][] generate(Point dims, Point s, Point e) {
        TerrainMap.TerrainCellType[][] terrain = new TerrainMap.TerrainCellType[dims.y][dims.x];
        for (int i = 0; i < dims.y; ++i) {
            for (int j = 0; j < dims.x; ++j) {
                terrain[i][j] = TerrainMap.TerrainCellType.VOID;
            }
        }
        generateBorders(terrain, dims);
        terrain[s.y][s.x] = TerrainMap.TerrainCellType.VOID;
        terrain[e.y][e.x] = TerrainMap.TerrainCellType.VOID;
        generateWalls(terrain, s, e, dims);

        return terrain;
    }

    /**
     * Generate some random walls on the rgl.map, saving connections between all
     * pairs of two empty cells.
     */
    private void generateWalls(TerrainMap.TerrainCellType[][] terrain, Point s, Point e, Point dims) {
        // List of points that can contain walls.
        ArrayList<Point> lst = new ArrayList<>();
        for (int i = 0; i < dims.y; ++i) {
            for (int j = 0; j < dims.x; ++j) {
                // Dont' add walls, start point and end point to the list.
                if (terrain[i][j] == TerrainMap.TerrainCellType.WALL
                        || (i == s.y && j == s.x) || (i == e.y && j == e.x)) {
                    continue;
                }

                lst.add(new Point(j, i));
            }
        }
        // Regulate number of walls.
        int free = (int) (lst.size() * 0.7);

        while (lst.size() != free) {
            Point p = lst.remove(rand.nextInt(lst.size() - 1));
            if (!checkNeibs(terrain, p, dims)) {
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

    private boolean checkNeibs(TerrainMap.TerrainCellType[][] terrain, Point pnt, Point dims) {
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
        // A wall can be placed if the cell have more than 3 neighbors.
        return cnt >= 3;
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
