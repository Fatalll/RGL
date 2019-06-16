package rgl;

import org.apache.commons.cli.*;
import rgl.logic.GameLoop;

import java.io.IOException;

public class Main {

    /**
     * Command line options:
     * -f path -- load rgl.map from the user file.
     * Map must be 10x10, with borders '#', 's' as the start point,
     * 'e' as the end point, and '.' as a floor.
     * -g      -- generate a random rgl.map 10x10.
     */
    public static void main(String[] args) {
        Options options = CMD.getOptions();

        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println("error: unable to parse command line parameters.");
            CMD.usage(options);
            return;
        }

        try {
            GameLoop gameLoop = null;

            if (cmd.hasOption(CMD.loadOption)) {
                gameLoop = new GameLoop(null, true);
            } else {
                if (cmd.hasOption(CMD.generatOption)) {
                    gameLoop = new GameLoop(null, false);
                } else if (cmd.hasOption(CMD.pathOption)) {
                    String str = (String) cmd.getOptionObject(CMD.pathOption.charAt(0));
                    gameLoop = new GameLoop(str, false);
                } else {
                    System.err.println("error: no required options.");
                    CMD.usage(options);
                    return;
                }
            }

            gameLoop.run();
        } catch (IOException e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    static class CMD {
        final static String pathOption = "f";
        final static String generatOption = "g";
        final static String loadOption = "l";

        static Options getOptions() {
            Options options = new Options();
            options.addOption(pathOption, true, "Path to the user rgl.map.");
            options.addOption(generatOption, "Generate a random rgl.map.");
            options.addOption(loadOption, "Load game if present.");
            return options;
        }

        static void usage(Options options) {
            HelpFormatter fmt = new HelpFormatter();
            fmt.printHelp("RGL", options);
        }
    }
}
