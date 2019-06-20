package ru.ifmo.rgl.map.terrain.cells;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.GameObject;
import ru.ifmo.rgl.gameobjects.GameObjectType;
import ru.ifmo.rgl.proto.GameObjectsProto;
import ru.ifmo.rgl.util.Serializable;

import java.awt.*;

/**
 * wall cell aka where game objects con't be placed
 */
public class Wall implements Cell<GameObjectType> {

    private Point position;
    private GameObjectType wall;

    public Wall(Point position) {
        this.position = position;
        wall = GameObjectType.WALL;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public GameObject<GameObjectType> getGameObject() {
        return null;
    }

    @Override
    public boolean canSetGameObject() {
        return false;
    }

    @Override
    public void clearGameObject() {
    }

    @Override
    public void setGameObjectToCell(@NotNull GameObject<GameObjectType> gameObject) {
        throw new IllegalArgumentException();
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return wall;
    }

    final public Serializable<GameObjectsProto.WallCell> getAsSerializableWall() {
        return new SerializableWallImpl();
    }

    private class SerializableWallImpl implements Serializable<GameObjectsProto.WallCell> {

        @Override
        public GameObjectsProto.WallCell serializeToProto() {
            return GameObjectsProto.WallCell.newBuilder()
                    .setPosition(GameObjectsProto.Position.newBuilder().setX(getPosition().x).setY(getPosition().y).build())
                    .setObjectType(wall.toString())
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.WallCell object) {
            wall = GameObjectType.valueOf(object.getObjectType());
            position = new Point(object.getPosition().getX(), object.getPosition().getY());
        }
    }
}
