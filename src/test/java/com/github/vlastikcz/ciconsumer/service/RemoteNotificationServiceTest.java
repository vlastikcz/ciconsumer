package com.github.vlastikcz.ciconsumer.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.vlastikcz.ciconsumer.UnitTest;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationStatus;
import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

@Category(UnitTest.class)
public class RemoteNotificationServiceTest {
    private RemoteNotificationService remoteNotificationService;
    private ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService;
    private RemoteNotificationTarget secondaryNotificationTarget;
    private RemoteNotificationTarget primaryNotificationTarget;

    @Before
    public void setup() {
        releaseDetailNotificationTaskService = createMock(ReleaseDetailNotificationTaskService.class);
        primaryNotificationTarget = createMock(RemoteNotificationTarget.class);
        secondaryNotificationTarget = createMock(RemoteNotificationTarget.class);
        final List<RemoteNotificationTarget> remoteNotificationTargets = new ArrayList<>();
        remoteNotificationTargets.add(primaryNotificationTarget);
        remoteNotificationTargets.add(secondaryNotificationTarget);
        remoteNotificationService = new RemoteNotificationService(
                releaseDetailNotificationTaskService, remoteNotificationTargets
        );
    }

    @Test
    public void givenSendRemoteNotifications_whenThereAreNoItemsInQueue_thenDoNothing() throws Exception {
        expect(releaseDetailNotificationTaskService.hasNext()).andReturn(false);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }


    @Test
    public void givenSendRemoteNotifications_whenThereAreMultipleItemsInQueue_thenProcessAll() throws Exception {
        final short numberOfItems = 5;
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final RemoteNotificationStatus primaryNotificationState = new RemoteNotificationStatus(primaryNotificationTarget, true);
        final RemoteNotificationStatus secondaryNotificationState = new RemoteNotificationStatus(secondaryNotificationTarget, true);
        final List<RemoteNotificationStatus> remoteNotificationStatuses = new ArrayList<>();
        remoteNotificationStatuses.add(primaryNotificationState);
        remoteNotificationStatuses.add(secondaryNotificationState);
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, remoteNotificationStatuses);
        expect(releaseDetailNotificationTaskService.hasNext()).andReturn(true).times(numberOfItems).andReturn(false);
        expect(releaseDetailNotificationTaskService.poll()).andReturn(releaseDetailNotificationTask).times(numberOfItems);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereWasFailureDuringSend_thenReQueueNotification() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, Collections.emptyList());
        final RemoteNotificationStatus primaryNotificationState = new RemoteNotificationStatus(primaryNotificationTarget, false);
        final RemoteNotificationStatus secondaryNotificationState = new RemoteNotificationStatus(secondaryNotificationTarget, false);
        final List<RemoteNotificationStatus> remoteNotificationStatuses = new ArrayList<>();
        remoteNotificationStatuses.add(primaryNotificationState);
        remoteNotificationStatuses.add(secondaryNotificationState);
        final ReleaseDetailNotificationTask updatedReleaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, remoteNotificationStatuses);
        expect(releaseDetailNotificationTaskService.hasNext()).andReturn(true).once().andReturn(false);
        expect(releaseDetailNotificationTaskService.poll()).andReturn(releaseDetailNotificationTask);
        expect(primaryNotificationTarget.notify(releaseDetail)).andReturn(false);
        expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(false);
        expectLastCall();
        releaseDetailNotificationTaskService.reQueue(updatedReleaseDetailNotificationTask);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereIsNewItemInQueue_thenSendNotificationsAndDeleteItemFromQueue() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, Collections.emptyList());
        expect(releaseDetailNotificationTaskService.hasNext()).andReturn(true).once().andReturn(false);
        expect(releaseDetailNotificationTaskService.poll()).andReturn(releaseDetailNotificationTask);
        expect(primaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereAreOnlySomeUnfinishedItems_thenSendOnlyUnfinishedNotifications() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final RemoteNotificationStatus primaryNotificationState = new RemoteNotificationStatus(primaryNotificationTarget, true);
        final RemoteNotificationStatus secondaryNotificationState = new RemoteNotificationStatus(secondaryNotificationTarget, false);
        final List<RemoteNotificationStatus> remoteNotificationStatuses = new ArrayList<>();
        remoteNotificationStatuses.add(primaryNotificationState);
        remoteNotificationStatuses.add(secondaryNotificationState);
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, remoteNotificationStatuses);
        expect(releaseDetailNotificationTaskService.hasNext()).andReturn(true).once().andReturn(false);
        expect(releaseDetailNotificationTaskService.poll()).andReturn(releaseDetailNotificationTask);
        expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    private void replay() {
        EasyMock.replay(releaseDetailNotificationTaskService, primaryNotificationTarget, secondaryNotificationTarget);
    }

    private void verify() {
        EasyMock.verify(releaseDetailNotificationTaskService, primaryNotificationTarget, secondaryNotificationTarget);
    }
}