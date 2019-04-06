package map;

public interface TerrainMapGenerator {
    TerrainMap generateFromFile(String path);

    TerrainMap generateFromSeed(int seed);
}
