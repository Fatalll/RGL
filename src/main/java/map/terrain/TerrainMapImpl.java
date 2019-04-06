package map.terrain;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class TerrainMapImpl implements TerrainMap {
    public TerrainCellType [][] terrain;
    private Point enterPoint;
    private Point exitPoint;
    private int height;
    private int width;

    public TerrainMapImpl(TerrainCellType [][] terrain, Point enterPoint, Point exitPoint, int width, int height) {
        this.terrain = terrain;
        this.enterPoint = enterPoint;
        this.exitPoint = exitPoint;
        this.height = height;
        this.width = width;
    }

    public TerrainMapImpl(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        deserialize(reader);
    }

    @Override
    public @NotNull Point getDimensions() {
        return new Point(width, height);
    }

    @Override
    public @NotNull Point getEnterPoint() {
        return enterPoint;
    }

    @Override
    public @NotNull Point getExitPoint() {
        return exitPoint;
    }

    @Override
    public @NotNull TerrainCellType getCellType(Point position) {
        return terrain[position.y][position.x];
    }

    public void deserialize(BufferedReader in) throws IOException {
        String w = in.readLine();
        String h = in.readLine();

        width = Integer.parseInt(w);
        height = Integer.parseInt(h);
        terrain = new TerrainCellType[height][width];

        for (int i = 0; i < height; i++) {
            String line = in.readLine();
            if (line.length() != width) {
                throw new IOException("Corrupted data");
            }
            for (int j = 0; j < width; j++) {
                char cellType = line.charAt(j);
                switch (cellType) {
                    case '0':
                        terrain[i][j] = TerrainCellType.VOID;
                        break;
                    case '1':
                        terrain[i][j] = TerrainCellType.WALL;
                        break;
                    case 's':
                        enterPoint = new Point(j, i);
                        terrain[i][j] = TerrainCellType.VOID;
                        break;
                    case 'e':
                        exitPoint = new Point(j, i);
                        terrain[i][j] = TerrainCellType.VOID;
                        break;
                    default:
                        throw new IOException("Corrupted data");
                }
            }
        }
    }
}
