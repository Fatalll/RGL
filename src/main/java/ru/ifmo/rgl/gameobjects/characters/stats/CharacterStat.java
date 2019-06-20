package ru.ifmo.rgl.gameobjects.characters.stats;

/**
 * Character stats.
 */
public class CharacterStat implements Stat {
    private int val;
    private int max;

    public CharacterStat(int max) {
        this.val = max;
        this.max = max;
    }

    public int get() {
        return val;
    }

    public void set(int val) {
        this.val = Math.min(val, max);
    }
}
