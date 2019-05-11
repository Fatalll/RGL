package gui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import logic.GameContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

public class ConsoleGUI extends GUI {

    private static final int xborder = 20;
    private static final int yborder = 20;
    private static final int xdelta = 10;
    private static final int ydelta = 10;

    private Terminal terminal;
    private TextGraphics textGraphics;
    private Point dimesions;

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
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
    }

    @Override
    public boolean iteration() throws IOException {
        terminal.clearScreen();
        textGraphics.putString(0, 0, "Control: wasd.");
        textGraphics.putString(0, 1, "Quit: q / <esc>.");
        for (int i = 0; i < dimesions.x; i++) {
            for (int j = 0; j < dimesions.y; j++) {
                char c = context.getWorld().cellDisplay(j, i);
                textGraphics.putString(i + ydelta, j + xdelta, c + " ");
            }
        }
        terminal.flush();

        KeyStroke key = terminal.readInput();
        KeyType keyType = key.getKeyType();
        if (keyType == KeyType.Escape) {
            terminal.exitPrivateMode();
            onPlayerAction(PlayerControl.Control.EXIT);
        } else if (keyType == KeyType.Character) {
            Character c = key.getCharacter();
            if (c == 'q') {
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

    @Override
    public void reload() {
        dimesions = context.getWorld().getDimensions();
    }

}
