package tk.jomp16;

import tk.jomp16.rcon.RconClient;
import tk.jomp16.rcon.internal.RconServer;

import java.io.IOException;

public final class Main {
    public static void main(final String[] args) throws IOException {
        new RconServer("127.0.0.1", 27015, "lolwut", true).startServer();

        try {
            Thread.sleep(1000);
        } catch(final InterruptedException e) {
            e.printStackTrace();
        }

        RconClient.start();
    }
}
