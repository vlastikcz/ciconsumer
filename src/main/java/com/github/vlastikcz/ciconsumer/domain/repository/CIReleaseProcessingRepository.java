package com.github.vlastikcz.ciconsumer.domain.repository;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Repository;

import com.github.vlastikcz.ciconsumer.domain.entity.CIRelease;
import com.github.vlastikcz.ciconsumer.domain.entity.CIReleaseProcessing;

@Repository
public class CIReleaseProcessingRepository {
    private final Queue<CIReleaseProcessing> queue;

    public CIReleaseProcessingRepository() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public Optional<CIReleaseProcessing> poll() {
        return Optional.ofNullable(queue.poll());
    }

    public void save(CIRelease ciRelease) {
        Objects.requireNonNull(ciRelease, "'ciRelease' cannot be null");
        queue.add(new CIReleaseProcessing(ciRelease, Collections.emptyList()));
    }

    public void save(CIReleaseProcessing ciReleaseProcessing) {
        queue.add(ciReleaseProcessing);
    }
}
