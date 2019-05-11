package game_objects;

import logic.ConfusionStatus;
import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public abstract class Dummy extends GameObject<Character> {

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
            GameObject<Character> object = context.getWorld().getCell(position).getGameObject();

            if (object instanceof Dummy && object != this) {
                health -= Math.max(((Dummy) object).attack - armor, 1);
                ((Dummy) object).health -= Math.max(attack - ((Dummy) object).armor, 1);

                System.out.println("Health: " + health);
                System.out.println("Mob: " + ((Dummy) object).health);

                if (this instanceof Player) {
                    if (Math.random() < 0.15) {
                        new ConfusionStatus(5, context);
                    }
                }

                if (health <= 0) {
                    cell.clearGameObject();
                }

                if (((Dummy) object).health <= 0) {
                    object.cell.clearGameObject();

                    if (this instanceof Player) {
                        ((Player) this).exp += ((Dummy) object).lvl;
                    }
                }

                ((Dummy) object).attended = true;
                attended = true;
            }
        }
    }

    public int getLvl() {
        return lvl;
    }
}
