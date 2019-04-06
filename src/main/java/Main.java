import game_objects.Player;
import map.WorldMap;
import map.WorldMapLayout;
import map.terrain.TerrainMap;
import map.terrain.TerrainMapImpl;
import utils.PlayerControl;

import java.awt.*;
import java.io.IOException;

import static map.terrain.TerrainMap.TerrainCellType.VOID;
import static map.terrain.TerrainMap.TerrainCellType.WALL;

public class Main {
    public static void main(String[] args) throws IOException {

        TerrainMap.TerrainCellType[][] map = {
                {WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL},
                {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
                {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
                {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
                {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
                {VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID},
                {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
                {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
                {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
                {WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL}
        };

        //TerrainMap terrain = new TerrainMapImpl(map, new Point(5, 0), new Point(5, 9), 10, 10);
        TerrainMap terrain = new TerrainMapImpl(map.getClass().getClassLoader().getResource("test.map").getPath());


        Player player = new Player();

        WorldMapLayout layout = new WorldMapLayout(terrain, player);
        WorldMap world = new WorldMap(layout, player);

        try {
            while (true) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        System.out.print(layout.displayCell(new Point(i, j)));
                        System.out.print(' ');
                    }
                    System.out.println();
                }

                System.out.println("Please, enter a command sequence:");
                char c = (char) System.in.read();
                System.in.skip(1);

                if (c == 'w') {
                    world.step(PlayerControl.Control.UP);
                } else if (c == 's') {
                    world.step(PlayerControl.Control.DOWN);
                } else if (c == 'a') {
                    world.step(PlayerControl.Control.LEFT);
                } else if (c == 'd') {
                    world.step(PlayerControl.Control.RIGHT);
                } else {
                    world.step(PlayerControl.Control.SKIP);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
