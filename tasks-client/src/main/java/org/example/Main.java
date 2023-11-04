package org.example;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannelBuilder;
import org.example.concurrency.MyJob;
import org.example.proto.MyTasksServiceGrpc;
import org.example.proto.TaskCommand;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        var channel = ManagedChannelBuilder.forAddress("localhost", 6200)
            .usePlaintext()
            .build();

        MyTasksServiceGrpc.MyTasksServiceStub stub = MyTasksServiceGrpc.newStub(channel);

        var tasks = new ConcurrentLinkedQueue<TaskCommand>();
        var stopWorkersLatch = new CountDownLatch(1);

        var worker1 = new Thread(new MyJob(tasks, stub, stopWorkersLatch));
        var worker2 = new Thread(new MyJob(tasks, stub, stopWorkersLatch));
        var worker3 = new Thread(new MyJob(tasks, stub, stopWorkersLatch));

        System.out.println("Created workers");

        worker1.start();
        worker2.start();
        worker3.start();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 50; j++) {
                var task = TaskCommand.newBuilder()
                        .setTaskId(i * 10 + j)
                        .build();

                tasks.add(task);
            }

            System.out.println("Added batch of tasks. Sleeping...");
            Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        }

        System.out.println("Stopping workers");
        stopWorkersLatch.countDown();

        try {
            worker1.join();
            worker2.join();
            worker3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}