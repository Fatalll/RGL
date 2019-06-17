package rgl.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import rgl.commands.SaveCommand;
import rgl.gui.ConsoleGUI;
import rgl.gui.GUI;
import rgl.logic.GameContext;
import rgl.map.terrain.TerrainMapImpl;
import rgl.proto.*;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RGLClient {

    private final ManagedChannel channel;
    private final NetworkRGLGrpc.NetworkRGLStub stub;
    private final StreamObserver<PlayerMove> observer;
    private final GameContext context;
    private GUI gui;
    private String playerID;
    private String server;
    private boolean exit = false;

    public RGLClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = NetworkRGLGrpc.newStub(channel);

        context = new GameContext(new TerrainMapImpl(100, 29),
                Collections.newSetFromMap(new IdentityHashMap<>()));

        //gui = new ConsoleGUI(context, playerID, new SaveCommand(context), () -> exit = true);

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
                    }

                    @Override
                    public void onCompleted() {
                        context.getGameStatus().appendStatus("You died!");
                    }
                }
        );
    }

    public static void main(String[] args) throws InterruptedException {
        RGLClient client = new RGLClient("5.19.190.45", 8888);
        client.server = "test";
        try {
            final CountDownLatch finishLatch = new CountDownLatch(1);
            client.stub.createServer(Server.newBuilder().setName(client.server).build(),
                    new StreamObserver<EnterServerResponse>() {
                        @Override
                        public void onNext(EnterServerResponse value) {
                            client.context.getAsSerializableContext().deserializeFromProto(value.getContext());
                            client.playerID = value.getPlayerId();
                        }

                        @Override
                        public void onError(Throwable t) {
                            finishLatch.countDown();
                        }

                        @Override
                        public void onCompleted() {
                            finishLatch.countDown();
                        }
                    });

            if (finishLatch.await(10, TimeUnit.SECONDS)) {
                client.createGUI();
                client.run();
            } else {
                System.out.println("Can't connect to the server!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.shutdown();
        }
    }

    private void createGUI() throws IOException {
        gui = new ConsoleGUI(context, UUID.fromString(playerID), new SaveCommand(context), () -> exit = true);
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
        observer.onNext(PlayerMove.newBuilder()
                .setPlayerId(playerID)
                .setServer(Server.newBuilder().setName(server).build())
                .setAction(PlayerAction.SKIP).build());

        while (!exit) {
            if (gui.iteration()) {
                // do something
            }
        }

        gui.close();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
