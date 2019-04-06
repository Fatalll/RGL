package generators;

import map.WorldMapLayout;
import map.terrain.TerrainMap;

public interface WorldMapGenerator<T> {
    WorldMapLayout generateWorldMapLayout(TerrainMap terrain, Complexity complexity);

    enum Complexity {
        EASY,
        MEDIUM,
        HARD,
    }
}
