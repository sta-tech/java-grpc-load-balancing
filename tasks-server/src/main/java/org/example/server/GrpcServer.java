package org.example.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public final class GrpcServer {
    private final Server server;

    public GrpcServer(int port) {
        server = ServerBuilder.forPort(port)
            .addService(new MyTasksService())
            .build();
    }

    public void startAndWait() throws IOException, InterruptedException {
        this.server.start();

        System.out.println("[" + ProcessHandle.current().pid() + "] Started server on port " + server.getPort());

        this.server.awaitTermination();
    }
}
