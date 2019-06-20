package ru.ifmo.rgl.gameobjects.items;

import org.junit.Test;
import ru.ifmo.rgl.generators.ItemGenerator;

import static org.junit.Assert.assertTrue;

public class ItemTest {

    // Shall not fall.
    // Better way to test it is to print in into stdio!
    @Test
    public void generatePropreties() {
        ItemGenerator.generatePropreties(2, -10, 20);
    }

    // Shall not fall.
    // Better way to test it is to print in into stdio!
    @Test
    public void generateNames() {
        String str1 = ItemGenerator.generateName("Ring of");
        String str2 = ItemGenerator.generateName(null);
        assertTrue(str1 != null);
        assertTrue(str2 != null);
    }
}
