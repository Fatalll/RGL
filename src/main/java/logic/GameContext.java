package logic;

import game_objects.GameObjectType;
import game_objects.Player;
import game_objects.items.HoodItem;
import game_objects.items.Item;
import game_objects.items.RingItem;
import game_objects.mobs.AggressiveMob;
import game_objects.mobs.CowardMob;
import game_objects.mobs.Hostile;
import game_objects.mobs.PassiveMob;
import gui.GUI;
import map.WorldMap;
import map.WorldMapLayout;
import map.terrain.TerrainMap;
import map.terrain.cells.Cell;
import map.terrain.cells.Exit;
import map.terrain.cells.Floor;
import map.terrain.cells.Wall;
import org.jetbrains.annotations.NotNull;
import protobuf.GameObjectsProto;
import protobuf.Serializable;

import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameContext {
    private Player player;
    private WorldMap world;
    private GUI gui;
    private GameStatus status;

    // listeners called on each game tick
    private Set<GameLoop.IterationListener> listeners;

    public GameContext(@NotNull TerrainMap initialMap, @NotNull Set<GameLoop.IterationListener> listeners) {
        this.listeners = listeners;

        player = new ConfusionPlayer(this, 1);
        world = new WorldMap(new WorldMapLayout(initialMap, this), this);
        status = new GameStatus();
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public WorldMap getWorld() {
        return world;
    }

    @NotNull
    public GUI getGui() {
        return gui;
    }

    public void setGui(@NotNull GUI gui) {
        this.gui = gui;
    }

    public void addIterationListener(@NotNull GameLoop.IterationListener listener) {
        listeners.add(listener);
    }

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
                    .setPlayer(getPlayer().getAsSerializablePlayer().serializeToProto()) // Save player
                    .addAllItems(itemStream
                            .map(item -> item.getAsSerializableItem().serializeToProto())
                            .collect(Collectors.toList())) // Save all items on the map
                    .addAllMobs(hostileStream
                            .map(mob -> mob.getAsSerializableHostile().serializeToProto()) // Save all mobs on the map
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
            getWorld().getLayout().setWorld(world);
            getWorld().getLayout().setEntry(entry);
            getWorld().getLayout().setExit(exit);

            player.getAsSerializablePlayer().deserializeFromProto(object.getPlayer()); // Load player
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
