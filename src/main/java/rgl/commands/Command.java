package rgl.commands;

import java.io.IOException;

/**
 * interface for user-specific command (pattern command)
 */
public interface Command {

    /**
     * execute command logic
     */
    void execute() throws IOException;
}
