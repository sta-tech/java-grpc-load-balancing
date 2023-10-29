package org.example.server;

import io.grpc.stub.StreamObserver;
import org.example.proto.EnqueueResult;
import org.example.proto.MyTasksServiceGrpc;
import org.example.proto.TaskCommand;

public class MyTasksService extends MyTasksServiceGrpc.MyTasksServiceImplBase {
    @Override
    public StreamObserver<TaskCommand> enqueueTask(StreamObserver<EnqueueResult> responseObserver) {
        return new TaskCommandStreamObserver(responseObserver);
    }
}
