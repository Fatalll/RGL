package rgl.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import rgl.gameobjects.GameObject;
import rgl.gameobjects.characters.player.Confusion;
import rgl.gameobjects.characters.player.Player;
import rgl.gui.GUI;
import rgl.gui.PlayerControl;
import rgl.logic.GameContext;
import rgl.logic.GameLoop;
import rgl.map.WorldMapLayout;
import rgl.map.terrain.TerrainMapImpl;
import rgl.proto.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RGLServer {

    private final int port;
    private final Server server;

    public RGLServer(int port) {
        this.port = port;
        server = ServerBuilder.forPort(port).addService(new NetworkRGLService()).build();
    }

	// Debug main.
    public static void main(String[] args) throws Exception {
        RGLServer server = new RGLServer(8888);
        server.start();
        server.blockUntilShutdown();
    }

    public void start() throws IOException {
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(RGLServer.this::stop));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void run() throws InterruptedException, IOException {
		start();
		blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private static class NetworkRGLService extends NetworkRGLGrpc.NetworkRGLImplBase {

        private static final int STEP_INTERVAL = 100;
        private static final int STEP_DELAY = 100;
        private Map<String, ServerGameLoop> sessions = new HashMap<>();
        private Timer timer = new Timer();

        @Override
        public void getServerList(Empty request, StreamObserver<ServerList> responseObserver) {
            ServerList.Builder builder = ServerList.newBuilder();

            for (String name : sessions.keySet()) {
                builder.addServers(rgl.proto.Server.newBuilder().setName(name).build());
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void createServer(rgl.proto.Server request, StreamObserver<EnterServerResponse> responseObserver) {
            ServerGameLoop loop;
            if (sessions.containsKey(request.getName())) {
                loop = sessions.get(request.getName());
            } else {
                loop = new ServerGameLoop();
                sessions.put(request.getName(), loop);
                timer.schedule(loop, STEP_DELAY, STEP_INTERVAL);
            }

            responseObserver.onNext(EnterServerResponse.newBuilder()
                    .setPlayerId(loop.registerPlayer().toString())
                    .setContext(loop.getGameContext())
                    .build());

            responseObserver.onCompleted();

        }

        @Override
        public void enterServer(rgl.proto.Server request, StreamObserver<EnterServerResponse> responseObserver) {
            ServerGameLoop loop = sessions.get(request.getName());

            EnterServerResponse.Builder builder = EnterServerResponse.newBuilder();
            if (loop != null) {
                builder.setPlayerId(loop.registerPlayer().toString()).setContext(loop.getGameContext());
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<PlayerMove> move(StreamObserver<GameObjectsProto.GameContext> responseObserver) {
            return new StreamObserver<PlayerMove>() {
                private UUID playerID;
                private ServerGameLoop loop;

                @Override
                public void onNext(PlayerMove value) {
                    loop = sessions.get(value.getServer().getName());

                    if (playerID == null) {
                        playerID = UUID.fromString(value.getPlayerId());
                        loop.addResponseObserver(playerID, responseObserver);
                    }

                    if (loop == null) {
                        responseObserver.onCompleted();
                        return;
                    }

                    loop.addAction(playerID, value.getAction());
                }

                @Override
                public void onError(Throwable t) {
                    loop.removePlayer(playerID);
                }

                @Override
                public void onCompleted() {
                    loop.removePlayer(playerID);
                }
            };
        }

        private static class ServerGameLoop extends TimerTask {
            private GameContext context;
            private Map<UUID, Queue<PlayerAction>> playerActions = new ConcurrentHashMap<>();
            private Set<GameLoop.IterationListener> listeners = Collections.newSetFromMap(new IdentityHashMap<>());
            private Map<UUID, StreamObserver<GameObjectsProto.GameContext>> observers = new ConcurrentHashMap<>();

            public ServerGameLoop() {
                context = new GameContext(new TerrainMapImpl(100, 29), listeners);
            }

            @Override
            public void run() {
                playerActions.forEach((playerID, actionQueue) -> {
                    Player player = context.getPlayers().get(playerID);
                    GUI.ActionListener listener = player.isConfused() ? new Confusion(player) : player;

                    PlayerAction action = actionQueue.poll();
                    if (action == PlayerAction.DROP) {
                        player.dropItem(0);
                    } else {
                        listener.onAction(PlayerControl.castFromPlayerAction(action));
                    }
                });

                for (GameLoop.IterationListener listener : new HashSet<>(listeners)) {
                    listener.iterate(context);
                }

                if (context.getPlayers().values().stream().map(GameObject::getPosition)
                        .anyMatch(p -> p.equals(context.getWorld().getExit()))) {
                    listeners.clear();
                    context.getWorld().loadMap(new WorldMapLayout(new TerrainMapImpl(100, 29), context));
                    context.getPlayers().values().forEach(player -> context.getWorld().initializePlayerRandomly(player));
                    context.updateGameStatus("New region!");
                }

                context.getPlayers().entrySet().stream().filter(entry -> entry.getValue().getHealth() <= 0)
                        .forEach(entry -> {
                            context.getPlayers().remove(entry.getKey());
                            playerActions.remove(entry.getKey());
                            observers.remove(entry.getKey()).onCompleted();
                        });

                observers.values().forEach(gameContextStreamObserver ->
                        gameContextStreamObserver.onNext(context.getAsSerializableContext().serializeToProto()));
            }

            public void addAction(UUID playerId, PlayerAction action) {
                Queue<PlayerAction> queue = playerActions.get(playerId);
                if (queue != null) {
                    queue.add(action);
                }
            }

            public UUID registerPlayer() {
                Player player = new Player(context, 1);
                UUID playerID = context.addPlayer(player);
                player.setId(playerID.toString());
                playerActions.put(playerID, new ConcurrentLinkedQueue<>());
                context.getWorld().initializePlayerRandomly(player);
                return playerID;
            }

            public GameObjectsProto.GameContext getGameContext() {
                return context.getAsSerializableContext().serializeToProto();
            }

            public void addResponseObserver(UUID uuid, StreamObserver<GameObjectsProto.GameContext> responseObserver) {
                observers.put(uuid, responseObserver);
            }

            public void removePlayer(UUID playerID) {
                context.getPlayers().remove(playerID);
                playerActions.remove(playerID);
                observers.remove(playerID).onCompleted();
            }
        }

    }
}
