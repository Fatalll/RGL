package map;

public interface WorldMapGenerator<T> {
    WorldMapLayout generateWorldMapLayout(TerrainMap terrain, Complexity complexity);

    enum Complexity {
        EASY,
        MEDIUM,
        HARD,
    }
}
