package map.terrain.cells;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Exit extends Floor {
    public Exit(Point position) {
        super(position);
    }

    @Override
    public @NotNull Character display() {
        Character label = super.display();
        return label == ' ' ? '$' : label;
    }
}
