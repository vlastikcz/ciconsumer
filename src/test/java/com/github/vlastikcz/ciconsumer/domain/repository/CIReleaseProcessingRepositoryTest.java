package com.github.vlastikcz.ciconsumer.domain.repository;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.vlastikcz.ciconsumer.UnitTest;
import com.github.vlastikcz.ciconsumer.domain.entity.CIRelease;
import com.github.vlastikcz.ciconsumer.domain.entity.CIReleaseProcessing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CIReleaseProcessingRepositoryTest {
    private CIReleaseProcessingRepository ciReleaseRepository;

    @Before
    public void setUp() {
        ciReleaseRepository = new CIReleaseProcessingRepository();
    }

    @Test
    public void givenPoll_whenRepositoryIsEmpty_ThenReturnEmptyOptional() throws Exception {
        final Optional<CIReleaseProcessing> result = ciReleaseRepository.poll();
        assertFalse(result.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void givenSave_whenNullCIReleaseProcessingReceived_thenThrowNullPointerException() throws Exception {
        final CIReleaseProcessing ciReleaseProcessing = null;
        ciReleaseRepository.save(ciReleaseProcessing);
    }

    @Test(expected = NullPointerException.class)
    public void givenSave_whenNullCIReleaseReceived_thenThrowNullPointerException() throws Exception {
        final CIRelease ciRelease = null;
        ciReleaseRepository.save(ciRelease);
    }

    @Test(expected = NullPointerException.class)
    public void givenSave_whenNullFinishedProcessorsReceived_thenThrowNullPointerException() throws Exception {
        final CIRelease ciRelease = new CIRelease("", Instant.now());
        final CIReleaseProcessing ciReleaseProcessing = new CIReleaseProcessing(ciRelease, null);
        ciReleaseRepository.save(ciReleaseProcessing);
    }

    @Test
    public void givenSave_whenValidEntityReceived_thenSaveItem() throws Exception {
        final CIRelease ciRelease = new CIRelease("givenSave_whenValidEntityReceived_thenSaveItem", Instant.parse("2016-05-03T10:15:30Z"));
        final CIReleaseProcessing ciReleaseProcessing = new CIReleaseProcessing(ciRelease, Collections.emptyList());
        ciReleaseRepository.save(ciRelease);

        final Optional<CIReleaseProcessing> result = ciReleaseRepository.poll();
        assertTrue(result.map(i -> i.equals(ciReleaseProcessing)).orElse(false));
    }

}