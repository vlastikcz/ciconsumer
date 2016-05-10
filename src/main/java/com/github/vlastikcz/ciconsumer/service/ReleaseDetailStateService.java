package com.github.vlastikcz.ciconsumer.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailState;
import com.github.vlastikcz.ciconsumer.domain.repository.ReleaseDetailStateRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReleaseDetailStateService {
    private final ReleaseDetailStateRepository releaseDetailStateRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public ReleaseDetailStateService(ReleaseDetailStateRepository releaseDetailStateRepository,
                                     ApplicationEventPublisher applicationEventPublisher) {
        this.releaseDetailStateRepository = releaseDetailStateRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ReleaseDetailState poll() {
        log.debug("action.call=poll");
        final ReleaseDetailState result = releaseDetailStateRepository.poll();
        log.debug("action.result=poll, result=[{}]", result);
        return result;
    }

    public boolean hasNext() {
        log.debug("action.call=hasNext");
        final boolean result = releaseDetailStateRepository.hasNext();
        log.debug("action.result=hasNext, result=[{}]", result);
        return result;
    }

    public void create(ReleaseDetail releaseDetail) {
        log.debug("action.call=create, arguments=[{}]", releaseDetail);
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, Collections.emptyList());
        releaseDetailStateRepository.create(releaseDetailState);
        applicationEventPublisher.publishEvent(new ReleaseDetailStateCreateEvent());
        log.debug("action.done=create");
    }

    public void reQueue(ReleaseDetailState releaseDetail) {
        log.debug("action.call=reQueue, arguments=[{}]", releaseDetail);
        releaseDetailStateRepository.create(releaseDetail);
        log.debug("action.done=reQueue");
    }
}
