package map;

public class WorldMap<T> {

    WorldMapLayout layout;

    public WorldMap(WorldMapLayout layout) {
        this.layout = layout;
    }

    enum Control {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SKIP,
    }

//
//    public abstract void step(@NotNull Control action);
//
//    public abstract void moveGameObjcetToPosition(@NotNull Point position, @NotNull GameObject<T> gameObject);

}
