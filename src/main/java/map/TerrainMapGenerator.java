package map;

public interface TerrainMapGenerator<T> {
    TerrainMap<T> generateFromFile(String path);
    TerrainMap<T> generateFromSeed(int seed);
}
