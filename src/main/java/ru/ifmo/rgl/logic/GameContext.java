package ru.ifmo.rgl.logic;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObjectType;
import ru.ifmo.rgl.gameobjects.characters.mobs.AggressiveMob;
import ru.ifmo.rgl.gameobjects.characters.mobs.CowardMob;
import ru.ifmo.rgl.gameobjects.characters.mobs.Hostile;
import ru.ifmo.rgl.gameobjects.characters.mobs.PassiveMob;
import ru.ifmo.rgl.gameobjects.characters.player.Player;
import ru.ifmo.rgl.gameobjects.items.HoodItem;
import ru.ifmo.rgl.gameobjects.items.Item;
import ru.ifmo.rgl.gameobjects.items.RingItem;
import ru.ifmo.rgl.map.WorldMap;
import ru.ifmo.rgl.map.WorldMapLayout;
import ru.ifmo.rgl.map.terrain.TerrainMap;
import ru.ifmo.rgl.map.terrain.cells.*;
import ru.ifmo.rgl.proto.GameObjectsProto;
import ru.ifmo.rgl.util.Serializable;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * game context, contains game specific info which needed in all parts of the program
 */
public class GameContext {
    private Map<UUID, Player> players = new ConcurrentHashMap<>();
    private WorldMap world;
    private GameStatus status;

    // listeners called on each game tick
    private Set<GameLoop.IterationListener> listeners;

    public GameContext(@NotNull TerrainMap initialMap, @NotNull Set<GameLoop.IterationListener> listeners) {
        this.listeners = listeners;

        world = new WorldMap(new WorldMapLayout(initialMap, this), this);
        status = new GameStatus();
    }

    @NotNull
    public Map<UUID, Player> getPlayers() {
        return players;
    }

    public int[][] getDistanceMap() {
        int[][] map = new int[world.getDimensions().y][world.getDimensions().x];
        for (int[] row : map) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        for (Player player : players.values()) {
            if (player.getDistanceToPlayerMap() == null) continue;
            for (int x = 0; x < world.getDimensions().x; x++) {
                for (int y = 0; y < world.getDimensions().y; y++) {
                    map[y][x] = Math.min(map[y][x], player.getDistanceToPlayerMap()[y][x]);
                }
            }
        }

        return map;
    }

    public UUID addPlayer(@NotNull Player player) {
        UUID uuid;
        if (player.getId().isEmpty()) {
            uuid = UUID.randomUUID();
            player.setId(uuid.toString());
        } else {
            uuid = UUID.fromString(player.getId());
        }

        if (players.isEmpty()) {
            world.initializePlayer(player);
        } else {
            world.initializePlayerRandomly(player);
        }

        players.put(uuid, player);

        return uuid;
    }

    @NotNull
    public WorldMap getWorld() {
        return world;
    }

    /**
     * add listener for game loop iteration
     *
     * @param listener listener
     */
    public void addIterationListener(@NotNull GameLoop.IterationListener listener) {
        listeners.add(listener);
    }

    /**
     * remove listener for game loop iteration
     *
     * @param listener listener
     */
    public void removeIterationListener(@NotNull GameLoop.IterationListener listener) {
        listeners.remove(listener);
    }


    public GameStatus getGameStatus() {
        return status;
    }

    public void updateGameStatus(String msg) {
        status.updateStatus(msg);
    }

    public void appendGameStatus(String msg) {
        status.appendStatus(msg);
    }

    public final Serializable<GameObjectsProto.GameContext> getAsSerializableContext() {
        return new SerializableGameContextImpl();
    }

    private class SerializableGameContextImpl implements Serializable<GameObjectsProto.GameContext> {

