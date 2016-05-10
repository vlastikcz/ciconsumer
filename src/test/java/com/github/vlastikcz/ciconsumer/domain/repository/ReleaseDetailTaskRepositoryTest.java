package com.github.vlastikcz.ciconsumer.domain.repository;

import java.time.Instant;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.vlastikcz.ciconsumer.UnitTest;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class ReleaseDetailTaskRepositoryTest {
    private ReleaseDetailNotificationTaskRepository ciReleaseRepository;

    @Before
    public void setUp() {
        ciReleaseRepository = new ReleaseDetailNotificationTaskRepository();
    }

    @Test
    public void givenAsStream_whenRepositoryIsEmpty_ThenReturnEmptyStream() throws Exception {
        final ReleaseDetailNotificationTask result = ciReleaseRepository.poll();
        assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void givenSave_whenNullCIReleaseProcessingReceived_thenThrowNullPointerException() throws Exception {
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = null;
        ciReleaseRepository.create(releaseDetailNotificationTask);
    }

    @Test(expected = NullPointerException.class)
    public void givenSave_whenNullFinishedProcessorsReceived_thenThrowNullPointerException() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("", Instant.now());
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, null);
        ciReleaseRepository.create(releaseDetailNotificationTask);
    }

    @Test
    public void givenSave_whenValidEntityReceived_thenSaveItem() throws Exception {
        final ReleaseDetail releaseDetail = new ReleaseDetail("givenSave_whenValidEntityReceived_thenSaveItem", Instant.parse("2016-05-03T10:15:30Z"));
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = new ReleaseDetailNotificationTask(releaseDetail, Collections.emptyList());
        ciReleaseRepository.create(releaseDetailNotificationTask);

        final ReleaseDetailNotificationTask result = ciReleaseRepository.poll();
        assertTrue(result.equals(releaseDetailNotificationTask));
    }

}