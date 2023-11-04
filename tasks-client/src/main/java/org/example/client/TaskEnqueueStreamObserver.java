package org.example.client;

import io.grpc.stub.StreamObserver;
import org.example.proto.EnqueueResult;

import java.util.concurrent.CountDownLatch;

public class TaskEnqueueStreamObserver implements StreamObserver<EnqueueResult> {
    private CountDownLatch latch;

    public TaskEnqueueStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(EnqueueResult taskEnqueue) {
        System.out.println("Enqueued " + taskEnqueue.getResultsCount() + " tasks");
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Unexpected error: " + throwable.getCause().toString());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        latch.countDown();
    }
}
