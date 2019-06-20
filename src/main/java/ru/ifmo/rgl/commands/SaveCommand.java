package ru.ifmo.rgl.commands;

import ru.ifmo.rgl.logic.GameContext;

import java.io.FileOutputStream;

/**
 * save current game state command
 */
public class SaveCommand implements Command {

    private GameContext context;

    public SaveCommand(GameContext context) {
        this.context = context;
    }

    @Override
    public void execute() {
        try (FileOutputStream output = new FileOutputStream("gamestate")) {
            context.getAsSerializableContext().serializeToProto().writeTo(output);
            context.updateGameStatus("Game saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
