package map.terrain;

import generators.TerrainMapGenerator;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.Random;


public class TerrainMapImpl implements TerrainMap {
    public TerrainCellType [][] terrain;
    private Point enterPoint;
    private Point exitPoint;
    private Point dims;

    public TerrainMapImpl(int width, int height) throws IOException {
        this.dims = new Point(width, height);
        Random rand = new Random();
        do {
            enterPoint = new Point(rand.nextInt(width - 2 + 1), rand.nextInt(height - 2) + 1);
            exitPoint = new Point(rand.nextInt(width - 2) + 1, rand.nextInt(height- 2) + 1);
        } while (enterPoint == exitPoint);

        terrain = new TerrainMapGenerator().generate(new Point(width, height), enterPoint, exitPoint);
    }

    public TerrainMapImpl(String fileName, int width, int height) throws IOException {
		FileReader fr = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fr);
        this.dims = new Point(width, height);
        Random rand = new Random();
        deserialize(reader);
    }

    public TerrainMapImpl(TerrainCellType [][] terrain, Point enterPoint, Point exitPoint, int width, int height) {
        this.terrain = terrain;
        this.enterPoint = enterPoint;
        this.exitPoint = exitPoint;
        this.dims = new Point(width, height);
    }

    @Override
    public @NotNull Point getDimensions() {
        return dims;
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
        int height = dims.y;
        int width = dims.x;
        terrain = new TerrainCellType[height][width];

        for (int i = 0; i < height; i++) {
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
				throw new IOException("Corrupted data. Size must be " + width + "x" + height + ".");
            }

            if (line.length() != width) {
				throw new IOException("Corrupted data. Size must be " + width + "x" + height + ".");
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
                        enterPoint = new Point(i, j);
                        terrain[i][j] = TerrainCellType.VOID;
                        break;
                    case 'e':
                        exitPoint = new Point(i, j);
                        terrain[i][j] = TerrainCellType.VOID;
                        break;
                    default:
                        throw new IOException("Corrupted data. Wrong symbol: " + cellType);
                }
            }
        }
    }
}
