package game_objects.items;

import generators.ItemGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ItemTest {

    @Test
    public void showProp() {
    }

    @Test
    public void generatePropreties() {
        System.out.println(ItemGenerator.generatePropreties(2, -10, 20));
    }

    @Test
    public void generateNames() {
        System.out.println(ItemGenerator.generateName("Ring of"));
        System.out.println(ItemGenerator.generateName("Ring of"));
        System.out.println(ItemGenerator.generateName("Ring of"));
        System.out.println(ItemGenerator.generateName("Ring of"));
        System.out.println(ItemGenerator.generateName("Ring of"));
        System.out.println(ItemGenerator.generateName("Ring of"));
    }
}