package game_objects.mobs;

import game_objects.Dummy;
import logic.GameContext;
import logic.GameLoop;
import logic.behavior.BehaviorStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class Hostile extends Dummy implements GameLoop.IterationListener {

    protected BehaviorStrategy strategy;
    protected int radius;

    public Hostile(@NotNull GameContext context, int lvl, BehaviorStrategy strategy) {
        super(context, lvl);
        radius = new Random().nextInt(lvl) + 5;
        this.strategy = strategy;

        context.addIterationListener(this);
    }

    @Override
    public void iterate(@NotNull GameContext context) {
        // if mob died -> delete it from gameloop
        if (health <= 0) {
            context.removeIterationListener(this);
        } else {
            attended = false; // prevent endless loop of beating each other
            moveOrAttack(strategy.step(context, getPosition(), radius));
        }
    }
}
