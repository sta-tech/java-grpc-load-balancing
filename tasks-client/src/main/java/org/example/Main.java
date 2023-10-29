package org.example;

import io.grpc.ManagedChannelBuilder;
import org.example.client.TaskEnqueueStreamObserver;
import org.example.proto.MyTasksServiceGrpc;
import org.example.proto.TaskCommand;

import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        var channel = ManagedChannelBuilder.forAddress("localhost", 6200)
            .usePlaintext()
            .build();

        MyTasksServiceGrpc.MyTasksServiceStub stub = MyTasksServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        var taskObserver = stub.enqueueTask(new TaskEnqueueStreamObserver(latch));

        for(int i = 1; i <= 10; i++) {
            var task = TaskCommand.newBuilder()
                .setTaskId(i)
                .build();

            taskObserver.onNext(task);
        }

        taskObserver.onCompleted();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}