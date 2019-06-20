package rgl;

import org.apache.commons.cli.*;
import rgl.logic.GameLoop;
import rgl.server.RGLClient;
import rgl.server.RGLServer;

import java.io.IOException;

/**
 * Main class, start program with arguments:
 * -f <arg>   Path to the user rgl.map.
 * -g         Generate a random rgl.map.
 * -i <arg>   IPv4 address of the server to connect to.
 * -l         Load game if present.
 * -n         Create new server.
 * -p <arg>   Port of the server to connect to.
 * -r         Request available servers list.
 * -s <arg>   Server name to connect to.
 */
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

        if (serverListOptions(cmd)) {
            requestServerList(cmd);
        } else if (clientOptions(cmd)) {
            System.out.println("Multiplayer mode: run as a client.");
            runClient(cmd);
        } else if (sessionOptions(cmd)) {
            System.out.println("Multiplayer mode: run as a server.");
            runServer(cmd);
        } else {
            System.out.println("Singleplayer mode.");
            runSimple(cmd);
        }
    }

    private static boolean clientOptions(CommandLine cmd) {
        return cmd.hasOption(CMD.ipOption) && cmd.hasOption(CMD.portOption)
                && cmd.hasOption(CMD.sessionOption);
    }

    private static boolean serverListOptions(CommandLine cmd) {
        return cmd.hasOption(CMD.ipOption) && cmd.hasOption(CMD.portOption)
                && cmd.hasOption(CMD.requestServersOption);
    }

    private static boolean sessionOptions(CommandLine cmd) {
        return cmd.hasOption(CMD.portOption);
    }

    private static void runClient(CommandLine cmd) {
        String portStr = (String) cmd.getOptionObject(CMD.portOption.charAt(0));
        String ip = (String) cmd.getOptionObject(CMD.ipOption.charAt(0));
        String name = (String) cmd.getOptionObject(CMD.sessionOption.charAt(0));
        boolean newServer = cmd.hasOption(CMD.createServerOption);
        int port = Integer.valueOf(portStr);
        runClient_(name, ip, port, newServer);
    }

    private static void runClient_(String name, String ip, int port, boolean newServer) {
        RGLClient client = null;
        try {
            client = new RGLClient(name, ip, port);
            client.connect(newServer);
        } catch (IOException | InterruptedException e) {
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

    private static void requestServerList(CommandLine cmd) {
        String portStr = (String) cmd.getOptionObject(CMD.portOption.charAt(0));
        int port = Integer.valueOf(portStr);
        String ip = (String) cmd.getOptionObject(CMD.ipOption.charAt(0));

        RGLClient client = null;
        try {
            client = new RGLClient(" ", ip, port);
            System.out.println("Available servers:");
            client.requestServerList().forEach(System.out::println);
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
        final static String sessionOption = "s";
        final static String createServerOption = "n";
        final static String requestServersOption = "r";
        final static Options options = new Options();

        static {
            options.addOption(pathOption, true, "Path to the user rgl.map.");
            options.addOption(generatOption, "Generate a random rgl.map.");
            options.addOption(loadOption, "Load game if present.");
            options.addOption(sessionOption, true, "Session name to connect to.");
            options.addOption(createServerOption, false, "Create new server.");
            options.addOption(requestServersOption, false, "Request available servers list.");
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
