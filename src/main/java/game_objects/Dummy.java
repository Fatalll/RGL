package game_objects;

import logic.ConfusionPlayer;
import logic.GameContext;
import org.jetbrains.annotations.NotNull;
import util.Property;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Dummy extends GameObject<GameObjectType> {

    protected int health;
    protected int armor;
    protected int attack;

    protected boolean attended; // чтобы не бить дважды

    protected int lvl;

    public Dummy(@NotNull GameContext context, int lvl) {
        super(context);
        this.lvl = lvl;

        Random random = new Random();
        health = 10 + random.nextInt(lvl * 5);
        armor = 1 + random.nextInt(lvl * 5);
        attack = 1 + random.nextInt(lvl * 5);
    }

    public void moveOrAttack(@NotNull Point position) {
        if (context.getWorld().isPassable(position)) {
            moveToCell(context.getWorld().getCell(position));
        } else if (!attended) {
            GameObject<?> object = context.getWorld().getCell(position).getGameObject();

            if (object instanceof Dummy && object != this) {
                Dummy dummy = (Dummy) object;

                health -= Math.max(dummy.attack - armor, 1);
                dummy.health -= Math.max(attack - dummy.armor, 1);

                context.updateGameStatus(" Hostile HP " + dummy.health);

                // cast confusion
                if (this instanceof ConfusionPlayer) {
                    ((ConfusionPlayer) this).confuse(5);
                }

                if (health <= 0) {
                    cell.clearGameObject();
                }

                if (dummy.health <= 0) {
                    object.cell.clearGameObject();

                    if (this instanceof Player) {
                        ((Player) this).exp += dummy.lvl;
                    }
                }

                dummy.attended = true;
                attended = true;
            }
        }
    }

    public int getLvl() {
        return lvl;
    }

    public int getHealth() {
        return health;
    }

    public List<Property> getStatus() {
        return Arrays.asList(
                () -> "Health: " + health,
                () -> "Attack: " + attack,
                () -> "Armor: " + armor
        );
    }
}

