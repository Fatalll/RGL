package game_objects;

public enum StatType {
    HEALTH("HP"),
    ARMOR("AR"),
    ATTACK("AT");

    private final String descr;
    StatType(String at) {
        this.descr = at;
    }

    public String show() {
        return descr;
    }
}
