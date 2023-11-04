package org.example.concurrency;

import org.example.client.TaskEnqueueStreamObserver;
import org.example.proto.MyTasksServiceGrpc;
import org.example.proto.TaskCommand;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyJob implements Runnable {
    private Queue<TaskCommand> tasks;
    private MyTasksServiceGrpc.MyTasksServiceStub stub;
    private CountDownLatch latch;

    public MyJob(
        Queue<TaskCommand> tasks,
        MyTasksServiceGrpc.MyTasksServiceStub stub,
        CountDownLatch latch
    ) {
        this.tasks = tasks;
        this.stub = stub;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            do {
                var task = tasks.poll();
                if (task == null) {
                    continue;
                }

                var streamLatch = new CountDownLatch(1);
                var stream = stub.enqueueTask(new TaskEnqueueStreamObserver(streamLatch));

                int counter = 0;
                while (task != null) {
                    stream.onNext(task);

                    if (++counter > 10) {
                        break;
                    }

                    task = tasks.poll();
                }

                stream.onCompleted();
                streamLatch.await();
            } while (!latch.await(100, TimeUnit.MILLISECONDS));
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
