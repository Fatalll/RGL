package map.terrain.cells;

import game_objects.GameObject;
import game_objects.GameObjectType;
import org.jetbrains.annotations.NotNull;
import protobuf.GameObjectsProto;
import protobuf.Serializable;

import java.awt.*;

public class Floor extends Cell<GameObjectType> {
    private GameObject<GameObjectType> gameObject;
    private GameObjectType floor = chooseFloor();

    public Floor(Point position) {
        super(position);
    }

    @Override
    public GameObject<GameObjectType> getGameObject() {
        return gameObject;
    }

    @Override
    public boolean canSetGameObject() {
        return gameObject == null;
    }

    @Override
    public void clearGameObject() {
        gameObject = null;
    }

    @Override
    public void setGameObjectToCell(@NotNull GameObject<GameObjectType> gameObject) {
        if (this.gameObject == null) {
            this.gameObject = gameObject;
        } else {
            throw new IllegalStateException("The cell already contains an object!");
        }
    }

    @NotNull
    @Override
    public GameObjectType display() {
        return (gameObject == null) ?
                floor :
                gameObject.display();
    }

    private GameObjectType chooseFloor() {
        double a = Math.random();
        if (a > 0.4) {
            return GameObjectType.FLOOR1;
        } else if (a > 0.1) {
            return GameObjectType.FLOOR2;
        }
        return GameObjectType.FLOOR3;
    }

    public final Serializable<GameObjectsProto.FloorCell> getAsSerializableFloor() {
        return new SerializableFloorImpl();
    }

    private class SerializableFloorImpl implements Serializable<GameObjectsProto.FloorCell> {

        @Override
        public GameObjectsProto.FloorCell serializeToProto() {
            return GameObjectsProto.FloorCell.newBuilder()
                    .setPosition(GameObjectsProto.Position.newBuilder().setX(getPosition().x).setY(getPosition().y).build())
                    .setObjectType(floor.toString())
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.FloorCell object) {
            floor = GameObjectType.valueOf(object.getObjectType());
            position = new Point(object.getPosition().getX(), object.getPosition().getY());
        }
    }
}
