import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.apache.commons.cli.*;

import game_objects.Player;
import map.WorldMap;
import map.WorldMapLayout;
import map.terrain.TerrainMap;
import map.terrain.TerrainMapImpl;
import utils.PlayerControl;

import java.awt.*;
import java.io.IOException;

public class Main {
    static class MainLoop {
		private final int mapWidth = 10;
		private final int mapHeight = 10;

        private final TerrainMap terrain;
        private final Player player;
        private final WorldMapLayout layout;
        private final WorldMap world;

        MainLoop() throws IOException {
            terrain = new TerrainMapImpl(mapWidth, mapHeight);
            player = new Player();
            layout = new WorldMapLayout(terrain, player);
            world = new WorldMap(layout, player);
        }

        MainLoop(String name) throws IOException {
            terrain = new TerrainMapImpl(name, mapWidth, mapHeight);
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

        void run() throws IOException {
            DefaultTerminalFactory defaultTerminal = new DefaultTerminalFactory();

            Point p = getDims();
            int xborder = 20;
            int yborder = 20;
            int xdelta = 10;
            int ydelta = 10;

            defaultTerminal.setInitialTerminalSize(new TerminalSize(p.x + xborder, p.y + yborder));

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
                        char c = displayCell(new Point(j, i));
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
                    step(PlayerControl.Control.UP);
                } else if (c == 's') {
                    step(PlayerControl.Control.DOWN);
                } else if (c == 'a') {
                    step(PlayerControl.Control.LEFT);
                } else if (c == 'd') {
                    step(PlayerControl.Control.RIGHT);
                } else {
                    step(PlayerControl.Control.SKIP);
                }
            }
        }
    }


	static class CMD {
    	final static String pathOption = "f";
    	final static String generatOption = "g";

		static Options getOptions() {
    		Options options = new Options();
            options.addOption(pathOption, true, "Path to the user map.");
            options.addOption(generatOption, "Generate a random map.");
        	return options;
		}

    	static void usage(Options options) {
    		HelpFormatter fmt = new HelpFormatter();
    		fmt.printHelp("RGL", options);
    	}
	}

    public static void main(String[] args) throws IOException {
        Options options = CMD.getOptions();

		CommandLine cmd = null;
		try {
    		cmd = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			System.err.println("error: unable to parse command line parameters.");
			CMD.usage(options);
			return;
		}

        MainLoop ml = null;
		if (cmd.hasOption(CMD.generatOption)) {
    		try {
    			ml = new MainLoop();
    		} catch (IOException e) {
				System.err.println("error: " + e.getMessage());
    		}
		} else if (cmd.hasOption(CMD.pathOption)) {
            String str = (String)cmd.getOptionObject(CMD.pathOption.charAt(0));
    		try {
    			ml = new MainLoop(str);
    		} catch (IOException e) {
				System.err.println("error: " + e.getMessage());
    		}
		} else {
			System.err.println("error: no required options.");
			CMD.usage(options);
			return;
		}
        ml.run();
    }
}
