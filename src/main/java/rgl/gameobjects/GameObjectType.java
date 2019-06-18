package rgl.gameobjects;

/**
 * enum of all types of game objects
 */
public enum GameObjectType {
    NONE,             // not a game object
    PLAYER,           // player
    HOSTILE_COWARD,   // mob with a cowardly strategy
    HOSTILE_PASS,     // mob with a passive strategy
    HOSTILE_AGR,      // mob with a aggressive strategy
    WALL,             // wall aka not passable
    FLOOR1,           // floor aka passable type 1
    FLOOR2,           // floor aka passable type 2
    FLOOR3,           // floor aka passable type 3
    EXIT,             // exit from the map
    RINGITEM,         // item on the map (ring)
    HOODITEM          // item on the map (hood)
}
