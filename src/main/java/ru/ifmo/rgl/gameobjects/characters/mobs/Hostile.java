package ru.ifmo.rgl.gameobjects.characters.mobs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.rgl.gameobjects.characters.Dummy;
import ru.ifmo.rgl.gameobjects.characters.mobs.behavior.BehaviorStrategy;
import ru.ifmo.rgl.gameobjects.characters.stats.StatType;
import ru.ifmo.rgl.logic.GameContext;
import ru.ifmo.rgl.logic.GameLoop;
import ru.ifmo.rgl.proto.GameObjectsProto;
import ru.ifmo.rgl.util.Serializable;

import java.util.Random;

/**
 * abstract class for a hostile
 */
public abstract class Hostile extends Dummy implements GameLoop.IterationListener {

    private BehaviorStrategy strategy;
    private int radius;

    public Hostile(@NotNull GameContext context, int lvl, BehaviorStrategy strategy) {
        super(context, lvl);
        radius = new Random().nextInt(lvl) + 5;
        this.strategy = strategy;

        context.addIterationListener(this);
    }

    /**
     * called each game loop iteration
     *
     * @param context game context
     */
    @Override
    public void iterate(@NotNull GameContext context) {
        // if mob died -> delete it from gameloop
        if (getStat(StatType.HEALTH).get() <= 0) {
            context.removeIterationListener(this);
        } else {
            attend(); // prevent endless loop of beating each other
            nextMove(strategy.step(context, getPosition(), radius));
        }
    }

    public final Serializable<GameObjectsProto.Hostile> getAsSerializableHostile() {
        return new SerializableHostileImpl();
    }

    /**
     * prepare hostile for serializing
     */
    private class SerializableHostileImpl implements Serializable<GameObjectsProto.Hostile> {

        @Override
        public GameObjectsProto.Hostile serializeToProto() {
            return GameObjectsProto.Hostile.newBuilder()
                    .setDummy(getAsSerializableDummy().serializeToProto())
                    .setRadius(radius)
                    .setHostileType(display().toString())
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.Hostile object) {
            getAsSerializableDummy().deserializeFromProto(object.getDummy());
            radius = object.getRadius();
        }
    }
}
