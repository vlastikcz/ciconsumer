package com.github.vlastikcz.ciconsumer.domain.repository;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailState;

@Repository
public class ReleaseDetailStateRepository {
    private final Queue<ReleaseDetailState> queue;

    public ReleaseDetailStateRepository() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public Stream<ReleaseDetailState> asStream() {
        return queue.stream();
    }

    public void create(ReleaseDetailState releaseDetailState) {
        Objects.requireNonNull(releaseDetailState, "'releaseDetailState' cannot be null");
        queue.add(releaseDetailState);
    }

    public void delete(ReleaseDetailState releaseDetailState) {
        Objects.requireNonNull(releaseDetailState, "'releaseDetailState' cannot be null");
        queue.remove(releaseDetailState);
    }

    public void update(ReleaseDetailState original, ReleaseDetailState updated) {
        Objects.requireNonNull(original, "'original' cannot be null");
        Objects.requireNonNull(updated, "'updated' cannot be null");
        queue.remove(original);
        queue.add(updated);
    }

    public int size() {
        return queue.size();
    }
}
