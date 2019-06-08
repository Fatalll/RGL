package map.terrain.cells;

import game_objects.GameObject;
import game_objects.GameObjectType;
import org.jetbrains.annotations.NotNull;
import protobuf.GameObjectsProto;
import protobuf.Serializable;

import java.awt.*;

public class Wall extends Cell<GameObjectType> {

    private GameObjectType wall;

    public Wall(Point position) {
        super(position);
        wall = GameObjectType.WALL;
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
