package util;

import logic.GameContext;
import protobuf.GameObjectsProto;

import java.io.FileInputStream;

public class LoadCommand implements Command {

    private GameContext context;

    public LoadCommand(GameContext context) {
        this.context = context;
    }

    @Override
    public void execute() {
        try (FileInputStream input = new FileInputStream("gamestate")) {
            context.getAsSerializableContext().deserializeFromProto(GameObjectsProto.GameContext.parseFrom(input));
            context.updateGameStatus("Game loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