        @Override
        public GameObjectsProto.GameContext serializeToProto() {
            Stream<Wall> wallStream = getWorld().getCellStream()
                    .filter(Wall.class::isInstance)
                    .map(Wall.class::cast);

            Stream<Floor> floorStream = getWorld().getCellStream()
                    .filter(Floor.class::isInstance)
                    .map(Floor.class::cast);

            Stream<Item> itemStream = getWorld().getCellStream()
                    .filter(Floor.class::isInstance)
                    .map(Floor.class::cast).map(Floor::getGameObject).filter(Item.class::isInstance).map(Item.class::cast);

            Stream<Hostile> hostileStream = getWorld().getCellStream()
                    .filter(Floor.class::isInstance)
                    .map(Floor.class::cast).map(Floor::getGameObject).filter(Hostile.class::isInstance).map(Hostile.class::cast);

            return GameObjectsProto.GameContext.newBuilder()
                    .setWidth(getWorld().getDimensions().x) // Save x dimension
                    .setHeight(getWorld().getDimensions().y) // Save y dimension
                    .setEntry(GameObjectsProto.Position.newBuilder().setX(getWorld().getLayout().getEntry().x).setY(getWorld().getLayout().getEntry().y).build()) // Save entry point
                    .setExit(GameObjectsProto.Position.newBuilder().setX(getWorld().getLayout().getExit().x).setY(getWorld().getLayout().getExit().y).build()) // Save exit point
                    .addAllFloors(floorStream.map(floor -> floor.getAsSerializableFloor().serializeToProto()).collect(Collectors.toList())) // Save all floors
                    .addAllWalls(wallStream.map(wall -> wall.getAsSerializableWall().serializeToProto()).collect(Collectors.toList())) // Save all walls
                    .addAllPlayer(getPlayers().values().stream().map(player -> player.getAsSerializablePlayer().serializeToProto()).collect(Collectors.toList()))
                    .addAllItems(itemStream
                            .map(item -> item.getAsSerializableItem().serializeToProto())
                            .collect(Collectors.toList())) // Save all items on the rgl.map
                    .addAllMobs(hostileStream
                            .map(mob -> mob.getAsSerializableHostile().serializeToProto()) // Save all mobs on the rgl.map
                            .collect(Collectors.toList()))
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.GameContext object) {
            listeners.clear(); // Remove old listeners

            int width = object.getWidth(); // Restore x dimension
            int height = object.getHeight(); // Restore y dimension
            Point entry = new Point(object.getEntry().getX(), object.getEntry().getY()); // Restore entry point
            Point exit = new Point(object.getExit().getX(), object.getExit().getY()); // Restore exit point
            Cell<GameObjectType>[][] world = new Cell[height][width]; // Create the new grid
            object.getFloorsList().forEach(floorCell -> {
                Floor floor = new Floor(null);
                floor.getAsSerializableFloor().deserializeFromProto(floorCell);
                world[floor.getPosition().y][floor.getPosition().x] = floor;
            }); // Fill the new grid with loaded floors
            object.getWallsList().forEach(wallCell -> {
                Wall wall = new Wall(null);
                wall.getAsSerializableWall().deserializeFromProto(wallCell);
                world[wall.getPosition().y][wall.getPosition().x] = wall;
            });// Fill the new grid with loaded walls
            world[exit.y][exit.x] = new Exit(exit); // Mark exit point on the grid
            world[entry.y][entry.x] = new Entry(entry);
            getWorld().getLayout().setWorld(world);
            getWorld().getLayout().setEntry(entry);
            getWorld().getLayout().setExit(exit);

            object.getPlayerList().forEach(player -> {
                Player p = new Player(GameContext.this, 1);
                p.getAsSerializablePlayer().deserializeFromProto(player);
                players.put(UUID.fromString(p.getId()), p);
            });

            //player.getAsSerializablePlayer().deserializeFromProto(object.getPlayer(0)); // Load player
            object.getItemsList().forEach(item -> {
                Item it = null;
                switch (GameObjectType.valueOf(item.getItemType())) {
                    case RINGITEM:
                        it = new RingItem(GameContext.this, null, null);
                        break;
                    case HOODITEM:
                        it = new HoodItem(GameContext.this, null, null);
                        break;
                }
                if (it != null) {
                    it.getAsSerializableItem().deserializeFromProto(item);
                }
            }); // Load items
            object.getMobsList().forEach(hostile -> {
                Hostile mob = null;
                switch (GameObjectType.valueOf(hostile.getHostileType())) {
                    case HOSTILE_COWARD:
                        mob = new CowardMob(GameContext.this);
                        break;
                    case HOSTILE_PASS:
                        mob = new PassiveMob(GameContext.this);
                        break;
                    case HOSTILE_AGR:
                        mob = new AggressiveMob(GameContext.this);
                        break;
                }
                if (mob != null) {
                    mob.getAsSerializableHostile().deserializeFromProto(hostile);
                }
            }); // Load mobs
        }
    }
}
