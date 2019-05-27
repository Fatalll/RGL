import logic.GameLoop;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new GameLoop("/test2.map").run();
    }
}
