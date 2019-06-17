package rgl.generators;

import rgl.gameobjects.characters.stats.StatType;

import java.util.*;

/**
 * Generate item properties and item names.
 * Object `Item` instanties outside.
 */
public final class ItemGenerator {
    private static final String[] actors = {"Warrior", "Farmer", "Warlock", "Jedi", "Druid", "Puppy", "Baby", "Miner",
                                           "Bookbinder", "Fisher", "Dwarf"};
    private static final String[] descrs = {"Proud", "Fat", "Bloody", "Dark", "Stinking", "Plain", "Mighty"};

	/**
	 * Generate random properies for an item.
	 *
	 * @param n Number of properties.
	 * @param min Minimal property value.
	 * @param max Maximum property value.
	 */
    public static Map<StatType, Integer> generatePropreties(int n, int min, int max) {
        ArrayList<StatType> stypes = new ArrayList<>(Arrays.asList(StatType.values()));
        Map<StatType, Integer> prop = new HashMap<>();
        Random rand = new Random();
        for (int i = 0; i < n && i < stypes.size(); i++) {
            int j = rand.nextInt(stypes.size());
            StatType s = stypes.get(j);
            stypes.remove(j);

            OptionalInt first = rand.ints(min, max).findFirst();
            if (first.isPresent()) {
                prop.put(s, first.getAsInt());
            } else {
                i -= 1;
            }
        }
        return prop;
    }

	/**
	 * Generate random name from `prefix` and builtin list of words.
	 *
	 * @param prefix Prefix of the name.
	 */
    public static String generateName(String prefix) {
        Random rand = new Random();
        String name = ItemGenerator.descrs[rand.nextInt(descrs.length)]
                      + " " + ItemGenerator.actors[rand.nextInt(actors.length)];
        if (prefix == null || prefix.isEmpty()) {
			return  "The " + name;
        }
		return prefix + " the " + name;
    }
}
