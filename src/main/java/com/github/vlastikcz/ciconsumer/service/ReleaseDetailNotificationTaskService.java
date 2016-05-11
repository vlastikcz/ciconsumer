package com.github.vlastikcz.ciconsumer.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;
import com.github.vlastikcz.ciconsumer.domain.repository.ReleaseDetailNotificationTaskRepository;
import com.github.vlastikcz.ciconsumer.service.event.ReleaseDetailNotificationTaskCreateEvent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReleaseDetailNotificationTaskService {
    private final ReleaseDetailNotificationTaskRepository releaseDetailNotificationTaskRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public ReleaseDetailNotificationTaskService(ReleaseDetailNotificationTaskRepository releaseDetailNotificationTaskRepository,
                                                ApplicationEventPublisher applicationEventPublisher) {
        this.releaseDetailNotificationTaskRepository = releaseDetailNotificationTaskRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ReleaseDetailNotificationTask poll() {
        log.debug("action.call=poll");
        final ReleaseDetailNotificationTask result = releaseDetailNotificationTaskRepository.poll();
        log.debug("action.result=poll, result=[{}]", result);
        return result;
    }

    public boolean hasNext() {
        log.debug("action.call=hasNext");
        final boolean result = releaseDetailNotificationTaskRepository.hasNext();
        log.debug("action.result=hasNext, result=[{}]", result);
        return result;
    }

    public void create(ReleaseDetail releaseDetail) {
        log.debug("action.call=create, arguments=[{}]", releaseDetail);
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, Collections.emptyList());
        applicationEventPublisher.publishEvent(new ReleaseDetailNotificationTaskCreateEvent(releaseDetailNotificationTask));
        log.debug("action.done=create");
    }

    public void reQueue(ReleaseDetailNotificationTask releaseDetailNotificationTask) {
        log.debug("action.call=reQueue, arguments=[{}]", releaseDetailNotificationTask);
        releaseDetailNotificationTaskRepository.create(releaseDetailNotificationTask);
        log.debug("action.done=reQueue");
    }

    public boolean queueEmpty() {
        log.debug("action.call=queueEmpty");
        final boolean result = releaseDetailNotificationTaskRepository.isEmpty();
        log.debug("action.result=queueEmpty, result=[{}]", result);
        return result;
    }
}
