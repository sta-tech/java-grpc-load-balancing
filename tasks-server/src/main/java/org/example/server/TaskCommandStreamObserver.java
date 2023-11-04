package org.example.server;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;
import org.example.proto.EnqueueResult;
import org.example.proto.TaskCommand;
import org.example.proto.TaskEnqueueResult;
import org.example.proto.TaskInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class TaskCommandStreamObserver implements StreamObserver<TaskCommand> {
    private final StreamObserver<EnqueueResult> enqueueResultObserver;
    private final List<TaskEnqueueResult> results = new ArrayList<>();

    public TaskCommandStreamObserver(StreamObserver<EnqueueResult> enqueueResultObserver) {
        this.enqueueResultObserver = enqueueResultObserver;
    }

    @Override
    public void onNext(TaskCommand taskCommand) {
        System.out.println("[" + ProcessHandle.current().pid() + "] [" + Thread.currentThread().getId() + "] Running task " + taskCommand.getTaskId());
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        results.add(
            TaskEnqueueResult.newBuilder()
                .setTaskId(taskCommand.getTaskId())
                .setInstance(
                    TaskInstance.newBuilder()
                        .setInstanceId(UUID.randomUUID().toString())
                        .build()
                ).build()
        );
    }

    @Override
    public void onError(Throwable throwable) {
        // Do nothing
    }

    @Override
    public void onCompleted() {
        System.out.println("[" + ProcessHandle.current().pid() + "] [" + Thread.currentThread().getId() + "] Stream is completed (" + results.size() + " tasks done)");
        enqueueResultObserver.onNext(
            EnqueueResult.newBuilder()
                .addAllResults(results)
                .build()
        );
        enqueueResultObserver.onCompleted();
    }
}
