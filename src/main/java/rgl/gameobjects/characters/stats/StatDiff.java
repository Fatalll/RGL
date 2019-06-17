package rgl.gameobjects.characters.stats;

/**
 * Amount of points that the item added to the player.
 */
public class StatDiff {
    private StatType type;
    private int diff;

    public StatDiff(StatType type, int diff) {
        this.type = type;
        this.diff = diff;
    }

    public StatType getType() {
        return type;
    }

    public int getDiff() {
        return diff;
    }
}
