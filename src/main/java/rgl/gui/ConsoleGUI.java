package rgl.gui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.jetbrains.annotations.NotNull;
import rgl.commands.Command;
import rgl.gameobjects.GameObjectType;
import rgl.gameobjects.items.Item;
import rgl.logic.GameContext;
import rgl.util.Property;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of GUI for console interface.
 */
public class ConsoleGUI extends GUI {

    // Border margins.
    private static final int xborder = 15;
    private static final int yborder = 15;
    private static final int xdelta = 7;
    private static final int ydelta = 2;

    // Default background color.
    private static final TextColor backColor = TextColor.ANSI.BLACK;
    // Default foreground color.
    private static final TextColor foreColor = TextColor.ANSI.WHITE;

    private GameContext context;
    private UUID playerID;

    private Terminal terminal;
    private TextGraphics textGraphics;
    private Point dimesions;
    private DisplayConfig<TextCharacter> display = ConsoleDisplayConfig.getConfig();
    private String previousKey = "";
    private Command save, exit;

    public ConsoleGUI(@NotNull GameContext context, @NotNull UUID playerID, @NotNull Command save, @NotNull Command exit) throws IOException {
        this.context = context;
        this.playerID = playerID;
        this.save = save;
        this.exit = exit;

        dimesions = context.getWorld().getDimensions();

        DefaultTerminalFactory defaultTerminal = new DefaultTerminalFactory();
        defaultTerminal.setInitialTerminalSize(new TerminalSize(dimesions.x + xborder, dimesions.y + yborder));
        defaultTerminal.setTerminalEmulatorTitle("RGL: A New Hope");
        defaultTerminal.setForceTextTerminal(false);

        terminal = defaultTerminal.createTerminal();
        terminal.setCursorVisible(false);
        terminal.enterPrivateMode();

        textGraphics = terminal.newTextGraphics();
        resetColor();
    }

    @Override
    public boolean iteration() throws IOException {
        KeyStroke key = terminal.readInput();
        return handleKeyStroke(key);
    }

    @Override
    public void update() throws IOException {
        terminal.clearScreen();

        // Redraw the status bar.
        updateStatus(ydelta, 0, 50);
        // Redraw the inventory bar.
        updateInventory(ydelta + 50, 0, 50);
        // Redraw the map.
        updateMap();


        terminal.flush();
        // Reset all possible color changes in the terminal's global state.
        resetColor();
    }

    @Override
    public void close() throws IOException {
        terminal.close();
    }

    private void updateInventory(int x, int y, int len) {
        textGraphics.drawRectangle(
                new TerminalPosition(x, y), new TerminalSize(len, xdelta), '.');

        x += 1;
        for (Item i : context.getPlayers().get(playerID).getInventory()) {
            y += 1;
            textGraphics.putString(x, y, "> " + i.desciption());
        }
    }

    private void updateStatus(int x, int y, int len) {
        textGraphics.drawRectangle(
                new TerminalPosition(x, y), new TerminalSize(len, xdelta), '.');

        x += 1;
        y += 1;
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(x, y, "Control: w/a/s/d. Quit: q / <esc>. Drop: e.");
        textGraphics.putString(x, y + 1, "Save (only for SinglePlayer): b");
        updatePlayerStatus(x, y + 2);
        textGraphics.putString(x, y + 3, "Press " + previousKey);
    }

    private void updatePlayerStatus(int x, int y) {
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        List<Property> ps = context.getPlayers().get(playerID).getStatus();
        String status = ps.stream().map(Property::show).collect(Collectors.joining(" "));
        textGraphics.putString(x, y, status);
    }

    private void updateMap() {
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        for (int i = 0; i < dimesions.x; i++) {
            for (int j = 0; j < dimesions.y; j++) {
                GameObjectType type = context.getWorld().cellDisplay(i, j);
                TextCharacter go = display.get(type);
                textGraphics.setCharacter(i + ydelta, j + xdelta, go);
            }
        }
    }

    private void setForeColor(TextColor color) {
        textGraphics.setForegroundColor(color);
    }

    private void setBackColor(TextColor color) {
        textGraphics.setBackgroundColor(color);
    }

    private void resetColor() {
        setBackColor(backColor);
        setForeColor(foreColor);
    }

