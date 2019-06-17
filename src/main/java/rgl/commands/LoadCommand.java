package rgl.commands;

import rgl.logic.GameContext;
import rgl.proto.GameObjectsProto;

import java.io.FileInputStream;

/**
 * load last saved game command
 */
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
