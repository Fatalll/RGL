package rgl.logic;

/**
 * This is the abstraction under the current status of the game.
 *
 * Status is a text message from the game components.

 */
public class GameStatus {
    private String msg;

    /**
     * Return current game status.
     *
     * @return Status (simple text message)
     */
    public String getStatus() {
        return msg;
    }

    /**
     * Fully update the current game status (and erase the old one).
     *
     * @param msg New status.
     */
    public void updateStatus(String msg) {
        this.msg = msg;
    }

    /**
     * Append new status to the current game status.
     *
     * @param msg Appended status.
     */
    public void appendStatus(String msg) {
        this.msg = this.msg + " " + msg;
    }
}
