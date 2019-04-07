import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import game_objects.Player;
import map.WorldMap;
import map.WorldMapLayout;
import map.terrain.TerrainMap;
import map.terrain.TerrainMapImpl;
import utils.PlayerControl;

import java.awt.*;
import java.io.IOException;

import static map.terrain.TerrainMap.TerrainCellType.VOID;
import static map.terrain.TerrainMap.TerrainCellType.WALL;

class MainLoop {

    TerrainMap.TerrainCellType[][] map = {
            {WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL},
            {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
            {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
            {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
            {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
            {VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID},
            {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
            {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
            {WALL, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, WALL},
            {WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL}
    };

    TerrainMap terrain;
    Player player;
    WorldMapLayout layout;
    WorldMap world;

	MainLoop() {
    	try {
            terrain = new TerrainMapImpl("/test.map");
    	} catch (Exception e) {
        	e.printStackTrace();
    	}

        player = new Player();
        layout = new WorldMapLayout(terrain, player);
        world = new WorldMap(layout, player);
	}

	Point getDims() {
	    return terrain.getDimensions();
    }

	char displayCell(Point p) {
        return layout.displayCell(p);
	}

	void step(PlayerControl.Control c) {
        world.step(c);
	}
}

public class Main {
    public static void main(String[] args) throws IOException {
        MainLoop ml = new MainLoop();
        DefaultTerminalFactory defaultTerminal = new DefaultTerminalFactory();
        // Doesn't work!
        //defaultTerminal.addTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode);

        Point p = ml.getDims();
        int xborder = 20;
        int yborder = 20;
        int xdelta = 10;
        int ydelta = 10;

        defaultTerminal.setInitialTerminalSize(new TerminalSize(p.x + xborder, p.y + yborder));
        // Works strangely.
        //defaultTerminal.setForceTextTerminal(true);
        defaultTerminal.setTerminalEmulatorTitle("RGL: A New Hope");
        Terminal term = defaultTerminal.createTerminal();
        term.setCursorVisible(false);
        term.enterPrivateMode();

        TextGraphics textGraphics = term.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        while (true) {
            term.clearScreen();
            textGraphics.putString(0, 0, "Control: wasd.");
            textGraphics.putString(0, 1, "Quit: q / <esc>.");
            for (int i = 0; i < p.x; i++) {
                for (int j = 0; j < p.y; j++) {
                    char c = ml.displayCell(new Point(j, i));
                    textGraphics.putString(i + ydelta, j + xdelta, c + " ");
                }
            }
            term.flush();

            KeyStroke key = term.readInput();
            KeyType keyType = key.getKeyType();
            if (keyType == KeyType.Escape) {
                term.exitPrivateMode();
                break;
            } else if (keyType != KeyType.Character) {
                continue;
            }

            Character c = key.getCharacter();
            if (c == 'q') {
                term.exitPrivateMode();
                break;
            } else if (c == 'w') {
                ml.step(PlayerControl.Control.UP);
            } else if (c == 's') {
                ml.step(PlayerControl.Control.DOWN);
            } else if (c == 'a') {
                ml.step(PlayerControl.Control.LEFT);
            } else if (c == 'd') {
                ml.step(PlayerControl.Control.RIGHT);
            } else {
                ml.step(PlayerControl.Control.SKIP);
            }

        }
    }
}