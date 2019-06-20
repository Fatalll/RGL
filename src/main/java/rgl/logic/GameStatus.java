package rgl.logic;

public class GameStatus {
    private String msg;

    public String getStatus() {
        return msg;
    }

    public void updateStatus(String msg) {
        this.msg = msg;
    }

    public void appendStatus(String msg) {
        this.msg = this.msg + " " + msg;
    }
}
