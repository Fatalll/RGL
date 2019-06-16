package rgl.map.terrain.cells;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObject;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.GameObjectsProto;
import rgl.util.Serializable;

import java.awt.*;

/**
 * floor cell aka where game objects can be placed
 */
public class Floor implements Cell<GameObjectType> {
    private Point position;
    private GameObject<GameObjectType> gameObject;
    private GameObjectType floor = chooseFloor();

    public Floor(Point position) {
        this.position = position;
    }

    @Override
    public Point getPosition() {
        return position;
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
