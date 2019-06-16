package rgl.gameobjects.items;

import org.junit.Test;
import rgl.generators.ItemGenerator;

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
        ItemGenerator.generateName("Ring of");
        ItemGenerator.generateName("Ring of");
        ItemGenerator.generateName("Ring of");
        ItemGenerator.generateName("Ring of");
        ItemGenerator.generateName("Ring of");
        ItemGenerator.generateName("Ring of");
    }
}
