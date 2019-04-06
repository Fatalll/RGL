package generators;

import map.terrain.TerrainMap;

public interface TerrainMapGenerator {
    TerrainMap generateFromFile(String path);

    TerrainMap generateFromSeed(int seed);
}
