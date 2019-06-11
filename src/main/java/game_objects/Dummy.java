package game_objects;

import logic.ConfusionPlayer;
import logic.GameContext;
import org.jetbrains.annotations.NotNull;
import protobuf.GameObjectsProto;
import protobuf.Serializable;
import util.Property;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public abstract class Dummy extends GameObject<GameObjectType> {

    protected Map<StatType, Stat> stats;

    protected boolean attended;

    protected int lvl;

    public Dummy(@NotNull GameContext context, int lvl) {
        super(context);
        this.lvl = lvl;

        Random random = new Random();
        stats = new HashMap<>();
        stats.put(StatType.HEALTH, new CharacterStat(10 + random.nextInt(lvl * 5)));
        stats.put(StatType.ARMOR, new CharacterStat(1 + random.nextInt(lvl * 5)));
        stats.put(StatType.ATTACK, new CharacterStat(1 + random.nextInt(lvl * 5)));
    }

    private int calcAttack(int attack, int armor) {
        return Math.max(attack - armor, 1);
    }

    public int statsSize() {
        return stats.size();
    }

    public Stat getStat(StatType type) {
        return stats.get(type);
    }


    public void nextMove(@NotNull Point position) {
        // check if we really can pass to this position
        if (context.getWorld().isPassable(position)) {
            moveToCell(context.getWorld().getCell(position));
        } else if (!attended) {
            // if we can't pass, probably there is mob, so check it
            attack(position);
        }
    }

    public void attack(@NotNull Point position) {
        GameObject<?> object = context.getWorld().getCell(position).getGameObject();

        if (object instanceof Dummy && object != this) {
            Dummy dummy = (Dummy) object;

            Stat dummy_health = dummy.getStat(StatType.HEALTH);
            int dummy_attack = dummy.getStat(StatType.ATTACK).get();
            int dummy_armor = dummy.getStat(StatType.ARMOR).get();

            Stat health = getStat(StatType.HEALTH);
            int attack = getStat(StatType.ATTACK).get();
            int armor = getStat(StatType.ARMOR).get();
            
            health.set(health.get() - calcAttack(dummy_attack, armor));
            dummy_health.set(dummy_health.get() - calcAttack(attack, dummy_armor));

            // cast confusion
            if (this instanceof ConfusionPlayer) {
                ((ConfusionPlayer) this).confuse(5);
            }

            // if we died, delete self from cell
            if (health.get() <= 0) {
                context.updateGameStatus("Killed!");
                cell.clearGameObject();
            }

            if (dummy_health.get() <= 0) {
                object.cell.clearGameObject();
                // if current object is Player, than add some exp
                if (this instanceof Player) {
                    ((Player) this).exp += dummy.lvl;
                }
            }

            dummy.attended = true;
            attended = true;

            stats.replace(StatType.HEALTH, health);
            dummy.stats.replace(StatType.HEALTH, dummy_health);
        }
    }

    public int getLvl() {
        return lvl;
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
            moveToCell(context.getWorld().getCell(position));
        }
    }
}