    private boolean handleKeyStroke(KeyStroke key) throws IOException {
        KeyType keyType = key.getKeyType();
        if (keyType == KeyType.Escape) {
            previousKey = "<enter>";
            terminal.exitPrivateMode();
            terminal.close();
            exit.execute();
        } else if (keyType == KeyType.Character) {
            Character c = key.getCharacter();
            previousKey = "<" + c + ">";
            if (c == 'q') {
                terminal.exitPrivateMode();
                terminal.close();
                exit.execute();
            } else if (c == 'w') {
                onPlayerAction(PlayerControl.Control.UP);
            } else if (c == 's') {
                onPlayerAction(PlayerControl.Control.DOWN);
            } else if (c == 'a') {
                onPlayerAction(PlayerControl.Control.LEFT);
            } else if (c == 'd') {
                onPlayerAction(PlayerControl.Control.RIGHT);
            } else if (c == 'e') {
                onPlayerAction(PlayerControl.Control.DROP);
            } else if (c == 'b') {
                save.execute();
                return false;
            } else {
                onPlayerAction(PlayerControl.Control.SKIP);
            }
            return true;
        }
        previousKey = "<other>";
        return false;
    }

    /**
     * Exact display configuration for console.
     * <p>
     * Include displayed characters and them colors.
     */
    static class ConsoleDisplayConfig {
        static DisplayConfig<TextCharacter> config = new DisplayConfig<>();

        static {
            config.set(GameObjectType.EXIT,
                    new TextCharacter(DisplayCharacter.EXIT, TextColor.ANSI.YELLOW, backColor));
            config.set(GameObjectType.FLOOR1,
                    new TextCharacter(DisplayCharacter.FLOOR1, TextColor.ANSI.WHITE, backColor));
            config.set(GameObjectType.FLOOR2,
                    new TextCharacter(DisplayCharacter.FLOOR2, TextColor.ANSI.YELLOW, backColor));
            config.set(GameObjectType.FLOOR3,
                    new TextCharacter(DisplayCharacter.FLOOR3, TextColor.ANSI.GREEN, backColor));
            config.set(GameObjectType.WALL,
                    new TextCharacter(DisplayCharacter.WALL, TextColor.ANSI.GREEN, backColor, SGR.ITALIC));
            config.set(GameObjectType.PLAYER,
                    new TextCharacter(DisplayCharacter.PLAYER, TextColor.ANSI.CYAN, backColor, SGR.BOLD));
            config.set(GameObjectType.HOSTILE_AGR,
                    new TextCharacter(DisplayCharacter.HOSTILE_AGR, TextColor.ANSI.RED, backColor, SGR.BOLD, SGR.FRAKTUR));
            config.set(GameObjectType.HOSTILE_PASS,
                    new TextCharacter(DisplayCharacter.HOSTILE_PASS, TextColor.ANSI.MAGENTA, backColor, SGR.BOLD, SGR.ITALIC));
            config.set(GameObjectType.HOSTILE_COWARD,
                    new TextCharacter(DisplayCharacter.HOSTILE_COWARD, TextColor.ANSI.GREEN, backColor, SGR.BOLD, SGR.ITALIC));
            config.set(GameObjectType.RINGITEM,
                    new TextCharacter(DisplayCharacter.RINGITEM, TextColor.ANSI.YELLOW, backColor, SGR.BOLD, SGR.ITALIC, SGR.BLINK));
            config.set(GameObjectType.HOODITEM,
                    new TextCharacter(DisplayCharacter.HOODITEM, TextColor.ANSI.WHITE, backColor, SGR.BOLD, SGR.ITALIC, SGR.BLINK));
        }

        static DisplayConfig<TextCharacter> getConfig() {
            return config;
        }

        // List of diplay symbols for object types.
        static final class DisplayCharacter {
            static final Character EXIT = '>';
            static final Character FLOOR1 = ' ';
            static final Character FLOOR2 = '.';
            static final Character FLOOR3 = ',';
            static final Character WALL = '*';
            static final Character PLAYER = '@';
            static final Character HOSTILE_AGR = 'a';
            static final Character HOSTILE_PASS = 'p';
            static final Character HOSTILE_COWARD = 'c';
            static final Character HOODITEM = '^';
            static final Character RINGITEM = 'o';

        }
    }

}
