package com.github.vlastikcz.ciconsumer.domain.repository;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Repository;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;

@Repository
public class ReleaseDetailNotificationTaskRepository {
    private final Queue<ReleaseDetailNotificationTask> queue;

    public ReleaseDetailNotificationTaskRepository() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public ReleaseDetailNotificationTask poll() {
        return queue.poll();
    }

    public boolean hasNext() {
        return queue.iterator().hasNext();
    }

    public void create(ReleaseDetailNotificationTask releaseDetailNotificationTask) {
        Objects.requireNonNull(releaseDetailNotificationTask, "'releaseDetailNotificationTask' cannot be null");
        queue.add(releaseDetailNotificationTask);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }


}
