package org.spanna.hook;

import org.spanna.Server;

public class DedicatedServer {

    private DedicatedServer() {
    }

    public static void init() {
        Server server = new Server();
    }
}
