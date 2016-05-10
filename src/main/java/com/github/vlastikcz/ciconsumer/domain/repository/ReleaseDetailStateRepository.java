package com.github.vlastikcz.ciconsumer.domain.repository;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Repository;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailState;

@Repository
public class ReleaseDetailStateRepository {
    private final Queue<ReleaseDetailState> queue;

    public ReleaseDetailStateRepository() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public ReleaseDetailState poll() {
        return queue.poll();
    }

    public boolean hasNext() {
        return queue.iterator().hasNext();
    }

    public void create(ReleaseDetailState releaseDetailState) {
        Objects.requireNonNull(releaseDetailState, "'releaseDetailState' cannot be null");
        queue.add(releaseDetailState);
    }

    public void delete(ReleaseDetailState releaseDetailState) {
        Objects.requireNonNull(releaseDetailState, "'releaseDetailState' cannot be null");
        queue.remove(releaseDetailState);
    }

}
