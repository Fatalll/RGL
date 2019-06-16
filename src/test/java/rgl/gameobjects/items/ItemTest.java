package rgl.gameobjects.items;

import org.junit.Test;
import rgl.generators.ItemGenerator;

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