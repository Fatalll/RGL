package rgl.gameobjects.characters;

import org.jetbrains.annotations.NotNull;
import rgl.gameobjects.GameObject;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.GameObjectsProto;
import rgl.gameobjects.characters.player.Confusion;
import rgl.gameobjects.characters.player.Player;
import rgl.gameobjects.characters.stats.CharacterStat;
import rgl.gameobjects.characters.stats.Stat;
import rgl.gameobjects.characters.stats.StatType;
import rgl.gui.GUI;
import rgl.logic.GameContext;
import rgl.util.Property;
import rgl.util.Serializable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static rgl.gameobjects.characters.player.Confusion.CONFUSION_CHANCE;

/**
 * default class for any character
 */
public abstract class Dummy extends GameObject<GameObjectType> {

    private Map<StatType, Stat> stats;
    private boolean attended;
    private int lvl;

    public Dummy(@NotNull GameContext context, int lvl) {
        super(context);
        this.lvl = lvl;

        Random random = new Random();
        stats = new HashMap<>();
        stats.put(StatType.HEALTH, new CharacterStat(10 + random.nextInt(lvl * 5)));
        stats.put(StatType.ARMOR, new CharacterStat(1 + random.nextInt(lvl * 5)));
        stats.put(StatType.ATTACK, new CharacterStat(1 + random.nextInt(lvl * 5)));
    }

    public boolean isAttended() {
        return attended;
    }

    protected void attend() {
        this.attended = false;
    }

    public int getLvl() {
        return lvl;
    }

    protected void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public Map<StatType, Stat> getStats() {
        return stats;
    }

    public void setStats(Map<StatType, Stat> stats) {
        this.stats = stats;
    }

    private int calcAttack(int attack, int armor) {
        return Math.max(attack - armor, 1);
    }

    public int statsSize() {
        return stats.size();
    }

    public Stat getStat(@NotNull StatType type) {
        return stats.get(type);
    }


    /**
     * move character to position
     *
     * @param position position
     */
    public void nextMove(@NotNull Point position) {
        // check if we really can pass to this position
        if (getContext().getWorld().isPassable(position)) {
            moveToCell(getContext().getWorld().getCell(position));
        } else if (!attended) {
            // if we can't pass, probably there is mob, so check it
            attack(position);
        }
    }

    private void attack(@NotNull Point position) {
        GameObject<?> object = getContext().getWorld().getCell(position).getGameObject();

        if (object instanceof Dummy && object != this) {
            Dummy dummy = (Dummy) object;

            Stat dummyHealth = dummy.getStat(StatType.HEALTH);
            int dummyAttack = dummy.getStat(StatType.ATTACK).get();
            int dummyArmor = dummy.getStat(StatType.ARMOR).get();

            Stat health = getStat(StatType.HEALTH);
            int attack = getStat(StatType.ATTACK).get();
            int armor = getStat(StatType.ARMOR).get();

            health.set(health.get() - calcAttack(dummyAttack, armor));
            dummyHealth.set(dummyHealth.get() - calcAttack(attack, dummyArmor));

            // cast confusion
            if (this instanceof Player) {
                if (Math.random() < CONFUSION_CHANCE) {
                    GUI gui = getContext().getGui();
                    // if confusion not already present
                    if (gui.removeActionListener((Player) this)) {
                        gui.addActionListener(new Confusion((Player) this, getContext().getGui(), 5));
                        getContext().updateGameStatus("Hostile casted 'Confusion'!");
                    }
                }
            }

            // if we died, delete self from cell
            if (health.get() <= 0) {
                getContext().updateGameStatus("Killed!");
                clearGameObject();
            }

            if (dummyHealth.get() <= 0) {
                object.clearGameObject();
                // if current object is Player, than add some exp
                if (this instanceof Player) {
                    ((Player) this).addExp(dummy.lvl);
                }
            }

            dummy.attended = true;
            attended = true;

            stats.replace(StatType.HEALTH, health);
            dummy.stats.replace(StatType.HEALTH, dummyHealth);
        }
    }

    public int getHealth() {
        return getStat(StatType.HEALTH).get();
    }

    public List<Property> getStatus() {
        return Arrays.asList(
                () -> "Health: " + getStat(StatType.HEALTH).get(),
                () -> "Attack: " + getStat(StatType.ATTACK).get(),
                () -> "Armor: " + getStat(StatType.ARMOR).get()
        );
    }

    final public Serializable<GameObjectsProto.Dummy> getAsSerializableDummy() {
        return new SerializableDummyImpl();
    }

    protected class SerializableDummyImpl implements Serializable<GameObjectsProto.Dummy> {
        @Override
        public GameObjectsProto.Dummy serializeToProto() {
            return GameObjectsProto.Dummy.newBuilder()
                    .setLevel(lvl)
                    .setPosition(GameObjectsProto.Position.newBuilder().setX(getPosition().x).setY(getPosition().y).build())
                    .addAllStats(stats.entrySet().stream().map(
                            statTypeStatEntry -> GameObjectsProto.StatEntry.newBuilder()
                                    .setStatType(statTypeStatEntry.getKey().toString())
                                    .setStatValue(statTypeStatEntry.getValue().get()).build()).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public void deserializeFromProto(GameObjectsProto.Dummy object) {
            lvl = object.getLevel();
            stats = object.getStatsList().stream()
                    .map(statEntry -> new AbstractMap.SimpleEntry<>(
                            StatType.valueOf(statEntry.getStatType()),
                            new Stat() {

                                int val = statEntry.getStatValue();

                                @Override
                                public int get() {
                                    return val;
                                }

                                @Override
                                public void set(int v) {
                                    val = v;
                                }
                            }
                    ))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
            Point position = new Point(object.getPosition().getX(), object.getPosition().getY());
            moveToCell(getContext().getWorld().getCell(position));
        }
    }
}

