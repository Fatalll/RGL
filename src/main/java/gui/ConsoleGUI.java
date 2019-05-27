package gui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import game_objects.GameObjectType;
import logic.GameContext;
import org.jetbrains.annotations.NotNull;
import util.Property;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleGUI extends GUI {

    private static final int xborder = 5;
    private static final int yborder = 5;
    private static final int xdelta = 5;
    private static final int ydelta = 5;
    private static final TextColor backColor = TextColor.ANSI.BLACK;
    private static final TextColor foreColor = TextColor.ANSI.WHITE;

    private Terminal terminal;
    private TextGraphics textGraphics;
    private Point dimesions;
    private DisplayConfig<Character> display = ConsoleDisplayConfig.getConfig();

    public ConsoleGUI(@NotNull GameContext context) throws IOException {
        super(context);

        dimesions = context.getWorld().getDimensions();

        DefaultTerminalFactory defaultTerminal = new DefaultTerminalFactory();
        defaultTerminal.setInitialTerminalSize(new TerminalSize(dimesions.x + xborder, dimesions.y + yborder));
        defaultTerminal.setTerminalEmulatorTitle("RGL: A New Hope");

        terminal = defaultTerminal.createTerminal();
        terminal.setCursorVisible(false);
        terminal.enterPrivateMode();

        textGraphics = terminal.newTextGraphics();
        resetColor();
    }

    @Override
    public boolean iteration() throws IOException {
        terminal.clearScreen();

        updateStatus();
        updateMap();

        terminal.flush();
        resetColor();
        KeyStroke key = terminal.readInput();
        return handleKeyStroke(key);
    }


    @Override
    public void reload() {
        dimesions = context.getWorld().getDimensions();
    }

    @Override
    public void close() throws IOException {
        terminal.close();
    }

    private void updateStatus() {
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(0, 0, "Control: w/a/s/d. Quit: q / <esc>");
        updatePlayerStatus();
        updateGameStatus();
    }

    private void updateGameStatus() {
        String s = context.getGameStatus().getStatus();
        textGraphics.putString(0, 2, "Last action: " + (s == null ? " " : s));
    }

    private void updatePlayerStatus() {
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        List<Property> ps = context.getPlayer().getStatus();
        String status = ps.stream().map(Property::show).collect(Collectors.joining(" "));
        textGraphics.putString(0, 1, status);
    }

    private void updateMap() {
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        for (int i = 0; i < dimesions.x; i++) {
            for (int j = 0; j < dimesions.y; j++) {
                GameObjectType type = context.getWorld().cellDisplay(j, i);
                DisplayGameObject go = display.get(type);
                setForeColor(go.getForeColor());
                setBackColor(go.getBackColor());

                char c = (Character) go.getDisplay();
                textGraphics.putString(i + ydelta, j + xdelta, c + " ");
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
            terminal.exitPrivateMode();
            onPlayerAction(PlayerControl.Control.EXIT);
        } else if (keyType == KeyType.Character) {
            Character c = key.getCharacter();
            if (c == 'q') {
                terminal.exitPrivateMode();
                onPlayerAction(PlayerControl.Control.EXIT);
            } else if (c == 'w') {
                onPlayerAction(PlayerControl.Control.UP);
                return true;
            } else if (c == 's') {
                onPlayerAction(PlayerControl.Control.DOWN);
                return true;
            } else if (c == 'a') {
                onPlayerAction(PlayerControl.Control.LEFT);
                return true;
            } else if (c == 'd') {
                onPlayerAction(PlayerControl.Control.RIGHT);
                return true;
            } else {
                onPlayerAction(PlayerControl.Control.SKIP);
                return true;
            }
        }
        return false;
    }

    static class ConsoleDisplayConfig {

        static class DisplayCharacter {
            static final Character EXIT = '>';
            static final Character FLOOR1 = ' ';
            static final Character FLOOR2 = '.';
            static final Character FLOOR3 = ',';
            static final Character WALL = '*';
            static final Character PLAYER = '@';
            static final Character HOSTILE_AGR = 'a';
            static final Character HOSTILE_PASS = 'p';
            static final Character HOSTILE_COWARD = 'c';

        }

        static DisplayConfig<Character> getConfig() {
            DisplayConfig<Character> display = new DisplayConfig<>();
            display.set(GameObjectType.EXIT,
                    new DisplayGameObject<>(DisplayCharacter.EXIT, TextColor.ANSI.YELLOW));
            display.set(GameObjectType.FLOOR1,
                    new DisplayGameObject<>(DisplayCharacter.FLOOR1, TextColor.ANSI.WHITE));
            display.set(GameObjectType.FLOOR2,
                    new DisplayGameObject<>(DisplayCharacter.FLOOR2, TextColor.ANSI.YELLOW));
            display.set(GameObjectType.FLOOR3,
                    new DisplayGameObject<>(DisplayCharacter.FLOOR3, TextColor.ANSI.GREEN));
            display.set(GameObjectType.WALL,
                    new DisplayGameObject<>(DisplayCharacter.WALL, TextColor.ANSI.GREEN));
            display.set(GameObjectType.PLAYER,
                    new DisplayGameObject<>(DisplayCharacter.PLAYER, TextColor.ANSI.CYAN));
            display.set(GameObjectType.HOSTILE_AGR,
                    new DisplayGameObject<>(DisplayCharacter.HOSTILE_AGR, TextColor.ANSI.RED));
            display.set(GameObjectType.HOSTILE_PASS,
                    new DisplayGameObject<>(DisplayCharacter.HOSTILE_PASS, TextColor.ANSI.MAGENTA));
            display.set(GameObjectType.HOSTILE_COWARD,
                    new DisplayGameObject<>(DisplayCharacter.HOSTILE_COWARD, TextColor.ANSI.GREEN));
            return display;
        }
    }

}
