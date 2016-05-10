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
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailState;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationState;
import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

@Category(UnitTest.class)
public class RemoteNotificationServiceTest {
    private RemoteNotificationService remoteNotificationService;
    private ReleaseDetailStateService releaseDetailStateService;
    private RemoteNotificationTarget secondaryNotificationTarget;
    private RemoteNotificationTarget primaryNotificationTarget;

    @Before
    public void setup() {
        releaseDetailStateService = createMock(ReleaseDetailStateService.class);
        primaryNotificationTarget = createMock(RemoteNotificationTarget.class);
        secondaryNotificationTarget = createMock(RemoteNotificationTarget.class);
        final List<RemoteNotificationTarget> remoteNotificationTargets = new ArrayList<>();
        remoteNotificationTargets.add(primaryNotificationTarget);
        remoteNotificationTargets.add(secondaryNotificationTarget);
        remoteNotificationService = new RemoteNotificationService(
                releaseDetailStateService, remoteNotificationTargets
        );
    }

    @Test
    public void givenSendRemoteNotifications_whenThereAreNoItemsInQueue_thenDoNothing() throws Exception {
        expect(releaseDetailStateService.hasNext()).andReturn(false);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }


    @Test
    public void givenSendRemoteNotifications_whenThereAreMultipleItemsInQueue_thenProcessAll() throws Exception {
        final short numberOfItems = 5;
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final RemoteNotificationState primaryNotificationState = new RemoteNotificationState(primaryNotificationTarget, true);
        final RemoteNotificationState secondaryNotificationState = new RemoteNotificationState(secondaryNotificationTarget, true);
        final List<RemoteNotificationState> remoteNotificationStates = new ArrayList<>();
        remoteNotificationStates.add(primaryNotificationState);
        remoteNotificationStates.add(secondaryNotificationState);
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, remoteNotificationStates);
        expect(releaseDetailStateService.hasNext()).andReturn(true).times(numberOfItems).andReturn(false);
        expect(releaseDetailStateService.poll()).andReturn(releaseDetailState).times(numberOfItems);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereWasFailureDuringSend_thenReQueueNotification() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, Collections.emptyList());
        final RemoteNotificationState primaryNotificationState = new RemoteNotificationState(primaryNotificationTarget, false);
        final RemoteNotificationState secondaryNotificationState = new RemoteNotificationState(secondaryNotificationTarget, false);
        final List<RemoteNotificationState> remoteNotificationStates = new ArrayList<>();
        remoteNotificationStates.add(primaryNotificationState);
        remoteNotificationStates.add(secondaryNotificationState);
        final ReleaseDetailState updatedReleaseDetailState = new ReleaseDetailState(releaseDetail, remoteNotificationStates);
        expect(releaseDetailStateService.hasNext()).andReturn(true).once().andReturn(false);
        expect(releaseDetailStateService.poll()).andReturn(releaseDetailState);
        expect(primaryNotificationTarget.notify(releaseDetail)).andReturn(false);
        expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(false);
        expectLastCall();
        releaseDetailStateService.reQueue(updatedReleaseDetailState);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereIsNewItemInQueue_thenSendNotificationsAndDeleteItemFromQueue() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, Collections.emptyList());
        expect(releaseDetailStateService.hasNext()).andReturn(true).once().andReturn(false);
        expect(releaseDetailStateService.poll()).andReturn(releaseDetailState);
        expect(primaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereAreOnlySomeUnfinishedItems_thenSendOnlyUnfinishedNotifications() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final RemoteNotificationState primaryNotificationState = new RemoteNotificationState(primaryNotificationTarget, true);
        final RemoteNotificationState secondaryNotificationState = new RemoteNotificationState(secondaryNotificationTarget, false);
        final List<RemoteNotificationState> remoteNotificationStates = new ArrayList<>();
        remoteNotificationStates.add(primaryNotificationState);
        remoteNotificationStates.add(secondaryNotificationState);
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, remoteNotificationStates);
        expect(releaseDetailStateService.hasNext()).andReturn(true).once().andReturn(false);
        expect(releaseDetailStateService.poll()).andReturn(releaseDetailState);
        expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    private void replay() {
        EasyMock.replay(releaseDetailStateService, primaryNotificationTarget, secondaryNotificationTarget);
    }

    private void verify() {
        EasyMock.verify(releaseDetailStateService, primaryNotificationTarget, secondaryNotificationTarget);
    }
}