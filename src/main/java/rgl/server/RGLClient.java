package rgl.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import rgl.gui.ConsoleGUI;
import rgl.gui.GUI;
import rgl.logic.GameContext;
import rgl.map.terrain.TerrainMapImpl;
import rgl.proto.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * implementation of GRPC client
 */
public class RGLClient {

    private final ManagedChannel channel;
    private final NetworkRGLGrpc.NetworkRGLStub stub;
    private final StreamObserver<PlayerMove> observer;
    private final GameContext context;
    private GUI gui;
    private String playerID;
    private String server;
    private boolean exit = false;

    public RGLClient(String serverName, String host, int port) {
        this.server = serverName;
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = NetworkRGLGrpc.newStub(channel);

        context = new GameContext(new TerrainMapImpl(100, 29),
                Collections.newSetFromMap(new IdentityHashMap<>()));

        observer = stub.move(
                new StreamObserver<GameObjectsProto.GameContext>() {
                    @Override
                    public void onNext(GameObjectsProto.GameContext value) {
                        context.getAsSerializableContext().deserializeFromProto(value);

                        if (gui != null) {
                            try {
                                gui.update();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Oops :(");
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        exit = true;
                        context.getGameStatus().appendStatus("You died!");
                    }
                }
        );
    }

    // Debug main.
    public static void main(String[] args) throws InterruptedException {
        RGLClient client = null;
        try {
            client = new RGLClient("test", "localhost", 8888);
            client.connect(true);
        } catch (IOException e) {
            System.err.println("error: unable to run the application! Please, contact the developers.");
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    private void createGUI() throws IOException {
        gui = new ConsoleGUI(context, UUID.fromString(playerID), System.out::println, () -> exit = true);
        gui.addActionListener(action -> {
            PlayerMove.Builder builder = PlayerMove.newBuilder()
                    .setPlayerId(playerID)
                    .setServer(Server.newBuilder().setName(server).build());

            switch (action) {
                case UP:
                    observer.onNext(builder.setAction(PlayerAction.UP).build());
                    break;
                case DOWN:
                    observer.onNext(builder.setAction(PlayerAction.DOWN).build());
                    break;
                case LEFT:
                    observer.onNext(builder.setAction(PlayerAction.LEFT).build());
                    break;
                case RIGHT:
                    observer.onNext(builder.setAction(PlayerAction.RIGHT).build());
                    break;
                case SKIP:
                    observer.onNext(builder.setAction(PlayerAction.SKIP).build());
                    break;
                case DROP:
                    observer.onNext(builder.setAction(PlayerAction.DROP).build());
                    break;
            }
        });
    }

    private void run() throws IOException {
        // initialize game stream
        observer.onNext(PlayerMove.newBuilder()
                .setPlayerId(playerID)
                .setServer(Server.newBuilder().setName(server).build())
                .setAction(PlayerAction.SKIP).build());

        while (!exit) {
            if (gui.iteration()) {
                // do something
            }
        }

        // leave session
        observer.onCompleted();
        gui.close();
    }

    /**
     * shutdown client
     *
     * @throws InterruptedException
     */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * request list of available server sessions
     * @return list of sessions names
     * @throws InterruptedException
     */
    public List<String> requestServerList() throws InterruptedException {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        List<String> list = new ArrayList<>();
        stub.getServerList(Empty.newBuilder().build(), new StreamObserver<ServerList>() {
            @Override
            public void onNext(ServerList value) {
                list.addAll(value.getServersList().stream().map(Server::getName).collect(Collectors.toList()));
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        });

        if (!finishLatch.await(5, TimeUnit.SECONDS)) {
            System.err.println("error: unable to connect to the server!");
        }

        return list;
    }

    /**
     * connect to server and enter the game
     * @param newServer true - create new server session
     * @throws InterruptedException
     * @throws IOException
     */
    public void connect(boolean newServer) throws InterruptedException, IOException {
        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<EnterServerResponse> responseStreamObserver = new StreamObserver<EnterServerResponse>() {
            @Override
            public void onNext(EnterServerResponse value) {
                context.getAsSerializableContext().deserializeFromProto(value.getContext());
                playerID = value.getPlayerId();
            }

            @Override
            public void onError(Throwable t) {
                // something went wrong, print unable to connect
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };

        if (newServer) {
            stub.createServer(Server.newBuilder().setName(server).build(), responseStreamObserver);
        } else {
            stub.enterServer(Server.newBuilder().setName(server).build(), responseStreamObserver);
        }

        // wait 10 seconds for response
        if (finishLatch.await(10, TimeUnit.SECONDS)) {
            createGUI(); // create interface
            run(); // start game
        } else {
            System.err.println("error: unable to connect to the server!");
        }
    }
}
