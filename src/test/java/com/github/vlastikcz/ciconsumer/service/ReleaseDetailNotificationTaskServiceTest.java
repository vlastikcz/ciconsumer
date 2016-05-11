package com.github.vlastikcz.ciconsumer.service;

import java.time.Instant;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;
import com.github.vlastikcz.ciconsumer.domain.repository.ReleaseDetailNotificationTaskRepository;
import com.github.vlastikcz.ciconsumer.service.event.ReleaseDetailNotificationTaskCreateEvent;

import static org.easymock.EasyMock.expectLastCall;

public class ReleaseDetailNotificationTaskServiceTest {

    private ReleaseDetailNotificationTaskRepository releaseDetailNotificationTaskRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService;

    @Before
    public void setup() {
        releaseDetailNotificationTaskRepository = EasyMock.createMock(ReleaseDetailNotificationTaskRepository.class);
        applicationEventPublisher = EasyMock.createMock(ApplicationEventPublisher.class);
        releaseDetailNotificationTaskService = new ReleaseDetailNotificationTaskService(
                releaseDetailNotificationTaskRepository, applicationEventPublisher
        );
    }

    @Test
    public void givenCreate_whenItemWasReceived_thenPublishEvent() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, Collections.emptyList());
        final ReleaseDetailNotificationTaskCreateEvent event = new ReleaseDetailNotificationTaskCreateEvent(releaseDetailNotificationTask);
        applicationEventPublisher.publishEvent(event);
        expectLastCall();
        replay();
        releaseDetailNotificationTaskService.create(releaseDetail);
        verify();
    }

    @Test
    public void givenReQueue_whenItemWasReceived_thenAddItIntoQueueAndDoNotPublishEvent() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, Collections.emptyList());
        releaseDetailNotificationTaskRepository.create(releaseDetailNotificationTask);
        expectLastCall();
        replay();
        releaseDetailNotificationTaskService.reQueue(releaseDetailNotificationTask);
        verify();
    }

    private void replay() {
        EasyMock.replay(releaseDetailNotificationTaskRepository, applicationEventPublisher);
    }

    private void verify() {
        EasyMock.verify(releaseDetailNotificationTaskRepository, applicationEventPublisher);
    }
}