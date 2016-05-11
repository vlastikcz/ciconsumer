package com.github.vlastikcz.ciconsumer.service;

import java.util.concurrent.ScheduledFuture;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

public class RemoteNotificationServiceSchedulerTest {
    private TaskScheduler taskScheduler;
    private ApplicationEventPublisher applicationEventPublisher;

    @Before
    public void setup() {
        taskScheduler = EasyMock.createMock(TaskScheduler.class);
        applicationEventPublisher = EasyMock.createMock(ApplicationEventPublisher.class);
    }

    @Test
    public void givenSchedule_whenTheTaskWasNotAlreadyScheduled_thenScheduleTask() throws Exception {
        final RemoteNotificationServiceScheduler scheduler = new RemoteNotificationServiceScheduler(taskScheduler, applicationEventPublisher);
        final ScheduledFuture scheduledFuture = EasyMock.createMock(ScheduledFuture.class);
        EasyMock.expect(taskScheduler.scheduleWithFixedDelay(EasyMock.anyObject(Runnable.class), EasyMock.anyLong())).andReturn(scheduledFuture);
        replay();
        EasyMock.replay(scheduledFuture);
        scheduler.schedule();
        verify();
        EasyMock.verify(scheduledFuture);
    }

    @Test
    public void givenSchedule_whenTheTaskWasAlreadyScheduled_thenDoNotScheduleTaskAgain() throws Exception {
        final RemoteNotificationServiceScheduler scheduler = new RemoteNotificationServiceScheduler(taskScheduler, applicationEventPublisher);
        final ScheduledFuture scheduledFuture = EasyMock.createMock(ScheduledFuture.class);
        EasyMock.expect(taskScheduler.scheduleWithFixedDelay(EasyMock.anyObject(Runnable.class), EasyMock.anyLong())).andReturn(scheduledFuture);
        EasyMock.expect(scheduledFuture.isCancelled()).andReturn(false);
        replay();
        EasyMock.replay(scheduledFuture);
        scheduler.schedule();
        scheduler.schedule();
        verify();
        EasyMock.verify(scheduledFuture);
    }

    @Test
    public void givenSchedule_whenSchedulingWasCanceled_thenScheduleTaskAgain() throws Exception {
        final RemoteNotificationServiceScheduler scheduler = new RemoteNotificationServiceScheduler(taskScheduler, applicationEventPublisher);
        final ScheduledFuture scheduledFuture = EasyMock.createMock(ScheduledFuture.class);
        EasyMock.expect(taskScheduler.scheduleWithFixedDelay(EasyMock.anyObject(Runnable.class), EasyMock.anyLong())).andReturn(scheduledFuture).times(2);
        EasyMock.expect(scheduledFuture.isCancelled()).andReturn(true);
        replay();
        EasyMock.replay(scheduledFuture);
        scheduler.schedule();
        scheduler.schedule();
        verify();
        EasyMock.verify(scheduledFuture);
    }

    @Test
    public void givenUnSchedule_whenTheTaskWasNotScheduled_thenDoNothing() throws Exception {
        final RemoteNotificationServiceScheduler scheduler = new RemoteNotificationServiceScheduler(taskScheduler, applicationEventPublisher);
        replay();
        scheduler.unSchedule();
        verify();
    }

    @Test
    public void givenUnSchedule_whenTheTaskIsScheduled_thenCancelSchedule() throws Exception {
        final RemoteNotificationServiceScheduler scheduler = new RemoteNotificationServiceScheduler(taskScheduler, applicationEventPublisher);
        final ScheduledFuture scheduledFuture = EasyMock.createMock(ScheduledFuture.class);
        EasyMock.expect(taskScheduler.scheduleWithFixedDelay(EasyMock.anyObject(Runnable.class), EasyMock.anyLong())).andReturn(scheduledFuture);
        EasyMock.expect(scheduledFuture.cancel(false)).andReturn(true);
        replay();
        EasyMock.replay(scheduledFuture);
        scheduler.schedule();
        scheduler.unSchedule();
        verify();
        EasyMock.verify(scheduledFuture);
    }

    private void replay() {
        EasyMock.replay(taskScheduler, applicationEventPublisher);
    }

    private void verify() {
        EasyMock.verify(taskScheduler, applicationEventPublisher);
    }
}