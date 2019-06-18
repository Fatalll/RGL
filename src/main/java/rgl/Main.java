package rgl;

import org.apache.commons.cli.*;
import rgl.logic.GameLoop;
import rgl.server.RGLClient;
import rgl.server.RGLServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Options options = CMD.getOptions();

        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println("error: unable to parse command line parameters.");
            CMD.usage();
            return;
        }

        if (clientOptions(cmd)) {
            System.out.println("Multiplayer mode: run as a client.");
            runClient(cmd);
        } else if (serverOptions(cmd)) {
            System.out.println("Multiplayer mode: run as a server.");
            runServer(cmd);
        } else {
            System.out.println("Singleplayer mode.");
            runSimple(cmd);
        }
    }

    private static boolean clientOptions(CommandLine cmd) {
        return cmd.hasOption(CMD.ipOption) && cmd.hasOption(CMD.portOption)
                && cmd.hasOption(CMD.serverOption);
    }

    private static boolean serverOptions(CommandLine cmd) {
        return cmd.hasOption(CMD.portOption);
    }

    private static void runClient(CommandLine cmd) {
        String portStr = (String) cmd.getOptionObject(CMD.portOption.charAt(0));
        String ip = (String) cmd.getOptionObject(CMD.ipOption.charAt(0));
        String name = (String) cmd.getOptionObject(CMD.serverOption.charAt(0));
        boolean newServer = cmd.hasOption(CMD.createServerOption);
        int port = Integer.valueOf(portStr);
        runClient_(name, ip, port, newServer);
    }

    private static void runClient_(String name, String ip, int port, boolean newServer) {
        RGLClient client = null;
        try {
            client = new RGLClient(name, ip, port);
            client.connect(newServer);
        } catch (IOException e) {
            System.err.println("error: unable to run the application! Please, contact the developers.");
        } catch (InterruptedException e) {
            System.err.println("error: unable to run the application! Please, contact the developers.");
        } finally {
            if (client != null) {
                try {
                    client.shutdown();
                } catch (InterruptedException e) {
                    System.err.println("error: " + e.getMessage());
                }
            }
        }
    }

    private static void runServer(CommandLine cmd) {
        String portStr = (String) cmd.getOptionObject(CMD.portOption.charAt(0));
        int port = Integer.valueOf(portStr);
        runServer_(port);
    }

    private static void runServer_(int port) {
        try {
            RGLServer server = new RGLServer(port);
            server.run();
        } catch (InterruptedException | IOException e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private static void runSimple(CommandLine cmd) {
        try {
            GameLoop gameLoop = null;
            if (cmd.hasOption(CMD.loadOption)) {
                gameLoop = new GameLoop(null, true);
            } else if (cmd.hasOption(CMD.generatOption)) {
                gameLoop = new GameLoop(null, false);
            } else if (cmd.hasOption(CMD.pathOption)) {
                String str = (String) cmd.getOptionObject(CMD.pathOption.charAt(0));
                gameLoop = new GameLoop(str, false);
            } else {
                System.err.println("error: no required options.");
                CMD.usage();
                return;
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
        final static String ipOption = "i";
        final static String portOption = "p";
        final static String serverOption = "s";
        final static String createServerOption = "n";
        final static Options options = new Options();

        static {
            options.addOption(pathOption, true, "Path to the user rgl.map.");
            options.addOption(generatOption, "Generate a random rgl.map.");
            options.addOption(loadOption, "Load game if present.");
            options.addOption(serverOption, true, "Server name to connect to.");
            options.addOption(createServerOption, false, "Create new server.");
            options.addOption(ipOption, true, "IPv4 address of the server to connect to.");
            options.addOption(portOption, true, "Port of the server to connect to.");
        }

        static Options getOptions() {
            return options;
        }

        static void usage() {
            HelpFormatter fmt = new HelpFormatter();
            fmt.printHelp("RGL", options);
        }
    }
}
