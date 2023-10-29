package org.example;

import org.example.server.GrpcServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6100;

        var server = new GrpcServer(port);

        try {
            server.startAndWait();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}