package generators;

public class TerrainMapGeneratorFactory {
    public static WorldMapGenerator<Character> createTerrainMapGenerator() {
        return new WorldMapGeneratorChars();
    }
}
