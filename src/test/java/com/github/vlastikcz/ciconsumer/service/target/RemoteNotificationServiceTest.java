package com.github.vlastikcz.ciconsumer.service.target;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.vlastikcz.ciconsumer.UnitTest;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailState;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationState;
import com.github.vlastikcz.ciconsumer.service.ReleaseDetailStateService;
import com.github.vlastikcz.ciconsumer.service.RemoteNotificationService;

@Category(UnitTest.class)
public class RemoteNotificationServiceTest {
    private RemoteNotificationService remoteNotificationService;
    private ReleaseDetailStateService releaseDetailStateService;
    private RemoteNotificationTarget secondaryNotificationTarget;
    private RemoteNotificationTarget primaryNotificationTarget;

    @Before
    public void setup() {
        releaseDetailStateService = EasyMock.createMock(ReleaseDetailStateService.class);
        primaryNotificationTarget = EasyMock.createMock(RemoteNotificationTarget.class);
        secondaryNotificationTarget = EasyMock.createMock(RemoteNotificationTarget.class);
        final List<RemoteNotificationTarget> remoteNotificationTargets = new ArrayList<>();
        remoteNotificationTargets.add(primaryNotificationTarget);
        remoteNotificationTargets.add(secondaryNotificationTarget);
        remoteNotificationService = new RemoteNotificationService(
                releaseDetailStateService, remoteNotificationTargets
        );
    }

    @Test
    public void givenSendRemoteNotifications_whenThereAreNoItemsInQueue_thenDoNothing() throws Exception {
        EasyMock.expect(releaseDetailStateService.asStream()).andReturn(Stream.empty());
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereWasFailureDuringSend_thenRequeueNotification() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, Collections.emptyList());
        final RemoteNotificationState primaryNotificationState = new RemoteNotificationState(primaryNotificationTarget, false);
        final RemoteNotificationState secondaryNotificationState = new RemoteNotificationState(secondaryNotificationTarget, false);
        final List<RemoteNotificationState> remoteNotificationStates = new ArrayList<>();
        remoteNotificationStates.add(primaryNotificationState);
        remoteNotificationStates.add(secondaryNotificationState);
        final ReleaseDetailState updatedReleaseDetailState = new ReleaseDetailState(releaseDetail, remoteNotificationStates);
        EasyMock.expect(releaseDetailStateService.asStream()).andReturn(Stream.of(releaseDetailState));
        EasyMock.expect(primaryNotificationTarget.notify(releaseDetail)).andReturn(false);
        EasyMock.expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(false);
        EasyMock.expectLastCall();
        releaseDetailStateService.update(releaseDetailState, updatedReleaseDetailState);
        replay();
        remoteNotificationService.sendRemoteNotifications();
        verify();
    }

    @Test
    public void givenSendRemoteNotifications_whenThereIsNewItemInQueue_thenSendNotificationsAndDeleteItemFromQueue() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("version", Instant.now());
        final ReleaseDetailState releaseDetailState = new ReleaseDetailState(releaseDetail, Collections.emptyList());
        EasyMock.expect(releaseDetailStateService.asStream()).andReturn(Stream.of(releaseDetailState));
        EasyMock.expect(primaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        EasyMock.expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        EasyMock.expectLastCall();
        releaseDetailStateService.delete(releaseDetailState);
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
        EasyMock.expect(releaseDetailStateService.asStream()).andReturn(Stream.of(releaseDetailState));
        EasyMock.expect(secondaryNotificationTarget.notify(releaseDetail)).andReturn(true);
        EasyMock.expectLastCall();
        releaseDetailStateService.delete(releaseDetailState);
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