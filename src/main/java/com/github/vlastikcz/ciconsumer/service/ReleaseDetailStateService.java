package com.github.vlastikcz.ciconsumer.service;

import java.util.Collections;
import java.util.stream.Stream;

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

    public Stream<ReleaseDetailState> asStream() {
        log.debug("action.call=asStream");
        final Stream<ReleaseDetailState> releaseDetailStateStream = releaseDetailStateRepository.asStream();
        log.debug("action.result=asStream, result=[{} items]", releaseDetailStateRepository.size());
        return releaseDetailStateStream;
    }

    public void delete(ReleaseDetailState releaseDetailState) {
        log.debug("action.call=delete, arguments=[{}]", releaseDetailState);
        releaseDetailStateRepository.delete(releaseDetailState);
        log.debug("action.done=delete");
    }

    public void create(ReleaseDetail releaseDetail) {
        log.debug("action.call=create, arguments=[{}]", releaseDetail);
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, Collections.emptyList());
        releaseDetailStateRepository.create(releaseDetailState);
        applicationEventPublisher.publishEvent(new ReleaseDetailStateCreateEvent());
        log.debug("action.done=create");
    }

    public void update(ReleaseDetailState original, ReleaseDetailState updated) {
        log.debug("action.call=update, arguments=[{}, {}]", original, updated);
        releaseDetailStateRepository.update(original, updated);
        log.debug("action.done=update");
    }
}
